package org.brewman.temporal.autoconfigure;

import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

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
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Import(TemporalWorkflowRegistrar.class)
public @interface EnableTemporalWorkflows {
    String[] value() default {};

    String[] basePackages() default {};
}
