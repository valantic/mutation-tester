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
 * Written by Fabian Hüsig <fabian.huesig@cec.valantic.com>, February, 2022
 */
package com.valantic.intellij.plugin.mutation.services.impl;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.valantic.intellij.plugin.mutation.services.Services;

import java.util.Arrays;
import java.util.Collection;

/**
 * created by fabian.huesig on 2022-02-01
 */
@Service
public final class ModuleService {

    private ProjectService projectService = Services.getService(ProjectService.class);

    public Collection<Module> getModules(final Project project) {
        return Arrays.asList(getModuleManager(project).getModules());
    }

    public Module findModule(final PsiFile psiFile) {
        return ModuleUtil.findModuleForFile(psiFile);
    }

    public ModuleManager getModuleManager(final Project project) {
        return ModuleManager.getInstance(project);
    }

}
