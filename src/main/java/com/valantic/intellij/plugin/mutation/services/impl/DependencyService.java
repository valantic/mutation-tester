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
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.extensions.PluginId;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

/**
 * created by fabian.huesig on 2023-06-06
 */
@Service
public final class DependencyService {

    private static final String PLUGIN_ID = "com.valantic.intellij.plugin.mutation";
    private static final String LIB_PATH = "lib";

    /**
     * get external dependencies which are included with the plugin itself, based on the gradle dependencies.
     *
     * @param nameExpression regex expression for file name
     * @return jar file from plugin dependencies
     */
    @Nullable
    public File getThirdPartyDependency(final String nameExpression) {
        return Arrays.stream(Optional.ofNullable(PluginId.findId(PLUGIN_ID))
                        .map(PluginManagerCore::getPlugin)
                        .map(IdeaPluginDescriptor::getPluginPath)
                        .map(path -> path.resolve(LIB_PATH))
                        .map(Path::toFile)
                        .map(File::listFiles)
                        .orElseGet(() -> new File[0]))
                .filter(file -> file.getName().matches(nameExpression))
                .findAny()
                .orElse(null);
    }
}
