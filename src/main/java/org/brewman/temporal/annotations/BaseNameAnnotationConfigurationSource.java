package org.brewman.temporal.annotations;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public abstract class BaseNameAnnotationConfigurationSource {

    private static final String ACTIVITY_BASE_PACKAGES = "activityBasePackages";
    private static final String WORKFLOW_BASE_PACKAGES = "workflowBasePackages";

    private final AnnotationAttributes attributes;

    public BaseNameAnnotationConfigurationSource(AnnotationMetadata metadata, Class<? extends Annotation> annotation,
                                                 ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry,
                                                 @Nullable BeanNameGenerator generator) {
        ClassLoader classLoader = getRequiredClassLoader(resourceLoader);

        Assert.notNull(classLoader, "ClassLoader must not be null!");
        Assert.notNull(metadata, "Metadata must not be null!");
        Assert.notNull(annotation, "Annotation must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotation.getName());

        if (annotationAttributes == null) {
            throw new IllegalStateException(String.format("Unable to obtain annotation attributes for %s!", annotation));
        }

        this.attributes = new AnnotationAttributes(annotationAttributes);
    }

    private static ClassLoader getRequiredClassLoader(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        ClassLoader classLoader = resourceLoader.getClassLoader();

        if (classLoader == null) {
            throw new IllegalArgumentException("Could not obtain ClassLoader from ResourceLoader!");
        }

        return classLoader;
    }

    private static BeanNameGenerator defaultBeanNameGenerator(@Nullable BeanNameGenerator generator) {
        return generator == null || ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR.equals(generator) //
                ? new AnnotationBeanNameGenerator() //
                : generator;
    }

    public Stream<String> getWorkflowBasePackages() {
        return getBasePackages(WORKFLOW_BASE_PACKAGES);
    }

    public Stream<String> getActivityPackages() {
        return getBasePackages(ACTIVITY_BASE_PACKAGES);
    }

    private Stream<String> getBasePackages(String property) {
        return new HashSet<>(Arrays.asList(attributes.getStringArray(property))).stream();
    }
}
