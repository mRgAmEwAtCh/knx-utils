package io.guw.knxutils.openhabtemplateprocessor;

import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Thing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.List;
import java.util.Properties;


@Slf4j
@RequiredArgsConstructor
public class OpenhabTemplateProcessor {
    final List<Thing> model;
    private static final String TEMPLATE_PATH_PREFIX = "io/guw/knxutils/openhabtemplateprocessor/templates/";
    private static final String ITEM_TEMPLATES = "items/";
    private static final String THING_TEMPLATES = "things/";

    VelocityEngine velocityEngine  = new VelocityEngine();

    public void generateModel()  {
        log.info("generate openhab files for model...");
        try {
            initVelocityEngine();

            for (Thing thing: model) {
                Template t = getTemplate(thing);
                log.info("getting template for " + thing.getName() + " Path: " + t.getName());
                VelocityContext context = new VelocityContext();
                context.put("name", thing.getName());
                context.put("description", thing.getPrimarySwitchGroupAddress().getDescription());

                StringWriter writer = new StringWriter();
                t.merge( context, writer );
                log.info(writer.toString());

            }
        } catch (Exception e) {
            log.error("error during model generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initVelocityEngine() throws Exception {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init(p);
    }

    private Template getTemplate(Thing thing) throws Exception {
        return velocityEngine.getTemplate(TEMPLATE_PATH_PREFIX + THING_TEMPLATES + thing.getClass().getSimpleName() + ".vm");
    }
}
