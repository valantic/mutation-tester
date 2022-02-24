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
package com.valantic.intellij.plugin.mutation.configuration;

import com.intellij.execution.Executor;
import com.intellij.execution.JavaRunConfigurationBase;
import com.intellij.execution.ShortenCommandLine;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.ModuleBasedConfigurationOptions;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.valantic.intellij.plugin.mutation.commandline.MutationCommandLineState;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.editor.MutationSettingsEditor;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.ModuleService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class MutationConfiguration extends JavaRunConfigurationBase {
    private String vmParameters;
    private boolean alternativeJrePathEnabled;
    private String alternativeJrePath;
    private String programParameters;
    private String workingDir;
    private Map<String, String> envs;
    private boolean passParentEnvs;
    private ShortenCommandLine shortenCommandLine;

    private ModuleService moduleService = Services.getService(ModuleService.class);

    public MutationConfiguration(final Project project, final ConfigurationFactory factory, final String name) {
        super(name, new JavaRunConfigurationModule(project, Boolean.TRUE), factory);
    }

    public MutationConfigurationOptions getMutationConfigurationOptions() {
        return Optional.of(getOptions())
                .filter(MutationConfigurationOptions.class::isInstance)
                .map(MutationConfigurationOptions.class::cast)
                .orElse(null);
    }

    @NotNull
    @Override
    protected Class<? extends ModuleBasedConfigurationOptions> getDefaultOptionsClass() {
        return MutationConfigurationOptions.class;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new MutationSettingsEditor();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        return new MutationCommandLineState(executionEnvironment);
    }

    @Override
    public Collection<Module> getValidModules() {
        return moduleService.getModules(getProject());
    }

    @Override
    public String getVMParameters() {
        return vmParameters;
    }

    @Override
    public void setVMParameters(@Nullable String vmParameters) {
        this.vmParameters = vmParameters;
    }

    @Override
    public boolean isAlternativeJrePathEnabled() {
        return alternativeJrePathEnabled;
    }

    @Override
    public void setAlternativeJrePathEnabled(boolean alternativeJrePathEnabled) {
        this.alternativeJrePathEnabled = alternativeJrePathEnabled;
    }

    @Override
    public @Nullable String getAlternativeJrePath() {
        return alternativeJrePath;
    }

    @Override
    public void setAlternativeJrePath(@Nullable String alternativeJrePath) {
        this.alternativeJrePath = alternativeJrePath;
    }

    @Override
    public @Nullable String getProgramParameters() {
        return programParameters;
    }

    @Override
    public void setProgramParameters(@Nullable String programParameters) {
        this.programParameters = programParameters;
    }

    @Override
    public @Nullable String getWorkingDirectory() {
        return workingDir;
    }

    @Override
    public void setWorkingDirectory(@Nullable String workingDir) {
        this.workingDir = workingDir;
    }

    @Override
    public @NotNull Map<String, String> getEnvs() {
        return envs;
    }

    @Override
    public void setEnvs(@NotNull Map<String, String> envs) {
        this.envs = envs;
    }

    @Override
    public boolean isPassParentEnvs() {
        return passParentEnvs;
    }

    @Override
    public void setPassParentEnvs(boolean passParentEnvs) {
        this.passParentEnvs = passParentEnvs;
    }

    @Override
    public @Nullable ShortenCommandLine getShortenCommandLine() {
        return shortenCommandLine;
    }

    @Override
    public void setShortenCommandLine(@Nullable ShortenCommandLine shortenCommandLine) {
        this.shortenCommandLine = shortenCommandLine;
    }

    /**
     * not used
     */
    @Override
    public void checkConfiguration() {
        // empty
    }

    /**
     * not used
     *
     * @return null
     */
    @Override
    public @Nullable String getRunClass() {
        return null;
    }

    /**
     * not used
     *
     * @return null
     */
    @Override
    public @Nullable String getPackage() {
        return null;
    }
}
