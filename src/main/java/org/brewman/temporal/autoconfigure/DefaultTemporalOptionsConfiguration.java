/*
 *  Copyright (c) 2020 Applica.ai All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package org.brewman.temporal.autoconfigure;

import io.temporal.activity.ActivityOptions;
import io.temporal.client.WorkflowClientOptions.Builder;
import io.temporal.client.WorkflowOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(
    type = "org.brewman.temporal.autoconfigure.TemporalOptionsConfiguration")
@Slf4j
public class DefaultTemporalOptionsConfiguration implements TemporalOptionsConfiguration {

  public DefaultTemporalOptionsConfiguration() {
    log.info("Creating Default TemporalOptionsConfiguration");
  }

  @Override
  public Builder modifyClientOptions(Builder builder) {
    return builder;
  }

  @Override
  public WorkflowOptions.Builder modifyDefaultStubOptions(WorkflowOptions.Builder builder) {
    return builder;
  }

  @Override
  public ActivityOptions.Builder modifyDefaultActivityOptions(ActivityOptions.Builder builder) {
    return builder;
  }
}
