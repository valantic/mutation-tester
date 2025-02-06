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
package com.valantic.intellij.plugin.mutation.services.impl;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.valantic.intellij.plugin.mutation.services.Services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by fabian.huesig on 2023-04-30
 */
@Service
public final class ClassPathService {
    private static final String ECLIPSE_BIN = "/eclipsebin";
    private static final String CLASSES = "/classes";

    private ProjectService projectService = Services.getService(ProjectService.class);

    /**
     * creates the classpath in a List of Strings for all modules of the current project
     *
     * @return List of classpath entries
     */
    public List<String> getClassPathForModules() {
        final List<String> result = Arrays.stream(ModuleManager.getInstance(projectService.getCurrentProject()).getModules())
                .map(this::getClassPathForModule)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        return result;
    }

    /**
     * get classpath entries of given module. also replaces /eclipsebin for /classes directory if those exists
     *
     * @param module
     * @return
     */
    public List<String> getClassPathForModule(final Module module) {
        return ModuleRootManager.getInstance(module).orderEntries().classes().getPathsList().getPathList().stream().map(entry -> {
            if (entry.endsWith(ECLIPSE_BIN)) {
                return entry.replace(ECLIPSE_BIN, CLASSES);
            }
            return entry;
        }).collect(Collectors.toList());
    }

}
