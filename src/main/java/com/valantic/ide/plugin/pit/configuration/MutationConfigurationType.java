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
import com.valantic.ide.plugin.pit.icons.Icons;
import com.valantic.ide.plugin.pit.localization.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class MutationConfigurationType implements ConfigurationType {

    public static final String ID = "MutationConfiguration";

    @NotNull
    @Override
    public String getDisplayName() {
        return Messages.getMessage("plugin.name");
    }

    @Override
    public String getConfigurationTypeDescription() {
        return Messages.getMessage("plugin.description");
    }

    @Override
    public Icon getIcon() {
        return Icons.MUTATIONx16;
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new MutationConfigurationFactory(this)};
    }

}
