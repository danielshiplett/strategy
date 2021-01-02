package org.brewman.spring.temporal.config;

import java.lang.annotation.Annotation;

class TemporalRegistrar extends TemporalBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableTemporalWorkflows.class;
    }
}
