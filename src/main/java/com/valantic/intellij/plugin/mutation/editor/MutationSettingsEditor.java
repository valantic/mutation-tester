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
package com.valantic.intellij.plugin.mutation.editor;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.EditorTextField;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.SettingsEditorService;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * created by fabian.huesig on 2022-02-01
 */
@SuppressWarnings({"java:S1104", "java:S3740"})
public class MutationSettingsEditor extends SettingsEditor<MutationConfiguration> {
    protected SettingsEditorService settingsEditorService = Services.getService(SettingsEditorService.class);

    public JPanel jPanel;
    public LabeledComponent<EditorTextField> targetClasses;
    public LabeledComponent<EditorTextField> targetTests;
    public LabeledComponent<TextFieldWithBrowseButton> reportDir;
    public LabeledComponent<TextFieldWithBrowseButton> sourceDirs;
    public LabeledComponent<ComboBox<String>> mutators;

    // advanced
    public LabeledComponent<EditorTextField> timeoutConst;
    public LabeledComponent<EditorTextField> outputFormats;
    public LabeledComponent<ComboBox<String>> timestampedReports;
    public LabeledComponent<ComboBox<String>> includeLaunchClasspath;
    public LabeledComponent<EditorTextField> dependencyDistance;
    public LabeledComponent<EditorTextField> threads;
    public LabeledComponent<EditorTextField> excludedMethods;
    public LabeledComponent<EditorTextField> excludedClasses;
    public LabeledComponent<EditorTextField> excludedTests;
    public LabeledComponent<EditorTextField> avoidCallsTo;
    public LabeledComponent<ComboBox<String>> verbose;
    public LabeledComponent<EditorTextField> timeoutFactor;
    public LabeledComponent<EditorTextField> maxMutationsPerClass;
    public LabeledComponent<EditorTextField> jvmArgs;
    public LabeledComponent<EditorTextField> jvmPath;
    public LabeledComponent<ComboBox<String>> failWhenNoMutations;
    public LabeledComponent<EditorTextField> mutableCodePaths;
    public LabeledComponent<EditorTextField> includedGroups;
    public LabeledComponent<EditorTextField> excludedGroups;
    public LabeledComponent<EditorTextField> detectInlinedCode;
    public LabeledComponent<EditorTextField> mutationThreshold;
    public LabeledComponent<EditorTextField> coverageThreshold;
    public LabeledComponent<EditorTextField> historyInputLocation;
    public LabeledComponent<EditorTextField> historyOutputLocation;
    public LabeledComponent<ComboBox<String>> skipFailingTests;
    public LabeledComponent<EditorTextField> classpathFile;
    public LabeledComponent<ComboBox<String>> useClasspathJar;
    public LabeledComponent<ComboBox<String>> deleteCpFile;

    @Override
    protected void resetEditorFrom(MutationConfiguration mutationConfiguration) {
        settingsEditorService.resetEditorFrom(this, mutationConfiguration);
    }

    @Override
    protected void applyEditorTo(@NotNull MutationConfiguration mutationConfiguration) {
        settingsEditorService.applyEditorTo(this, mutationConfiguration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return jPanel;
    }

    /**
     * called by the form to create the neccessary fields and components
     */
    protected void createUIComponents() {
        targetClasses = new LabeledComponent<>();
        targetTests = new LabeledComponent<>();
        reportDir = new LabeledComponent<>();
        sourceDirs = new LabeledComponent<>();
        mutators = new LabeledComponent<>();

        // advanced
        timeoutConst = new LabeledComponent<>();
        outputFormats = new LabeledComponent<>();
        timestampedReports = new LabeledComponent<>();
        includeLaunchClasspath = new LabeledComponent<>();
        dependencyDistance = new LabeledComponent<>();
        threads = new LabeledComponent<>();
        excludedMethods = new LabeledComponent<>();
        excludedClasses = new LabeledComponent<>();
        excludedTests = new LabeledComponent<>();
        avoidCallsTo = new LabeledComponent<>();
        verbose = new LabeledComponent<>();
        timeoutFactor = new LabeledComponent<>();
        maxMutationsPerClass = new LabeledComponent<>();
        jvmArgs = new LabeledComponent<>();
        jvmPath = new LabeledComponent<>();
        failWhenNoMutations = new LabeledComponent<>();
        mutableCodePaths = new LabeledComponent<>();
        includedGroups = new LabeledComponent<>();
        excludedGroups = new LabeledComponent<>();
        detectInlinedCode = new LabeledComponent<>();
        mutationThreshold = new LabeledComponent<>();
        coverageThreshold = new LabeledComponent<>();
        historyInputLocation = new LabeledComponent<>();
        historyOutputLocation = new LabeledComponent<>();
        skipFailingTests = new LabeledComponent<>();
        classpathFile = new LabeledComponent<>();
        useClasspathJar = new LabeledComponent<>();
        deleteCpFile = new LabeledComponent<>();

        setUIComponents();
    }

    /**
     * called by createUIComponents to set the components
     */
    protected void setUIComponents() {
        targetClasses.setComponent(new EditorTextField());
        targetTests.setComponent(new EditorTextField());
        reportDir.setComponent(new TextFieldWithBrowseButton());
        sourceDirs.setComponent(new TextFieldWithBrowseButton());
        mutators.setComponent(new ComboBox<>());

        // advanced
        timeoutConst.setComponent(new EditorTextField());
        outputFormats.setComponent(new EditorTextField());
        timestampedReports.setComponent(new ComboBox<>());
        includeLaunchClasspath.setComponent(new ComboBox<>());
        dependencyDistance.setComponent(new EditorTextField());
        threads.setComponent(new EditorTextField());
        excludedMethods.setComponent(new EditorTextField());
        excludedClasses.setComponent(new EditorTextField());
        excludedTests.setComponent(new EditorTextField());
        avoidCallsTo.setComponent(new EditorTextField());
        verbose.setComponent(new ComboBox<>());
        timeoutFactor.setComponent(new EditorTextField());
        maxMutationsPerClass.setComponent(new EditorTextField());
        jvmArgs.setComponent(new EditorTextField());
        jvmPath.setComponent(new EditorTextField());
        failWhenNoMutations.setComponent(new ComboBox<>());
        mutableCodePaths.setComponent(new EditorTextField());
        includedGroups.setComponent(new EditorTextField());
        excludedGroups.setComponent(new EditorTextField());
        detectInlinedCode.setComponent(new EditorTextField());
        mutationThreshold.setComponent(new EditorTextField());
        coverageThreshold.setComponent(new EditorTextField());
        historyInputLocation.setComponent(new EditorTextField());
        historyOutputLocation.setComponent(new EditorTextField());
        skipFailingTests.setComponent(new ComboBox());
        classpathFile.setComponent(new EditorTextField());
        useClasspathJar.setComponent(new ComboBox());
        deleteCpFile.setComponent(new ComboBox());
    }

}
