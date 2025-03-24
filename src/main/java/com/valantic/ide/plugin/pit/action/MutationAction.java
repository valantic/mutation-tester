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
package com.valantic.ide.plugin.pit.action;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.valantic.ide.plugin.pit.configuration.MutationConfiguration;
import com.valantic.ide.plugin.pit.enums.MutationConstants;
import com.valantic.ide.plugin.pit.icons.Icons;
import com.valantic.ide.plugin.pit.localization.Messages;
import com.valantic.ide.plugin.pit.services.Services;
import com.valantic.ide.plugin.pit.services.impl.ConfigurationService;
import com.valantic.ide.plugin.pit.services.impl.PsiService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
    private ConfigurationService configurationService = Services.getService(ConfigurationService.class);
    private PsiService psiService = Services.getService(PsiService.class);

    public MutationAction() {
        super(Messages.getMessage("action.com.valantic.intellij.plugin.mutation.action.MutationAction.text"),
                Messages.getMessage("action.com.valantic.intellij.plugin.mutation.action.MutationAction.description"), Icons.MUTATIONx16);
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

    @Override
    public void update(@NotNull AnActionEvent event) {
        if (PROJECT_VIEW_POPUP.equals(event.getPlace())) {
            updatePresentation(event);
        }
    }

    @Override
    public void actionPerformed(final @NotNull AnActionEvent event) {
        final Project project = event.getProject();
        setEventTargetTest(event);
        setEventTargetClass();
        final MutationConfiguration mutationConfiguration = configurationService.getOrCreateMutationConfiguration(project, getTargetTest());
        Optional.of(mutationConfiguration)
                .map(MutationConfiguration::getMutationConfigurationOptions)
                .ifPresent(options -> {
                    if (StringUtils.isEmpty(options.getSourceDirs())) {
                        options.setSourceDirs(project.getBasePath());
                    }
                    Optional.ofNullable(getTargetTest())
                            .ifPresent(options::setTargetTests);
                    Optional.ofNullable(getTargetClass())
                            .ifPresent(options::setTargetClasses);
                });
        Optional.ofNullable(DefaultRunExecutor.getRunExecutorInstance())
                .map(executor -> ExecutionEnvironmentBuilder.createOrNull(executor, mutationConfiguration))
                .ifPresent(builder -> ExecutionManager.getInstance(project).restartRunProfile(builder.build()));
    }

    private void updatePresentation(final AnActionEvent event) {
        final PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);
        ReadAction.run(() -> {
            final PsiClass psiClass = Optional.ofNullable(psiElement)
                    .filter(PsiJavaDirectoryImpl.class::isInstance)
                    .map(PsiJavaDirectoryImpl.class::cast)
                    .map(element -> PsiTreeUtil.findChildOfType(element, PsiClass.class))
                    .orElseGet(() -> Optional.ofNullable(psiElement)
                            .filter(PsiClass.class::isInstance)
                            .map(PsiClass.class::cast)
                            .orElse(null));
            Optional.of(event)
                    .map(AnActionEvent::getPresentation)
                    .ifPresent(presentation -> presentation.setEnabled(psiClass != null && psiService.isTestClass(psiClass)));
        });
    }


    private void setEventTargetTest(final AnActionEvent event) {
        final DataContext dataContext = event.getDataContext();
        this.targetTest = Optional.ofNullable(dataContext)
                .map(this::getSelectedTestDir)
                .map(psiService::resolvePackageNameForDir)
                .map(packageName -> packageName + MutationConstants.PACKAGE_WILDCARD_SUFFIX.getValue())
                .orElseGet(() -> getSelectedFile(dataContext));
    }

    private void setEventTargetClass() {
        if (StringUtils.isNotEmpty(this.targetTest)) {
            if (this.targetTest.endsWith(MutationConstants.WILDCARD_SUFFIX.getValue())) {
                this.targetClass = this.targetTest;
            } else if (psiService.doesClassExists(this.targetTest.split(MutationConstants.TEST_CLASS_SUFFIX.getValue())[0])) {
                this.targetClass = this.targetTest.split(MutationConstants.TEST_CLASS_SUFFIX.getValue())[0];
            } else {
                String[] testPathAndClass = this.targetTest.split(MutationConstants.TEST_CLASS_PREFIX.getValue());

                if (psiService.doesClassExists(StringUtils.join(testPathAndClass))) {
                    this.targetClass = StringUtils.join(testPathAndClass);
                }
            }
        }
    }

    private String getSelectedFile(final DataContext dataContext) {
        return Optional.of(PSI_DATA_FILE)
                .map(DataKey::create)
                .map(dataContext::getData)
                .filter(PsiJavaFile.class::isInstance)
                .map(PsiJavaFile.class::cast)
                .map(psiJavaFile -> psiJavaFile.getPackageName() + MutationConstants.PACKAGE_SEPARATOR.getValue() + psiJavaFile.getName().split(MutationConstants.JAVA_FILE_SUFFIX_REGEX.getValue())[0])
                .orElse(StringUtils.EMPTY);
    }

    private PsiJavaDirectoryImpl getSelectedTestDir(final DataContext dataContext) {
        return Optional.of(PSI_DATA_DIR)
                .map(DataKey::create)
                .map(dataContext::getData)
                .filter(PsiElement[].class::isInstance)
                .map(PsiElement[].class::cast)
                .map(Arrays::stream)
                .flatMap(Stream::findAny)
                .filter(PsiJavaDirectoryImpl.class::isInstance)
                .map(PsiJavaDirectoryImpl.class::cast)
                .orElse(null);
    }

    protected String getTargetClass() {
        return this.targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    protected String getTargetTest() {
        return this.targetTest;
    }

    public void setTargetTest(String targetTest) {
        this.targetTest = targetTest;
    }
}
