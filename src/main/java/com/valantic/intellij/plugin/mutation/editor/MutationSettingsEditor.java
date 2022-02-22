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
package com.valantic.intellij.plugin.mutation.editor;

import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.enums.Mutations;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.InsertPathAction;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class MutationSettingsEditor extends SettingsEditor<MutationConfiguration>
{
	private JPanel jPanel;
	private LabeledComponent<EditorTextField> targetClasses;
	private LabeledComponent<EditorTextField> targetTests;
	private LabeledComponent<TextFieldWithBrowseButton> reportDir;
	private LabeledComponent<TextFieldWithBrowseButton> sourceDirs;
	private LabeledComponent<ComboBox<String>> mutators;

	// advanced
	private LabeledComponent<EditorTextField> timeoutConst;
	private LabeledComponent<EditorTextField> outputFormats;
	private LabeledComponent<ComboBox<String>> timestampedReports;
	private LabeledComponent<ComboBox<String>> includeLaunchClasspath;
	private LabeledComponent<EditorTextField> dependencyDistance;
	private LabeledComponent<EditorTextField> threads;
	private LabeledComponent<EditorTextField> excludedMethods;
	private LabeledComponent<EditorTextField> excludedClasses;
	private LabeledComponent<EditorTextField> excludedTests;
	private LabeledComponent<EditorTextField> avoidCallsTo;
	private LabeledComponent<ComboBox<String>> verbose;
	private LabeledComponent<EditorTextField> timeoutFactor;
	private LabeledComponent<EditorTextField> maxMutationsPerClass;
	private LabeledComponent<EditorTextField> jvmArgs;
	private LabeledComponent<EditorTextField> jvmPath;
	private LabeledComponent<ComboBox<String>> failWhenNoMutations;
	private LabeledComponent<EditorTextField> classPath;
	private LabeledComponent<EditorTextField> mutableCodePaths;
	private LabeledComponent<EditorTextField> testPlugin;
	private LabeledComponent<EditorTextField> includedGroups;
	private LabeledComponent<EditorTextField> excludedGroups;
	private LabeledComponent<EditorTextField> detectInlinedCode;
	private LabeledComponent<EditorTextField> mutationThreshold;
	private LabeledComponent<EditorTextField> coverageThreshold;
	private LabeledComponent<EditorTextField> historyInputLocation;
	private LabeledComponent<EditorTextField> historyOutputLocation;

	@Override
	protected void resetEditorFrom(MutationConfiguration mutationConfiguration)
	{
		Optional.of(mutationConfiguration)
				.map(MutationConfiguration::getPitRunConfigurationOptions)
				.ifPresent(this::resetFields);
	}

	@Override
	protected void applyEditorTo(@NotNull MutationConfiguration mutationConfiguration)
	{
		Optional.of(mutationConfiguration)
				.map(MutationConfiguration::getPitRunConfigurationOptions)
				.ifPresent(this::applyValuesToOptions);
	}

	@NotNull
	@Override
	protected JComponent createEditor()
	{
		return jPanel;
	}

	/**
	 * called by the form to create the neccessary fields and components
	 */
	private void createUIComponents()
	{
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
		classPath = new LabeledComponent<>();
		mutableCodePaths = new LabeledComponent<>();
		testPlugin = new LabeledComponent<>();
		includedGroups = new LabeledComponent<>();
		excludedGroups = new LabeledComponent<>();
		detectInlinedCode = new LabeledComponent<>();
		mutationThreshold = new LabeledComponent<>();
		coverageThreshold = new LabeledComponent<>();
		historyInputLocation = new LabeledComponent<>();
		historyOutputLocation = new LabeledComponent<>();

		setUIComponents();
	}

	/**
	 * called by createUIComponents to set the components
	 */
	private void setUIComponents()
	{
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
		classPath.setComponent(new EditorTextField());
		mutableCodePaths.setComponent(new EditorTextField());
		testPlugin.setComponent(new EditorTextField());
		includedGroups.setComponent(new EditorTextField());
		excludedGroups.setComponent(new EditorTextField());
		detectInlinedCode.setComponent(new EditorTextField());
		mutationThreshold.setComponent(new EditorTextField());
		coverageThreshold.setComponent(new EditorTextField());
		historyInputLocation.setComponent(new EditorTextField());
		historyOutputLocation.setComponent(new EditorTextField());
	}

	/**
	 * applies the values submitted by the form to the options
	 * 
	 * @param options
	 */
	private void applyValuesToOptions(final MutationConfigurationOptions options)
	{
		options.setTargetClasses(targetClasses.getComponent().getText());
		options.setTargetTests(targetTests.getComponent().getText());
		options.setReportDir(reportDir.getComponent().getText());
		options.setSourceDirs(sourceDirs.getComponent().getText());
		options.setMutators(mutators.getComponent().getSelectedItem().toString());

		// advanced
		options.setTimeoutConst(timeoutConst.getComponent().getText());
		options.setOutputFormats(outputFormats.getComponent().getText());
		options.setTimestampedReports(timestampedReports.getComponent().getSelectedItem().toString());
		options.setIncludeLaunchClasspath(includeLaunchClasspath.getComponent().getSelectedItem().toString());
		options.setDependencyDistance(dependencyDistance.getComponent().getText());
		options.setThreads(threads.getComponent().getText());
		options.setExcludedMethods(excludedMethods.getComponent().getText());
		options.setExcludedClasses(excludedClasses.getComponent().getText());
		options.setExcludedTests(excludedTests.getComponent().getText());
		options.setAvoidCallsTo(avoidCallsTo.getComponent().getText());
		options.setVerbose(verbose.getComponent().getSelectedItem().toString());
		options.setTimeoutFactor(timeoutFactor.getComponent().getText());
		options.setMaxMutationsPerClass(maxMutationsPerClass.getComponent().getText());
		options.setJvmArgs(jvmArgs.getComponent().getText());
		options.setJvmPath(jvmPath.getComponent().getText());
		options.setFailWhenNoMutations(failWhenNoMutations.getComponent().getSelectedItem().toString());
		options.setClassPath(classPath.getComponent().getText());
		options.setMutableCodePaths(mutableCodePaths.getComponent().getText());
		options.setTestPlugin(testPlugin.getComponent().getText());
		options.setIncludedGroups(includedGroups.getComponent().getText());
		options.setExcludedGroups(excludedGroups.getComponent().getText());
		options.setDetectInlinedCode(detectInlinedCode.getComponent().getText());
		options.setMutationThreshold(mutationThreshold.getComponent().getText());
		options.setCoverageThreshold(coverageThreshold.getComponent().getText());
		options.setHistoryInputLocation(historyInputLocation.getComponent().getText());
		options.setHistoryOutputLocation(historyOutputLocation.getComponent().getText());
	}

	/**
	 * reset the editor fields to the values from the provided options.
	 *
	 * @param options
	 */
	private void resetFields(final MutationConfigurationOptions options)
	{
		resetTextFieldWithBrowseButton(reportDir, options.getReportDir(), null);
		resetTextFieldWithBrowseButton(sourceDirs, options.getSourceDirs(), null);
		resetTextField(targetClasses, options.getTargetClasses());
		resetTextField(targetTests, options.getTargetTests());
		resetTextField(timeoutConst, options.getTimeoutConst());
		resetTextField(outputFormats, options.getOutputFormats());
		resetTextField(dependencyDistance, options.getDependencyDistance());
		resetTextField(threads, options.getThreads());
		resetTextField(excludedMethods, options.getExcludedMethods());
		resetTextField(excludedClasses, options.getExcludedClasses());
		resetTextField(excludedTests, options.getExcludedTests());
		resetTextField(avoidCallsTo, options.getAvoidCallsTo());
		resetTextField(timeoutFactor, options.getTimeoutFactor());
		resetTextField(maxMutationsPerClass, options.getMaxMutationsPerClass());
		resetTextField(jvmArgs, options.getJvmArgs());
		resetTextField(jvmPath, options.getJvmPath());
		resetTextField(classPath, options.getClassPath());
		resetTextField(mutableCodePaths, options.getMutableCodePaths());
		resetTextField(testPlugin, options.getTestPlugin());
		resetTextField(includedGroups, options.getIncludedGroups());
		resetTextField(excludedGroups, options.getExcludedGroups());
		resetTextField(detectInlinedCode, options.getDetectInlinedCode());
		resetTextField(mutationThreshold, options.getMutationThreshold());
		resetTextField(coverageThreshold, options.getCoverageThreshold());
		resetTextField(historyInputLocation, options.getHistoryInputLocation());
		resetTextField(historyOutputLocation, options.getHistoryOutputLocation());
		resetBooleanComboBox(timestampedReports, options.getTimestampedReports());
		resetBooleanComboBox(includeLaunchClasspath, options.getIncludeLaunchClasspath());
		resetBooleanComboBox(verbose, options.getVerbose());
		resetBooleanComboBox(failWhenNoMutations, options.getFailWhenNoMutations());
		Optional.of(mutators).map(LabeledComponent::getComponent).ifPresent(component -> {
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
	private void resetBooleanComboBox(final LabeledComponent<ComboBox<String>> comboBoxField, final String value)
	{
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
	 * @param allowedSuffix
	 */
	private void resetTextFieldWithBrowseButton(final LabeledComponent<TextFieldWithBrowseButton> textFieldWithBrowseButton, final String text, final String allowedSuffix)
	{
		Optional.of(textFieldWithBrowseButton).map(LabeledComponent::getComponent).ifPresent(component -> {
			component.setText(text);
			addPathListener(component, allowedSuffix);
		});
	}

	/**
	 * reset the editor text fields to the values from the provided options.
	 * 
	 * @param textField
	 * @param text
	 */
	private void resetTextField(final LabeledComponent<EditorTextField> textField, final String text)
	{
		Optional.of(textField).map(LabeledComponent::getComponent).ifPresent(component -> {
			component.setText(text);
		});
	}

	/**
	 * adds path listener to textFieldWithBrowseButton.
	 * If allowed suffix is not null, the path listener allows access to files.
	 * Otherwise only directories are allowed.
	 *
	 * @param textFieldWithBrowseButton
	 * @param allowedSuffix
	 */
	private void addPathListener(final TextFieldWithBrowseButton textFieldWithBrowseButton, final String allowedSuffix)
	{
		FileChooserDescriptor fileChooserDescriptor;
		if (StringUtils.isNotEmpty(allowedSuffix))
		{
			fileChooserDescriptor = new FileChooserDescriptor(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE)
			{
				@Override
				public boolean isFileSelectable(VirtualFile file)
				{
					return file.getName().endsWith(allowedSuffix);
				}
			};
		}
		else
		{
			fileChooserDescriptor = new FileChooserDescriptor(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
		}
		textFieldWithBrowseButton.addBrowseFolderListener(null, null, null, fileChooserDescriptor);
		InsertPathAction.addTo(textFieldWithBrowseButton.getTextField(), fileChooserDescriptor);
	}

}
