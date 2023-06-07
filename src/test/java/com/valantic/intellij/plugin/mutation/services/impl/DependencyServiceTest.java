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

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2023-06-06
 */
@ExtendWith(MockitoExtension.class)
class DependencyServiceTest {

    private DependencyService underTest;

    private MockedStatic<PluginId> pluginIdMockedStatic;
    private MockedStatic<PluginManagerCore> pluginManagerCoreMockedStatic;

    @BeforeEach
    void setUp() {
        pluginIdMockedStatic = mockStatic(PluginId.class);
        pluginManagerCoreMockedStatic = mockStatic(PluginManagerCore.class);
        underTest = new DependencyService();
    }

    @Test
    void testGetThirdPartyDependency_fileFound() {
        final PluginId pluginId = mock(PluginId.class);
        final IdeaPluginDescriptor ideaPluginDescriptor = mock(IdeaPluginDescriptor.class);
        final Path basePath = mock(Path.class);
        final Path libPath = mock(Path.class);
        final File libRootFile = mock(File.class);
        final File libFile1 = mock(File.class);
        final File libFile2 = mock(File.class);

        pluginIdMockedStatic.when(() -> PluginId.findId("com.valantic.intellij.plugin.mutation")).thenReturn(pluginId);
        pluginManagerCoreMockedStatic.when(() -> PluginManagerCore.getPlugin(pluginId)).thenReturn(ideaPluginDescriptor);
        when(ideaPluginDescriptor.getPluginPath()).thenReturn(basePath);
        when(basePath.resolve("lib")).thenReturn(libPath);
        when(libPath.toFile()).thenReturn(libRootFile);
        when(libRootFile.listFiles()).thenReturn(new File[]{libFile1, libFile2});
        when(libFile1.getName()).thenReturn("notTheNameToBeExpected");
        when(libFile2.getName()).thenReturn("pitest-1.0");

        final File result = underTest.getThirdPartyDependency("pitest\\-\\d.*");

        verify(libFile1).getName();
        verify(libFile2).getName();
        assertSame(libFile2, result);
        pluginIdMockedStatic.verify(() -> PluginId.findId("com.valantic.intellij.plugin.mutation"));
        pluginManagerCoreMockedStatic.verify(() -> PluginManagerCore.getPlugin(pluginId));
    }

    @Test
    void testGetThirdPartyDependency_fileNotFound() {
        final PluginId pluginId = mock(PluginId.class);
        final IdeaPluginDescriptor ideaPluginDescriptor = mock(IdeaPluginDescriptor.class);
        final Path basePath = mock(Path.class);
        final Path libPath = mock(Path.class);
        final File libRootFile = mock(File.class);

        pluginIdMockedStatic.when(() -> PluginId.findId("com.valantic.intellij.plugin.mutation")).thenReturn(pluginId);
        pluginManagerCoreMockedStatic.when(() -> PluginManagerCore.getPlugin(pluginId)).thenReturn(ideaPluginDescriptor);
        when(ideaPluginDescriptor.getPluginPath()).thenReturn(basePath);
        when(basePath.resolve("lib")).thenReturn(libPath);
        when(libPath.toFile()).thenReturn(libRootFile);
        when(libRootFile.listFiles()).thenReturn(new File[0]);

        final File result = underTest.getThirdPartyDependency("pitest\\-command\\-\\d.*");

        assertNull(result);
        pluginIdMockedStatic.verify(() -> PluginId.findId("com.valantic.intellij.plugin.mutation"));
        pluginManagerCoreMockedStatic.verify(() -> PluginManagerCore.getPlugin(pluginId));
    }

    @AfterEach
    void tearDown() {
        pluginIdMockedStatic.close();
        pluginManagerCoreMockedStatic.close();
    }
}