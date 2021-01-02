package org.brewman.spring.temporal.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class AnnotationWorkflowConfigurationSource {

    private static final String BASE_PACKAGES = "basePackages";

    private final Environment environment;
    private final WorkflowBeanNameGenerator beanNameGenerator;
    private final BeanDefinitionRegistry registry;

    private final AnnotationMetadata configMetadata;
    private final AnnotationMetadata enableAnnotationMetadata;
    private final AnnotationAttributes attributes;
    private final ResourceLoader resourceLoader;

    public AnnotationWorkflowConfigurationSource(AnnotationMetadata metadata, Class<? extends Annotation> annotation,
                                                   ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry,
                                                   @Nullable BeanNameGenerator generator) {
        ClassLoader classLoader = getRequiredClassLoader(resourceLoader);

        Assert.notNull(environment, "Environment must not be null!");
        Assert.notNull(classLoader, "ClassLoader must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        this.environment = environment;
        this.beanNameGenerator = new WorkflowBeanNameGenerator(classLoader, generator);
        this.registry = registry;

        Assert.notNull(metadata, "Metadata must not be null!");
        Assert.notNull(annotation, "Annotation must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotation.getName());

        if (annotationAttributes == null) {
            throw new IllegalStateException(String.format("Unable to obtain annotation attributes for %s!", annotation));
        }

        this.attributes = new AnnotationAttributes(annotationAttributes);
        this.enableAnnotationMetadata = new StandardAnnotationMetadata(annotation);
        this.configMetadata = metadata;
        this.resourceLoader = resourceLoader;
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

    public Stream<String> getBasePackages() {

        String[] value = attributes.getStringArray("value");
        String[] basePackages = attributes.getStringArray(BASE_PACKAGES);

        // Default configuration - return package of annotated class
        if (value.length == 0 && basePackages.length == 0) {

            String className = configMetadata.getClassName();
            return Stream.of(ClassUtils.getPackageName(className));
        }

        Set<String> packages = new HashSet<>();
        packages.addAll(Arrays.asList(value));
        packages.addAll(Arrays.asList(basePackages));

        return packages.stream();
    }
}
