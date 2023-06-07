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

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.EditorTextField;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.SettingsEditorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Container;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class MutationSettingsEditorTest {

    private MutationSettingsEditor underTest;

    @Mock
    private SettingsEditorService settingsEditorService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<Disposer> disposerMockedStatic;
    private MockedConstruction<LabeledComponent> labeledComponentMockedConstruction;
    private MockedConstruction<EditorTextField> editorTextFieldMockedConstruction;
    private MockedConstruction<TextFieldWithBrowseButton> textFieldWithBrowseButtonMockedConstruction;
    private MockedConstruction<ComboBox> comboBoxMockedConstruction;
    private MockedConstruction<Container> containerMockedConstruction;
    private MockedConstruction<JScrollPane> jScrollPaneMockedConstruction;
    private MockedConstruction<JTextPane> jTextPaneMockedConstruction;
    private MockedConstruction<MutationSettingsEditor> mutationSettingsEditorMockedConstruction;

    @BeforeEach
    void setUp() {
        final MutationSettingsEditor mutationSettingsEditor = mock(MutationSettingsEditor.class);
        mutationSettingsEditor.jPanel = new JPanel();
        mutationSettingsEditor.settingsEditorService = settingsEditorService;
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(SettingsEditorService.class)).thenReturn(settingsEditorService);
        disposerMockedStatic = mockStatic(Disposer.class);
        editorTextFieldMockedConstruction = mockConstruction(EditorTextField.class);
        labeledComponentMockedConstruction = mockConstruction(LabeledComponent.class);
        textFieldWithBrowseButtonMockedConstruction = mockConstruction(TextFieldWithBrowseButton.class);
        comboBoxMockedConstruction = mockConstruction(ComboBox.class);
        containerMockedConstruction = mockConstruction(Container.class);
        jScrollPaneMockedConstruction = mockConstruction(JScrollPane.class);
        jTextPaneMockedConstruction = mockConstruction(JTextPane.class);
        mutationSettingsEditorMockedConstruction = mockConstruction(MutationSettingsEditor.class);
        underTest = spy(mutationSettingsEditor);
    }

    @Test
    void testResetEditorFrom() {
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);

        underTest.resetEditorFrom(mutationConfiguration);

        verify(settingsEditorService).resetEditorFrom(underTest, mutationConfiguration);
    }

    @Test
    void testApplyEditorTo() {
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);

        underTest.applyEditorTo(mutationConfiguration);

        verify(settingsEditorService).applyEditorTo(underTest, mutationConfiguration);
    }

    @Test
    void testCreateEditor() {
        underTest.createEditor();
        verify(underTest).createEditor();
    }


    @Test
    void testCreateUiComponents_majorSettings() {
        underTest.createUIComponents();

        verify(underTest).setUIComponents();
        verify(underTest.targetClasses).setComponent(any());
        verify(underTest.targetTests).setComponent(any());
        verify(underTest.reportDir).setComponent(any());
        verify(underTest.sourceDirs).setComponent(any());
        verify(underTest.mutators).setComponent(any());
        verify(underTest.timeoutConst).setComponent(any());
        verify(underTest.outputFormats).setComponent(any());
        verify(underTest.timestampedReports).setComponent(any());
    }

    @Test
    void testCreateUiComponents_advancedSettings() {
        underTest.createUIComponents();

        verify(underTest.includeLaunchClasspath).setComponent(any());
        verify(underTest.dependencyDistance).setComponent(any());
        verify(underTest.threads).setComponent(any());
        verify(underTest.excludedMethods).setComponent(any());
        verify(underTest.excludedClasses).setComponent(any());
        verify(underTest.excludedTests).setComponent(any());
        verify(underTest.avoidCallsTo).setComponent(any());
        verify(underTest.verbose).setComponent(any());
        verify(underTest.timeoutFactor).setComponent(any());
        verify(underTest.maxMutationsPerClass).setComponent(any());
        verify(underTest.jvmArgs).setComponent(any());
        verify(underTest.jvmPath).setComponent(any());
        verify(underTest.failWhenNoMutations).setComponent(any());
        verify(underTest.mutableCodePaths).setComponent(any());
        verify(underTest.includedGroups).setComponent(any());
        verify(underTest.excludedGroups).setComponent(any());
        verify(underTest.detectInlinedCode).setComponent(any());
        verify(underTest.mutationThreshold).setComponent(any());
        verify(underTest.coverageThreshold).setComponent(any());
        verify(underTest.historyInputLocation).setComponent(any());
        verify(underTest.historyOutputLocation).setComponent(any());
        verify(underTest.skipFailingTests).setComponent(any());
        verify(underTest.useClasspathJar).setComponent(any());
        verify(underTest.classpathFile).setComponent(any());
        verify(underTest.deleteCpFile).setComponent(any());
    }

    @AfterEach
    void tearDown() throws Exception {
        servicesMockedStatic.close();
        disposerMockedStatic.close();
        labeledComponentMockedConstruction.close();
        editorTextFieldMockedConstruction.close();
        textFieldWithBrowseButtonMockedConstruction.close();
        comboBoxMockedConstruction.close();
        containerMockedConstruction.close();
        jScrollPaneMockedConstruction.close();
        jTextPaneMockedConstruction.close();
        mutationSettingsEditorMockedConstruction.close();
    }
}
