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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JPanel;
import javax.swing.JTextField;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class SettingsEditorServiceTest {

    private SettingsEditorService underTest;

    @Mock
    private EditorTextField editorTextField;
    @Mock
    private ComboBox comboBox;
    @Mock
    private TextFieldWithBrowseButton textFieldWithBrowseButton;

    private MockedStatic<InsertPathAction> insertPathActionMockedStatic;

    @BeforeEach
    void setUp() {
        insertPathActionMockedStatic = mockStatic(InsertPathAction.class);
        underTest = new SettingsEditorService();
    }

    @Test
    void testResetEditorFrom() {
        MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        ArgumentCaptor<FileChooserDescriptor> fileChooserDescriptorArgumentCaptor = ArgumentCaptor.forClass(FileChooserDescriptor.class);
        JTextField textField = mock(JTextField.class);
        MutationSettingsEditor mutationSettingsEditor = mockMutationSettingsEditor();

        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(textFieldWithBrowseButton.getTextField()).thenReturn(textField);

        underTest.resetEditorFrom(mutationSettingsEditor, mutationConfiguration);

        verify(comboBox, times(7)).addItem(Boolean.FALSE.toString());
        verify(comboBox, times(7)).addItem(Boolean.TRUE.toString());
        verify(comboBox, times(1)).addItem(Mutations.DEFAULTS.getValue());
        verify(comboBox, times(1)).addItem(Mutations.ALL.getValue());
        verify(comboBox, times(1)).addItem(Mutations.STRONGER.getValue());
        verify(comboBox, times(1)).addItem(Mutations.OLD_DEFAULTS.getValue());
        verify(comboBox, times(1)).setEditable(Boolean.TRUE);
        verify(comboBox, times(8)).setSelectedItem(any());
        verify(editorTextField, times(23)).setText(null);
        verify(textFieldWithBrowseButton, times(2)).setText(null);
        verify(textFieldWithBrowseButton, times(2)).addBrowseFolderListener(eq(null), eq(null), eq(null), fileChooserDescriptorArgumentCaptor.capture());
        insertPathActionMockedStatic.verify(() -> InsertPathAction.addTo(textField, fileChooserDescriptorArgumentCaptor.getValue()));
    }

    @Test
    void testApplyEditorTo_majorSettings() {
        MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        MutationSettingsEditor mutationSettingsEditor = mockMutationSettingsEditor();
        Object selectedObject = mock(Object.class);

        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(editorTextField.getText()).thenReturn("editorTextFieldText");
        when(textFieldWithBrowseButton.getText()).thenReturn("textFieldWithBrowseButtonText");
        when(comboBox.getSelectedItem()).thenReturn(selectedObject);
        when(selectedObject.toString()).thenReturn("comboBoxText");

        underTest.applyEditorTo(mutationSettingsEditor, mutationConfiguration);

        verify(mutationConfigurationOptions).setTargetClasses(anyString());
        verify(mutationConfigurationOptions).setTargetTests(anyString());
        verify(mutationConfigurationOptions).setReportDir(anyString());
        verify(mutationConfigurationOptions).setSourceDirs(anyString());
        verify(mutationConfigurationOptions).setMutators(anyString());
        verify(mutationConfigurationOptions).setTimeoutConst(anyString());
        verify(mutationConfigurationOptions).setOutputFormats(anyString());
        verify(mutationConfigurationOptions).setTimestampedReports(anyString());
    }

    @Test
    void testApplyEditorTo_advancedSettings() {
        MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        MutationSettingsEditor mutationSettingsEditor = mockMutationSettingsEditor();
        Object selectedObject = mock(Object.class);

        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(editorTextField.getText()).thenReturn("editorTextFieldText");
        when(textFieldWithBrowseButton.getText()).thenReturn("textFieldWithBrowseButtonText");
        when(comboBox.getSelectedItem()).thenReturn(selectedObject);
        when(selectedObject.toString()).thenReturn("comboBoxText");

        underTest.applyEditorTo(mutationSettingsEditor, mutationConfiguration);

        verify(mutationConfigurationOptions).setIncludeLaunchClasspath(anyString());
        verify(mutationConfigurationOptions).setDependencyDistance(anyString());
        verify(mutationConfigurationOptions).setThreads(anyString());
        verify(mutationConfigurationOptions).setExcludedMethods(anyString());
        verify(mutationConfigurationOptions).setExcludedClasses(anyString());
        verify(mutationConfigurationOptions).setExcludedTests(anyString());
        verify(mutationConfigurationOptions).setAvoidCallsTo(anyString());
        verify(mutationConfigurationOptions).setVerbose(anyString());
        verify(mutationConfigurationOptions).setTimeoutFactor(anyString());
        verify(mutationConfigurationOptions).setMaxMutationsPerClass(anyString());
        verify(mutationConfigurationOptions).setJvmArgs(anyString());
        verify(mutationConfigurationOptions).setJvmPath(anyString());
        verify(mutationConfigurationOptions).setFailWhenNoMutations(anyString());
        verify(mutationConfigurationOptions).setMutableCodePaths(anyString());
        verify(mutationConfigurationOptions).setIncludedGroups(anyString());
        verify(mutationConfigurationOptions).setExcludedGroups(anyString());
        verify(mutationConfigurationOptions).setDetectInlinedCode(anyString());
        verify(mutationConfigurationOptions).setMutationThreshold(anyString());
        verify(mutationConfigurationOptions).setCoverageThreshold(anyString());
        verify(mutationConfigurationOptions).setHistoryInputLocation(anyString());
        verify(mutationConfigurationOptions).setHistoryOutputLocation(anyString());
        verify(mutationConfigurationOptions).setSkipFailingTests(anyString());
        verify(mutationConfigurationOptions).setUseClasspathJar(anyString());
        verify(mutationConfigurationOptions).setDeleteCpFile(anyString());
        verify(mutationConfigurationOptions).setClassPathFile(anyString());
    }

    private MutationSettingsEditor mockMutationSettingsEditor() {
        final MutationSettingsEditor mutationSettingsEditor = mock(MutationSettingsEditor.class);

        final JPanel jPanel = mock(JPanel.class);
        final LabeledComponent<EditorTextField> targetClasses = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> targetTests = mock(LabeledComponent.class);
        final LabeledComponent<TextFieldWithBrowseButton> reportDir = mock(LabeledComponent.class);
        final LabeledComponent<TextFieldWithBrowseButton> sourceDirs = mock(LabeledComponent.class);
        final LabeledComponent<ComboBox<String>> mutators = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> timeoutConst = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> outputFormats = mock(LabeledComponent.class);
        final LabeledComponent<ComboBox<String>> timestampedReports = mock(LabeledComponent.class);
        final LabeledComponent<ComboBox<String>> includeLaunchClasspath = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> dependencyDistance = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> threads = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> excludedMethods = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> excludedClasses = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> excludedTests = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> avoidCallsTo = mock(LabeledComponent.class);
        final LabeledComponent<ComboBox<String>> verbose = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> timeoutFactor = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> maxMutationsPerClass = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> jvmArgs = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> jvmPath = mock(LabeledComponent.class);
        final LabeledComponent<ComboBox<String>> failWhenNoMutations = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> mutableCodePaths = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> includedGroups = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> excludedGroups = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> detectInlinedCode = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> mutationThreshold = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> coverageThreshold = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> historyInputLocation = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> historyOutputLocation = mock(LabeledComponent.class);
        final LabeledComponent<ComboBox<String>> skipFailingTests = mock(LabeledComponent.class);
        final LabeledComponent<ComboBox<String>> useClasspathJar = mock(LabeledComponent.class);
        final LabeledComponent<EditorTextField> classpathFile = mock(LabeledComponent.class);
        final LabeledComponent<ComboBox<String>> deleteCpFile = mock(LabeledComponent.class);

        when(targetClasses.getComponent()).thenReturn(editorTextField);
        when(targetTests.getComponent()).thenReturn(editorTextField);
        when(reportDir.getComponent()).thenReturn(textFieldWithBrowseButton);
        when(sourceDirs.getComponent()).thenReturn(textFieldWithBrowseButton);
        when(mutators.getComponent()).thenReturn(comboBox);
        when(timeoutConst.getComponent()).thenReturn(editorTextField);
        when(outputFormats.getComponent()).thenReturn(editorTextField);
        when(timestampedReports.getComponent()).thenReturn(comboBox);
        when(includeLaunchClasspath.getComponent()).thenReturn(comboBox);
        when(dependencyDistance.getComponent()).thenReturn(editorTextField);
        when(threads.getComponent()).thenReturn(editorTextField);
        when(excludedMethods.getComponent()).thenReturn(editorTextField);
        when(excludedClasses.getComponent()).thenReturn(editorTextField);
        when(excludedTests.getComponent()).thenReturn(editorTextField);
        when(avoidCallsTo.getComponent()).thenReturn(editorTextField);
        when(verbose.getComponent()).thenReturn(comboBox);
        when(timeoutFactor.getComponent()).thenReturn(editorTextField);
        when(maxMutationsPerClass.getComponent()).thenReturn(editorTextField);
        when(jvmArgs.getComponent()).thenReturn(editorTextField);
        when(jvmPath.getComponent()).thenReturn(editorTextField);
        when(failWhenNoMutations.getComponent()).thenReturn(comboBox);
        when(mutableCodePaths.getComponent()).thenReturn(editorTextField);
        when(includedGroups.getComponent()).thenReturn(editorTextField);
        when(excludedGroups.getComponent()).thenReturn(editorTextField);
        when(detectInlinedCode.getComponent()).thenReturn(editorTextField);
        when(mutationThreshold.getComponent()).thenReturn(editorTextField);
        when(coverageThreshold.getComponent()).thenReturn(editorTextField);
        when(historyInputLocation.getComponent()).thenReturn(editorTextField);
        when(historyOutputLocation.getComponent()).thenReturn(editorTextField);
        when(skipFailingTests.getComponent()).thenReturn(comboBox);
        when(useClasspathJar.getComponent()).thenReturn(comboBox);
        when(classpathFile.getComponent()).thenReturn(editorTextField);
        when(deleteCpFile.getComponent()).thenReturn(comboBox);

        mutationSettingsEditor.jPanel = jPanel;
        mutationSettingsEditor.targetClasses = targetClasses;
        mutationSettingsEditor.targetTests = targetTests;
        mutationSettingsEditor.reportDir = reportDir;
        mutationSettingsEditor.sourceDirs = sourceDirs;
        mutationSettingsEditor.mutators = mutators;
        mutationSettingsEditor.timeoutConst = timeoutConst;
        mutationSettingsEditor.outputFormats = outputFormats;
        mutationSettingsEditor.timestampedReports = timestampedReports;
        mutationSettingsEditor.includeLaunchClasspath = includeLaunchClasspath;
        mutationSettingsEditor.dependencyDistance = dependencyDistance;
        mutationSettingsEditor.threads = threads;
        mutationSettingsEditor.excludedMethods = excludedMethods;
        mutationSettingsEditor.excludedClasses = excludedClasses;
        mutationSettingsEditor.excludedTests = excludedTests;
        mutationSettingsEditor.avoidCallsTo = avoidCallsTo;
        mutationSettingsEditor.verbose = verbose;
        mutationSettingsEditor.timeoutFactor = timeoutFactor;
        mutationSettingsEditor.maxMutationsPerClass = maxMutationsPerClass;
        mutationSettingsEditor.jvmArgs = jvmArgs;
        mutationSettingsEditor.jvmPath = jvmPath;
        mutationSettingsEditor.failWhenNoMutations = failWhenNoMutations;
        mutationSettingsEditor.mutableCodePaths = mutableCodePaths;
        mutationSettingsEditor.includedGroups = includedGroups;
        mutationSettingsEditor.excludedGroups = excludedGroups;
        mutationSettingsEditor.detectInlinedCode = detectInlinedCode;
        mutationSettingsEditor.mutationThreshold = mutationThreshold;
        mutationSettingsEditor.coverageThreshold = coverageThreshold;
        mutationSettingsEditor.historyInputLocation = historyInputLocation;
        mutationSettingsEditor.historyOutputLocation = historyOutputLocation;
        mutationSettingsEditor.skipFailingTests = skipFailingTests;
        mutationSettingsEditor.useClasspathJar = useClasspathJar;
        mutationSettingsEditor.classpathFile = classpathFile;
        mutationSettingsEditor.deleteCpFile = deleteCpFile;

        return mutationSettingsEditor;
    }

    @AfterEach
    void tearDown() {
        insertPathActionMockedStatic.close();
    }
}
