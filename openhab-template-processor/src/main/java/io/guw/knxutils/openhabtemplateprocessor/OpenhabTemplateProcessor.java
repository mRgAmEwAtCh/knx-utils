package io.guw.knxutils.openhabtemplateprocessor;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Thing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
public class OpenhabTemplateProcessor {
    final List<Thing> model;
    private static final String TEMPLATE_PATH_PREFIX = "io/guw/knxutils/openhabtemplateprocessor/templates/";
    private static final String ITEM_TEMPLATES = "items/";
    private static final String THING_TEMPLATES = "things/";

    private final Pattern unsupportedNameCharacters = Pattern.compile ("[!@#$%&*()+=|<>,.?{}\\[\\]~]");

    VelocityEngine velocityEngine  = new VelocityEngine();

    public void generateModel()  {
        log.info("generate openhab files for model...");
        try {
            initVelocityEngine();
            Map<String, List<Thing>> result = model.stream().collect(Collectors.groupingBy(thing -> thing.getPrimarySwitchGroupAddress().getGroupAddressRange().getParent().getName()));

            for (Map.Entry<String, List<Thing>> listEntry : result.entrySet()) {
                List<String> items = listEntry.getValue().stream().map(this::processThing).collect(Collectors.toList());
                log.info("\nRoom: " + listEntry.getKey() + "\n" + String.join("\n", items));
            }


        } catch (Exception e) {
            log.error("error during model generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String processThing(Thing thing){
        Template t = getTemplate(thing);
        log.info("getting template for " + thing.getName() + " Path: " + t.getName());

        VelocityContext context;
        try {
            context = getVelocityContext(thing);
        } catch (OpenhabValidationException e) {
            log.error("validating " + thing.getName() + " failed");
            return "";
        }

        StringWriter writer = new StringWriter();
        t.merge( context, writer );
        //log.info(writer.toString());
        return writer.toString();
    }

    private VelocityContext getVelocityContext(Thing thing) throws OpenhabValidationException {
        var context = new VelocityContext();
        setNameAndDescription(context, thing);
        addAllGaToContext(context, thing);
        return context;
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    private void addAllGaToContext(VelocityContext context, Thing item){
        for (Field declaredField : getAllFields(item.getClass())) {
            declaredField.setAccessible(true);
            try {
                if (declaredField.getType() == GroupAddress.class){
                    context.put(declaredField.getName(), ((GroupAddress)declaredField.get(item)).getAddress());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setNameAndDescription(VelocityContext context, Thing item) throws OpenhabValidationException {
        if (unsupportedNameCharacters.matcher(item.getName()).find()){
            throw new OpenhabValidationException();
        }else {
            context.put("name", item.getName()); // needs to be validated
        }

        context.put("description", item.getPrimarySwitchGroupAddress().getDescription().lines().findFirst().orElse(item.getName()));
    }


    private void initVelocityEngine() throws Exception {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init(p);
    }

    private Template getTemplate(Thing thing) {
        try {
            return velocityEngine.getTemplate(TEMPLATE_PATH_PREFIX + THING_TEMPLATES + thing.getClass().getSimpleName() + ".vm");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
