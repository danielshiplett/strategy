package org.brewman.temporal.autoconfigure;

import org.springframework.beans.factory.support.AbstractBeanDefinition;

public class WorkerBeanDefinition extends AbstractBeanDefinition {

    public WorkerBeanDefinition() {
        super();

    }

    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        return null;
    }

    @Override
    public void setParentName(String parentName) {

    }

    @Override
    public String getParentName() {
        return null;
    }
}
