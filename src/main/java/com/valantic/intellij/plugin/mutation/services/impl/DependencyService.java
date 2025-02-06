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
 * Written by Fabian Hüsig, June, 2023
 */
package com.valantic.intellij.plugin.mutation.services.impl;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.extensions.PluginId;

import java.io.File;

/**
 * created by fabian.huesig on 2023-06-06
 */
@Service
public final class DependencyService {

    private static final String PLUGIN_ID = "com.valantic.intellij.plugin.mutation";

    /**
     * get external dependencies which are included with the plugin itself.
     *
     * @return plugin jar file from plugin dependencies
     */
    public File getPluginJar() {
        PluginId pluginId = PluginId.getId(PLUGIN_ID);
        IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
        if (pluginDescriptor != null) {
            File pluginPath = pluginDescriptor.getPath();
            return pluginPath;
        }
        return null;
    }
}
