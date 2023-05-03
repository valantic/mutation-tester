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
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.InsertPathAction;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.editor.MutationSettingsEditor;
import com.valantic.intellij.plugin.mutation.enums.Mutations;

import java.util.Optional;

@Service
public final class SettingsEditorService {

    /**
     * reset field values to the values from the provided options.
     *
     * @param mutationSettingsEditor
     * @param mutationConfiguration
     */
    public void resetEditorFrom(final MutationSettingsEditor mutationSettingsEditor, MutationConfiguration mutationConfiguration) {
        Optional.of(mutationConfiguration)
                .map(MutationConfiguration::getMutationConfigurationOptions)
                .ifPresent(mutationConfigurationOptions -> resetFields(mutationSettingsEditor, mutationConfigurationOptions));
    }

    /**
     * apply configuration options from mutationConfigurationOptions
     *
     * @param mutationSettingsEditor
     * @param mutationConfiguration
     */
    public void applyEditorTo(final MutationSettingsEditor mutationSettingsEditor, MutationConfiguration mutationConfiguration) {
        Optional.of(mutationConfiguration)
                .map(MutationConfiguration::getMutationConfigurationOptions)
                .ifPresent(mutationConfigurationOptions -> applyValuesToOptions(mutationSettingsEditor, mutationConfigurationOptions));
    }

    /**
     * reset the editor fields to the values from the provided options.
     *
     * @param options
     */
    private void resetFields(final MutationSettingsEditor mutationSettingsEditor, final MutationConfigurationOptions options) {
        resetTextFieldWithBrowseButton(mutationSettingsEditor.reportDir, options.getReportDir());
        resetTextFieldWithBrowseButton(mutationSettingsEditor.sourceDirs, options.getSourceDirs());
        resetTextField(mutationSettingsEditor.targetClasses, options.getTargetClasses());
        resetTextField(mutationSettingsEditor.targetTests, options.getTargetTests());
        resetTextField(mutationSettingsEditor.timeoutConst, options.getTimeoutConst());
        resetTextField(mutationSettingsEditor.outputFormats, options.getOutputFormats());
        resetTextField(mutationSettingsEditor.dependencyDistance, options.getDependencyDistance());
        resetTextField(mutationSettingsEditor.threads, options.getThreads());
        resetTextField(mutationSettingsEditor.excludedMethods, options.getExcludedMethods());
        resetTextField(mutationSettingsEditor.excludedClasses, options.getExcludedClasses());
        resetTextField(mutationSettingsEditor.excludedTests, options.getExcludedTests());
        resetTextField(mutationSettingsEditor.avoidCallsTo, options.getAvoidCallsTo());
        resetTextField(mutationSettingsEditor.timeoutFactor, options.getTimeoutFactor());
        resetTextField(mutationSettingsEditor.maxMutationsPerClass, options.getMaxMutationsPerClass());
        resetTextField(mutationSettingsEditor.jvmArgs, options.getJvmArgs());
        resetTextField(mutationSettingsEditor.jvmPath, options.getJvmPath());
        resetTextField(mutationSettingsEditor.mutableCodePaths, options.getMutableCodePaths());
        resetTextField(mutationSettingsEditor.includedGroups, options.getIncludedGroups());
        resetTextField(mutationSettingsEditor.excludedGroups, options.getExcludedGroups());
        resetTextField(mutationSettingsEditor.detectInlinedCode, options.getDetectInlinedCode());
        resetTextField(mutationSettingsEditor.mutationThreshold, options.getMutationThreshold());
        resetTextField(mutationSettingsEditor.coverageThreshold, options.getCoverageThreshold());
        resetTextField(mutationSettingsEditor.historyInputLocation, options.getHistoryInputLocation());
        resetTextField(mutationSettingsEditor.historyOutputLocation, options.getHistoryOutputLocation());
        resetTextField(mutationSettingsEditor.classpathFile, options.getClasspathFile());
        resetBooleanComboBox(mutationSettingsEditor.timestampedReports, options.getTimestampedReports());
        resetBooleanComboBox(mutationSettingsEditor.includeLaunchClasspath, options.getIncludeLaunchClasspath());
        resetBooleanComboBox(mutationSettingsEditor.verbose, options.getVerbose());
        resetBooleanComboBox(mutationSettingsEditor.failWhenNoMutations, options.getFailWhenNoMutations());
        resetBooleanComboBox(mutationSettingsEditor.skipFailingTests, options.getSkipFailingTests());
        resetBooleanComboBox(mutationSettingsEditor.useClasspathJar, options.getUseClasspathJar());
        resetBooleanComboBox(mutationSettingsEditor.deleteCpFile, options.getDeleteCpFile());
        Optional.of(mutationSettingsEditor.mutators).map(LabeledComponent::getComponent).ifPresent(component -> {
            component.setEditable(Boolean.TRUE);
            component.setSelectedItem(options.getMutators());
            component.addItem(Mutations.DEFAULTS.getValue());
            component.addItem(Mutations.ALL.getValue());
            component.addItem(Mutations.STRONGER.getValue());
            component.addItem(Mutations.OLD_DEFAULTS.getValue());
        });
    }


    /**
     * reset the editor combobox fields to the values from the provided options. Is only applicable for boolean values
     *
     * @param comboBoxField
     * @param value
     */
    private void resetBooleanComboBox(final LabeledComponent<ComboBox<String>> comboBoxField, final String value) {
        Optional.of(comboBoxField).map(LabeledComponent::getComponent).ifPresent(component -> {
            component.addItem(Boolean.FALSE.toString());
            component.addItem(Boolean.TRUE.toString());
            component.setSelectedItem(value);
        });
    }

    /**
     * reset the editor text field with browse button to the values from the provided options.
     *
     * @param textFieldWithBrowseButton
     * @param text
     */
    private void resetTextFieldWithBrowseButton(final LabeledComponent<TextFieldWithBrowseButton> textFieldWithBrowseButton, final String text) {
        Optional.of(textFieldWithBrowseButton).map(LabeledComponent::getComponent).ifPresent(component -> {
            component.setText(text);
            addPathListener(component);
        });
    }

    /**
     * reset the editor text fields to the values from the provided options.
     *
     * @param textField
     * @param text
     */
    private void resetTextField(final LabeledComponent<EditorTextField> textField, final String text) {
        Optional.of(textField).map(LabeledComponent::getComponent).ifPresent(component -> component.setText(text));
    }

    /**
     * adds path listener to textFieldWithBrowseButton.
     * Only directories are allowed.
     *
     * @param textFieldWithBrowseButton
     */
    private void addPathListener(final TextFieldWithBrowseButton textFieldWithBrowseButton) {
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        textFieldWithBrowseButton.addBrowseFolderListener(null, null, null, fileChooserDescriptor);
        InsertPathAction.addTo(textFieldWithBrowseButton.getTextField(), fileChooserDescriptor);
    }


    /**
     * applies the values submitted by the form to the options
     *
     * @param options
     */
    private void applyValuesToOptions(final MutationSettingsEditor mutationSettingsEditor, final MutationConfigurationOptions options) {
        options.setTargetClasses(mutationSettingsEditor.targetClasses.getComponent().getText());
        options.setTargetTests(mutationSettingsEditor.targetTests.getComponent().getText());
        options.setReportDir(mutationSettingsEditor.reportDir.getComponent().getText());
        options.setSourceDirs(mutationSettingsEditor.sourceDirs.getComponent().getText());
        options.setMutators(mutationSettingsEditor.mutators.getComponent().getSelectedItem().toString());

        // advanced
        options.setTimeoutConst(mutationSettingsEditor.timeoutConst.getComponent().getText());
        options.setOutputFormats(mutationSettingsEditor.outputFormats.getComponent().getText());
        options.setTimestampedReports(mutationSettingsEditor.timestampedReports.getComponent().getSelectedItem().toString());
        options.setIncludeLaunchClasspath(mutationSettingsEditor.includeLaunchClasspath.getComponent().getSelectedItem().toString());
        options.setDependencyDistance(mutationSettingsEditor.dependencyDistance.getComponent().getText());
        options.setThreads(mutationSettingsEditor.threads.getComponent().getText());
        options.setExcludedMethods(mutationSettingsEditor.excludedMethods.getComponent().getText());
        options.setExcludedClasses(mutationSettingsEditor.excludedClasses.getComponent().getText());
        options.setExcludedTests(mutationSettingsEditor.excludedTests.getComponent().getText());
        options.setAvoidCallsTo(mutationSettingsEditor.avoidCallsTo.getComponent().getText());
        options.setVerbose(mutationSettingsEditor.verbose.getComponent().getSelectedItem().toString());
        options.setTimeoutFactor(mutationSettingsEditor.timeoutFactor.getComponent().getText());
        options.setMaxMutationsPerClass(mutationSettingsEditor.maxMutationsPerClass.getComponent().getText());
        options.setJvmArgs(mutationSettingsEditor.jvmArgs.getComponent().getText());
        options.setJvmPath(mutationSettingsEditor.jvmPath.getComponent().getText());
        options.setFailWhenNoMutations(mutationSettingsEditor.failWhenNoMutations.getComponent().getSelectedItem().toString());
        options.setMutableCodePaths(mutationSettingsEditor.mutableCodePaths.getComponent().getText());
        options.setIncludedGroups(mutationSettingsEditor.includedGroups.getComponent().getText());
        options.setExcludedGroups(mutationSettingsEditor.excludedGroups.getComponent().getText());
        options.setDetectInlinedCode(mutationSettingsEditor.detectInlinedCode.getComponent().getText());
        options.setMutationThreshold(mutationSettingsEditor.mutationThreshold.getComponent().getText());
        options.setCoverageThreshold(mutationSettingsEditor.coverageThreshold.getComponent().getText());
        options.setHistoryInputLocation(mutationSettingsEditor.historyInputLocation.getComponent().getText());
        options.setHistoryOutputLocation(mutationSettingsEditor.historyOutputLocation.getComponent().getText());
        options.setSkipFailingTests(mutationSettingsEditor.skipFailingTests.getComponent().getSelectedItem().toString());
        options.setUseClasspathJar(mutationSettingsEditor.useClasspathJar.getComponent().getSelectedItem().toString());
        options.setDeleteCpFile(mutationSettingsEditor.deleteCpFile.getComponent().getSelectedItem().toString());
        options.setClassPathFile(mutationSettingsEditor.classpathFile.getComponent().getText());
    }
}
