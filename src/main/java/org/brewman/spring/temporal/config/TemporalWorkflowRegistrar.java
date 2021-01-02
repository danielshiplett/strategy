package org.brewman.spring.temporal.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;

class TemporalWorkflowRegistrar extends TemporalWorkflowBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableTemporalWorkflows.class;
    }
}
