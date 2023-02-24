package io.guw.knxutils.semanticanalyzer.semanticmodel.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface KnxIdentifier {
    String[] terms() default {};

    String[] prefixes() default {};

    String[] description() default {};

}