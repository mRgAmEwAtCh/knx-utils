package io.guw.knxutils.semanticanalyzer.semanticmodel.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SemanticIdentifier {
    String[] terms() default {};

    String[] prefixes() default {};

    String[] description() default {};

}
