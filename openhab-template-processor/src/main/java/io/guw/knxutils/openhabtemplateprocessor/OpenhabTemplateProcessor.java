package io.guw.knxutils.openhabtemplateprocessor;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.swing.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;


@Slf4j
@RequiredArgsConstructor
public class OpenhabTemplateProcessor {
    final List<Thing> model;
    private static final String TEMPLATE_PATH_PREFIX = "io/guw/knxutils/openhabtemplateprocessor/templates/";
    private static final String ITEM_TEMPLATES = "items/";
    private static final String THING_TEMPLATES = "things/";

    private final Pattern special = Pattern.compile ("[!@#$%&*()+=|<>?{}\\[\\]~]");

    VelocityEngine velocityEngine  = new VelocityEngine();

    public void generateModel()  {
        log.info("generate openhab files for model...");
        try {
            initVelocityEngine();
            model.forEach(this::processThing);
        } catch (Exception e) {
            log.error("error during model generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processThing(Thing thing){
        Template t = getTemplate(thing);
        log.info("getting template for " + thing.getName() + " Path: " + t.getName());

        VelocityContext context;
        try {
            context = getVelocityContext(thing);
        } catch (OpenhabValidationException e) {
            log.error("validating " + thing.getName() + " failed");
            return;
        }

        StringWriter writer = new StringWriter();
        try {
            t.merge( context, writer );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info(writer.toString());
    }

    private VelocityContext getVelocityContext(Thing thing) throws OpenhabValidationException {
        return switch (thing){
            case DimmableLight item -> addToContext(item);
            case Light item -> addToContext(item);
            case PowerOutlet item -> addToContext(item);
            case Blinds item -> addToContext(item);
            case Shutter item -> addToContext(item);
            default -> new VelocityContext();
        };
    }

    private VelocityContext addToContext(Thing item) throws OpenhabValidationException {
        var context = new VelocityContext();

        if (special.matcher(item.getName()).find()){
            throw new OpenhabValidationException();
        }else {
            context.put("name", item.getName()); // needs to be validated
        }

        context.put("description", item.getPrimarySwitchGroupAddress().getDescription().lines().findFirst().orElse(item.getName()));
        return context;
    }

    private VelocityContext addToContext(Light item) throws OpenhabValidationException {
        var context = addToContext((Thing) item);
        context.put("primarySwitchGroupAddress", item.getPrimarySwitchGroupAddress().getAddress());
        context.put("statusGroupAddress", item.getStatusGroupAddress().getAddress());
        return context;
    }

    private VelocityContext addToContext(PowerOutlet item) throws OpenhabValidationException {
        var context = addToContext((Thing) item);
        context.put("primarySwitchGroupAddress", item.getPrimarySwitchGroupAddress().getAddress());
        context.put("statusGroupAddress", item.getStatusGroupAddress().getAddress());
        return context;
    }

    private VelocityContext addToContext(DimmableLight item) throws OpenhabValidationException {
        var context = addToContext((Light) item);
        context.put("dimGa", item.getDimGa().getAddress());
        context.put("brightnessGa", item.getBrightnessGa().getAddress());
        context.put("brightnessStatusGa", item.getBrightnessStatusGa().getAddress());
        return context;
    }

    private VelocityContext addToContext(Shutter item) throws OpenhabValidationException {
        var context = addToContext((Thing) item);
        context.put("lockGroupAddress", item.getLockGroupAddress().getAddress());
        context.put("stopGroupAddress", item.getStopGroupAddress().getAddress());
        context.put("positionHeightGroupAddress", item.getPositionHeightGroupAddress().getAddress());
        context.put("statusPositionHeightGroupAddress", item.getStatusPositionHeightGroupAddress().getAddress());
        return context;
    }

    private VelocityContext addToContext(Blinds item) throws OpenhabValidationException {
        var context = addToContext((Shutter) item);
        context.put("positionSlateGroupAddress", item.getPositionSlateGroupAddress().getAddress());
        context.put("statusPositionSlateGroupAddress", item.getStatusPositionSlateGroupAddress().getAddress());
        context.put("shadowGroupAddress", item.getShadowGroupAddress().getAddress());
        return context;
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
