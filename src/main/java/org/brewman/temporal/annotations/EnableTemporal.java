package org.brewman.temporal.annotations;

import org.brewman.temporal.autoconfigure.TemporalRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(TemporalRegistrar.class)
public @interface EnableTemporal {

    String[] workflowBasePackages() default {};

    String[] activityBasePackages() default {};
}
