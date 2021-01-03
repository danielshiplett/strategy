package org.brewman.temporal.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.brewman.temporal.annotations.BaseNameAnnotationConfigurationSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.stream.Stream;

@Slf4j
public abstract class TemporalBeanDefinitionRegistrarSupport implements
        ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware  {

    protected ResourceLoader resourceLoader;
    protected Environment environment;

    protected abstract Class<? extends Annotation> getEnableAnnotation();

    protected abstract void registerBeansIn(
            BeanDefinitionRegistry registry, BaseNameAnnotationConfigurationSource configurationSource);

    protected abstract BaseNameAnnotationConfigurationSource getConfigurationSource(
            AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator generator);

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(@Nonnull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(@Nonnull Environment environment) {
        this.environment = environment;
    }

    @Override
    @Deprecated
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata metadata,
                                        @Nonnull BeanDefinitionRegistry registry) {
        registerBeanDefinitions(metadata, registry, ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR);
    }

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata metadata,
                                        @Nonnull BeanDefinitionRegistry registry,
                                        @Nonnull BeanNameGenerator generator) {
        log.info("registerBeanDefinitions");

        Assert.notNull(metadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        // Guard against calls for sub-classes
        if (metadata.getAnnotationAttributes(getEnableAnnotation().getName()) == null) {
            return;
        }

        BaseNameAnnotationConfigurationSource configurationSource = getConfigurationSource(
                metadata, registry, generator);

        log.info("configurationSource: {}", configurationSource);

        registerBeansIn(registry, configurationSource);
    }

    protected Stream<BeanDefinition> getCandidates(
            Stream<String> basePackages,
            ClassPathScanningCandidateComponentProvider componentProvider) {
        componentProvider.setEnvironment(environment);
        componentProvider.setResourceLoader(resourceLoader);

        return basePackages.flatMap(it -> componentProvider.findCandidateComponents(it).stream());
    }

    protected Class<?> loadClassFromBeanDefinition(BeanDefinition beanDefinition) {
        try {
            Assert.notNull(beanDefinition, "BeanDefinition must not be null");
            Assert.notNull(beanDefinition.getBeanClassName(), "BeanClassName must not be null");
            return ClassUtils.forName(beanDefinition.getBeanClassName(), resourceLoader.getClassLoader());
        } catch (ClassNotFoundException | LinkageError e) {
            log.warn("Could not load type {} with class loader {}", beanDefinition.getBeanClassName(), resourceLoader.getClassLoader());
        }

        return null;
    }
}
