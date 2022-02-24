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
import com.intellij.execution.ShortenCommandLine;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.ModuleService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class MutationConfigurationTest {

    private MutationConfiguration underTest;

    @Mock
    private Project project;
    @Mock
    private ModuleService moduleService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<GlobalSearchScopes> globalSearchScopesMockedStatic;
    private MockedStatic<TextConsoleBuilderFactory> textConsoleBuilderFactoryMockedStatic;


    @Before
    public void setUp() {
        final ConfigurationFactory factory = mock(ConfigurationFactory.class);
        final String name = "name";
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ModuleService.class)).thenReturn(moduleService);
        globalSearchScopesMockedStatic = mockStatic(GlobalSearchScopes.class);
        textConsoleBuilderFactoryMockedStatic = mockStatic(TextConsoleBuilderFactory.class);
        underTest = new MutationConfiguration(project, factory, name);
    }

    @Test
    public void testGetMutationConfigurationOptions() {
        assertNotNull(underTest.getMutationConfigurationOptions());
    }

    @Test
    public void testGetDefaultOptionsClass() {
        assertSame(MutationConfigurationOptions.class, underTest.getDefaultOptionsClass());
    }

    @Test
    public void testGetState() {
        final ExecutionEnvironment environment = mock(ExecutionEnvironment.class);
        final Project project = mock(Project.class);
        final RunProfile runProfile = mock(RunProfile.class);
        final TextConsoleBuilderFactory textConsoleBuilderFactory = mock(TextConsoleBuilderFactory.class);
        final GlobalSearchScope searchScope = mock(GlobalSearchScope.class);
        when(environment.getProject()).thenReturn(project);
        when(environment.getRunProfile()).thenReturn(runProfile);
        globalSearchScopesMockedStatic.when(() -> GlobalSearchScopes.executionScope(project, runProfile)).thenReturn(searchScope);
        textConsoleBuilderFactoryMockedStatic.when(() -> TextConsoleBuilderFactory.getInstance()).thenReturn(textConsoleBuilderFactory);
        when(textConsoleBuilderFactory.createBuilder(project, searchScope)).thenReturn(null);

        assertNotNull(underTest.getState(mock(Executor.class), environment));
        globalSearchScopesMockedStatic.verify(() -> GlobalSearchScopes.executionScope(project, runProfile));
        textConsoleBuilderFactoryMockedStatic.verify(() -> TextConsoleBuilderFactory.getInstance());
    }

    @Test
    public void testGetValidModules() {
        Collection<Module> modules = mock(Collection.class);
        when(moduleService.getModules(project)).thenReturn(modules);

        assertSame(modules, underTest.getValidModules());
    }

    @Test
    public void testVMParameters() {
        underTest.setVMParameters("vmParameters");
        assertEquals("vmParameters", underTest.getVMParameters());
    }

    @Test
    public void testAlternativeJrePathEnabled_shouldBeTrue() {
        underTest.setAlternativeJrePathEnabled(true);
        assertEquals(true, underTest.isAlternativeJrePathEnabled());
    }

    @Test
    public void testAlternativeJrePathEnabled_shouldBeFalse() {
        underTest.setAlternativeJrePathEnabled(false);
        assertEquals(false, underTest.isAlternativeJrePathEnabled());
    }

    @Test
    public void testAlternativeJrePath() {
        underTest.setAlternativeJrePath("alternativeJrePath");
        assertEquals("alternativeJrePath", underTest.getAlternativeJrePath());
    }

    @Test
    public void testProgramParameter() {
        underTest.setProgramParameters("programParameters");
        assertEquals("programParameters", underTest.getProgramParameters());
    }

    @Test
    public void testWorkingDirectory() {
        underTest.setWorkingDirectory("workingDir");
        assertEquals("workingDir", underTest.getWorkingDirectory());
    }

    @Test
    public void testEnvs() {
        final Map<String, String> envs = new HashMap<>();
        underTest.setEnvs(envs);
        assertSame(envs, underTest.getEnvs());
    }

    @Test
    public void testPassParentEnvs_shouldBeTrue() {
        underTest.setPassParentEnvs(true);
        assertEquals(true, underTest.isPassParentEnvs());
    }

    @Test
    public void testPassParentEnvs_shouldBeFalse() {
        underTest.setPassParentEnvs(false);
        assertEquals(false, underTest.isPassParentEnvs());
    }

    @Test
    public void testShortenCommandLine() {
        final ShortenCommandLine shortenCommandLine = mock(ShortenCommandLine.class);
        underTest.setShortenCommandLine(shortenCommandLine);
        assertSame(shortenCommandLine, underTest.getShortenCommandLine());
    }

    @Test
    public void testCheckConfiguration() {
        // nothing will happen
        underTest.checkConfiguration();
    }

    @Test
    public void testGetRunClass() {
        assertNull(underTest.getRunClass());
    }

    @Test
    public void testGetPackage() {
        assertNull(underTest.getPackage());
    }

    @After
    public void tearDown() {
        servicesMockedStatic.close();
        globalSearchScopesMockedStatic.close();
        textConsoleBuilderFactoryMockedStatic.close();
    }

}
