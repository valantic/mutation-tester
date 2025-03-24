/*
 * Copyright [2022] [valantic CEC Schweiz AG]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Written by Fabian Hüsig, February, 2022
 */
package com.valantic.ide.plugin.pit.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import com.valantic.ide.plugin.pit.configuration.option.MutationConfigurationOptions;
import com.valantic.ide.plugin.pit.localization.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class MutationConfigurationFactory extends ConfigurationFactory {

    public MutationConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull String getId() {
        return MutationConfigurationType.ID;
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new MutationConfiguration(project, this, Messages.getMessage("plugin.name"));
    }

    @Nullable
    @Override
    public Class<? extends BaseState> getOptionsClass() {
        return MutationConfigurationOptions.class;
    }

}
