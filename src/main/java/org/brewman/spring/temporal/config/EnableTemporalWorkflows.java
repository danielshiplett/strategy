package org.brewman.spring.temporal.config;

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
@Import(TemporalWorkflowRegistrar.class)
public @interface EnableTemporalWorkflows {
    String[] value() default {};

    String[] basePackages() default {};
}
