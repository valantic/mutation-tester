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
package com.valantic.intellij.plugin.mutation.action;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfigurationFactory;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfigurationType;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.constants.MutationConstants;
import com.valantic.intellij.plugin.mutation.icons.Icons;
import com.valantic.intellij.plugin.mutation.localization.Messages;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.PsiService;
import com.valantic.intellij.plugin.mutation.services.impl.UtilService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class MutationAction extends AnAction {

    private static final String PSI_DATA_DIR = "psi.Element.array";
    private static final String PSI_DATA_FILE = "psi.File";
    private static final String PROJECT_VIEW_POPUP = "ProjectViewPopup";

    private String targetClass;
    private String targetTest;

    private PsiService psiService = Services.getService(PsiService.class);
    private UtilService utilService = Services.getService(UtilService.class);

    public MutationAction() {
        super(Messages.getMessage("action.com.valantic.intellij.plugin.mutation.action.MutationAction.text"),
                Messages.getMessage("action.com.valantic.intellij.plugin.mutation.action.MutationAction.description"), Icons.MUTATIONx16);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        if (PROJECT_VIEW_POPUP.equals(event.getPlace())) {
            utilService.allowSlowOperations(() -> {
                final PsiElement psiElement = event.getData(LangDataKeys.PSI_ELEMENT);
                final PsiClass psiClass = Optional.ofNullable(psiElement)
                        .filter(PsiJavaDirectoryImpl.class::isInstance)
                        .map(PsiJavaDirectoryImpl.class::cast)
                        .map(element -> PsiTreeUtil.findChildOfType(element, PsiClass.class))
                        .orElseGet(() -> getPsiClass(psiElement));
                Optional.of(event)
                        .map(AnActionEvent::getPresentation)
                        .ifPresent(presentation -> presentation.setEnabled(psiClass != null && psiService.isTestClass(psiClass)));
            });
        }
    }

    private PsiClass getPsiClass(final PsiElement psiElement) {
        return Optional.ofNullable(psiElement)
                .filter(PsiClass.class::isInstance)
                .map(PsiClass.class::cast)
                .orElse(null);
    }

    public static MutationAction[] getSingletonActions() {
        return getSingletonActions(null, null);
    }

    public static MutationAction[] getSingletonActions(final String targetClass, final String targetTest) {
        MutationAction action = new MutationAction();
        Optional.ofNullable(targetClass)
                .ifPresent(action::setTargetClass);
        Optional.ofNullable(targetTest)
                .ifPresent(action::setTargetTest);
        return new MutationAction[]{action};
    }

    private void setEventTargetTestIfNotExists(final AnActionEvent event) {
        final DataContext dataContext = event.getDataContext();
        final String selectedTargetTest = Optional.ofNullable(dataContext)
                .map(this::getSelectedTestDir)
                .map(psiService::resolvePackageNameForDir)
                .map(packageName -> packageName + MutationConstants.PACKAGE_WILDCARD_SUFFIX)
                .orElseGet(() -> getSelectedFile(dataContext));
        if (StringUtils.isNotEmpty(selectedTargetTest)) {
            this.targetTest = selectedTargetTest;
            if (selectedTargetTest.endsWith(MutationConstants.WILDCARD_SUFFIX)) {
                this.targetClass = selectedTargetTest;
            }
        }
    }

    private String getSelectedFile(final DataContext dataContext) {
        return Optional.of(PSI_DATA_FILE)
                .map(dataContext::getData)
                .filter(PsiJavaFile.class::isInstance)
                .map(PsiJavaFile.class::cast)
                .map(psiJavaFile -> psiJavaFile.getPackageName() + MutationConstants.PACKAGE_SEPARATOR + psiJavaFile.getName().split(MutationConstants.JAVA_FILE_SUFFIX_REGEX)[0])
                .orElse(StringUtils.EMPTY);
    }

    private PsiJavaDirectoryImpl getSelectedTestDir(final DataContext dataContext) {
        return Optional.of(PSI_DATA_DIR)
                .map(dataContext::getData)
                .filter(PsiElement[].class::isInstance)
                .map(PsiElement[].class::cast)
                .map(Arrays::stream)
                .flatMap(Stream::findAny)
                .filter(PsiJavaDirectoryImpl.class::isInstance)
                .map(PsiJavaDirectoryImpl.class::cast)
                .orElse(null);
    }

    @Override
    public void actionPerformed(final @NotNull AnActionEvent event) {
        final Project project = event.getProject();
        setEventTargetTestIfNotExists(event);
        final MutationConfiguration mutationConfiguration = getOrCreateMutationConfiguration(project);
        Optional.of(mutationConfiguration)
                .map(MutationConfiguration::getPitRunConfigurationOptions)
                .ifPresent(options -> {
                    if (StringUtils.isEmpty(options.getSourceDirs())) {
                        options.setSourceDirs(project.getBasePath());
                    }
                    Optional.ofNullable(this.targetTest)
                            .ifPresent(options::setTargetTests);
                    Optional.ofNullable(this.targetClass)
                            .ifPresent(options::setTargetClasses);
                    psiService.updateModule(project, options.getTargetTests(), mutationConfiguration.getConfigurationModule());
                });
        Optional.ofNullable(DefaultRunExecutor.getRunExecutorInstance())
                .map(executor -> ExecutionEnvironmentBuilder.createOrNull(executor, mutationConfiguration))
                .ifPresent(builder -> ExecutionManager.getInstance(project).restartRunProfile(builder.build()));
    }

    /**
     * get or create pitRunConfiguration. Search for Existing mutationConfiguration
     * and if not exists create a new with default values.
     *
     * @param project
     * @return existing or new mutationConfiguration
     */
    private MutationConfiguration getOrCreateMutationConfiguration(final Project project) {
        final Optional<MutationConfiguration> optionalPitRunConfiguration = getOptionalPitConfiguration(project);
        if (optionalPitRunConfiguration.isPresent()) {
            return optionalPitRunConfiguration.get();
        }
        return createPitRunConfiguration(project);
    }

    private Optional<MutationConfiguration> getOptionalPitConfiguration(final Project project) {
        final String targetTestClassName = psiService.getClassName(this.targetTest);
        return Optional.ofNullable(project)
                .map(RunManager::getInstance)
                .map(RunManager::getAllConfigurationsList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(MutationConfiguration.class::isInstance)
                .map(MutationConfiguration.class::cast)
                .filter(mutationConfiguration -> Optional.of(mutationConfiguration)
                        .map(MutationConfiguration::getPitRunConfigurationOptions)
                        .map(MutationConfigurationOptions::getTargetTests)
                        .filter(StringUtils::isNotEmpty)
                        .filter(targetTestClassName::equals)
                        .isPresent())
                .findFirst();
    }

    private MutationConfiguration createPitRunConfiguration(final Project project) {
        final MutationConfigurationFactory factory = getOrCreateConfigurationFactory(project);
        final MutationConfiguration mutationConfiguration = new MutationConfiguration(project, factory, getMutationConfigurationName());
        final RunManager runManager = RunManager.getInstance(project);
        Optional.ofNullable(runManager.createConfiguration(mutationConfiguration, factory))
                .ifPresent(runManager::addConfiguration);
        return mutationConfiguration;
    }

    private String getMutationConfigurationName() {
        return Optional.ofNullable(this.targetTest)
                .filter(targetTestClass -> targetTestClass.endsWith(MutationConstants.PACKAGE_SEPARATOR + MutationConstants.WILDCARD_SUFFIX))
                .map(targetTestClass -> targetTestClass.split(MutationConstants.WILDCARD_SUFFIX_REGEX)[0])
                .orElseGet(() -> psiService.getClassName(this.targetTest));
    }

    private MutationConfigurationFactory getOrCreateConfigurationFactory(final Project project) {
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

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public void setTargetTest(String targetTest) {
        this.targetTest = targetTest;
    }
}
