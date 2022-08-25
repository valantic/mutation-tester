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

import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.services.Services;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * created by fabian.huesig on 2022-02-01
 */
@Service
public final class ModuleService {

    private PsiService psiService = Services.getService(PsiService.class);
    private ProjectService projectService = Services.getService(ProjectService.class);

    public Collection<Module> getModules(final Project project) {
        return Arrays.asList(getModuleManager(project).getModules());
    }

    public Module findModule(final PsiFile psiFile) {
        return ModuleUtilCore.findModuleForFile(psiFile);
    }

    public ModuleManager getModuleManager(final Project project) {
        return ModuleManager.getInstance(project);
    }

    /**
     * get or create configuration module
     *
     * @param mutationConfiguration
     * @param targetTests
     * @return
     */
    public JavaRunConfigurationModule getOrCreateRunConfigurationModule(final MutationConfiguration mutationConfiguration, final String targetTests) {
        JavaRunConfigurationModule module = Optional.ofNullable(mutationConfiguration)
                .map(MutationConfiguration::getConfigurationModule)
                .orElseGet(this::createJavaRunConfigurationModule);
        updateModule(module, targetTests);
        return module;
    }

    protected JavaRunConfigurationModule createJavaRunConfigurationModule() {
        return new JavaRunConfigurationModule(projectService.getCurrentProject(), true);
    }

    /**
     * updates the module needed for java command line state.
     * This can change in a multi module project depending of the used module.
     * Determines the correct moule based by package of the test
     */
    private void updateModule(final JavaRunConfigurationModule configurationModule, final String targetTests) {
        Module module = Optional.ofNullable(targetTests)
                .map(psiService::getPsiFile)
                .map(this::findModule)
                .orElseGet(this::getFallbackModule);
        configurationModule.setModule(module);
        configurationModule.setModuleName(module.getName());
    }

    /**
     * if no module can be found try with random fallback module in this project.
     *
     * @return
     */
    private Module getFallbackModule() {
        return Optional.ofNullable(projectService.getCurrentProject())
                .map(this::getModules)
                .orElse(Collections.emptyList())
                .stream()
                .filter(fallbackModule -> StringUtils.isNotEmpty(fallbackModule.getName()))
                .findFirst()
                .orElse(null);
    }

}
