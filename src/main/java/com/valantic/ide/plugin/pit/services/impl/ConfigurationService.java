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
package com.valantic.ide.plugin.pit.services.impl;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.valantic.ide.plugin.pit.configuration.MutationConfiguration;
import com.valantic.ide.plugin.pit.configuration.MutationConfigurationFactory;
import com.valantic.ide.plugin.pit.configuration.MutationConfigurationType;
import com.valantic.ide.plugin.pit.configuration.option.MutationConfigurationOptions;
import com.valantic.ide.plugin.pit.enums.MutationConstants;
import com.valantic.ide.plugin.pit.exception.MutationConfigurationException;
import com.valantic.ide.plugin.pit.services.Services;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Optional;

/**
 * created by fabian.huesig on 2022-02-01
 */
@Service
public final class ConfigurationService {

    private PsiService psiService = Services.getService(PsiService.class);

    /**
     * get or create mutationRunConfiguration. Search for Existing mutationConfiguration
     * and if not exists create a new with default values.
     *
     * @param project     used project
     * @param targetTests targetTests
     * @return existing or new mutationConfiguration
     */
    public MutationConfiguration getOrCreateMutationConfiguration(final Project project, final String targetTests) {
        final String targetTestClassName = psiService.getClassName(targetTests);
        return Optional.ofNullable(project)
                .map(RunManager::getInstance)
                .map(RunManager::getAllConfigurationsList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(MutationConfiguration.class::isInstance)
                .map(MutationConfiguration.class::cast)
                .filter(mutationConfiguration -> Optional.of(mutationConfiguration)
                        .map(MutationConfiguration::getMutationConfigurationOptions)
                        .map(MutationConfigurationOptions::getTargetTests)
                        .filter(StringUtils::isNotEmpty)
                        .filter(targetTestClassName::equals)
                        .isPresent())
                .findFirst()
                .orElseGet(() -> createMutationConfiguration(project, targetTests));
    }

    /**
     * create a new MutationConfiguration
     *
     * @param project
     * @param targetTests
     * @return
     */
    private MutationConfiguration createMutationConfiguration(final Project project, final String targetTests) {
        final MutationConfigurationFactory factory = getOrCreateMutationConfigurationFactory(project);
        final MutationConfiguration mutationConfiguration = createNewMutationConfiguration(project, factory, getMutationRunConfigurationName(targetTests));
        final RunManager runManager = RunManager.getInstance(project);
        Optional.ofNullable(runManager.createConfiguration(mutationConfiguration, factory))
                .ifPresent(runManager::addConfiguration);
        return mutationConfiguration;
    }

    /**
     * get existing MutationConfigurationFactory if one exists,
     * otherwise creates a new MutationConfigurationFactory
     *
     * @param project
     * @return
     */
    private MutationConfigurationFactory getOrCreateMutationConfigurationFactory(final Project project) {
        return Optional.ofNullable(project)
                .map(RunManager::getInstance)
                .map(RunManager::getAllConfigurationsList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(MutationConfiguration.class::isInstance)
                .map(MutationConfiguration.class::cast)
                .map(MutationConfiguration::getFactory)
                .filter(MutationConfigurationFactory.class::isInstance)
                .map(MutationConfigurationFactory.class::cast)
                .findFirst()
                .orElseGet(() -> {
                    final MutationConfigurationType configurationType = new MutationConfigurationType();
                    return new MutationConfigurationFactory(configurationType);
                });
    }

    /**
     * determines the name of the mutation run configuration
     *
     * @param name
     * @return
     */
    private String getMutationRunConfigurationName(final String name) {
        return Optional.ofNullable(name)
                .filter(targetTestClass -> targetTestClass.endsWith(MutationConstants.PACKAGE_SEPARATOR.getValue() + MutationConstants.WILDCARD_SUFFIX.getValue()))
                .map(targetTestClass -> targetTestClass.split(MutationConstants.WILDCARD_SUFFIX_REGEX.getValue())[0])
                .orElseGet(() -> psiService.getClassName(name));
    }

    /**
     * create MutationConfiguration based on template
     *
     * @param project
     * @param factory
     * @param configurationName
     * @return
     */
    protected MutationConfiguration createNewMutationConfiguration(final Project project, final MutationConfigurationFactory factory, final String configurationName) {
        return Optional.of(RunManager.getInstance(project))
                .map(runManager -> runManager.getConfigurationTemplate(factory))
                .map(RunnerAndConfigurationSettings::getConfiguration)
                .map(runConfiguration -> factory.createConfiguration(configurationName, runConfiguration))
                .filter(MutationConfiguration.class::isInstance)
                .map(MutationConfiguration.class::cast)
                .orElseThrow(() -> new MutationConfigurationException("Could not create mutation configuration"));
    }
}
