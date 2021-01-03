package org.brewman.temporal.annotations;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;

public class TemporalAnnotationConfigurationSource extends BaseNameAnnotationConfigurationSource {

    public TemporalAnnotationConfigurationSource(AnnotationMetadata metadata, Class<? extends Annotation> annotation, ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry, BeanNameGenerator generator) {
        super(metadata, annotation, resourceLoader, environment, registry, generator);
    }
}
