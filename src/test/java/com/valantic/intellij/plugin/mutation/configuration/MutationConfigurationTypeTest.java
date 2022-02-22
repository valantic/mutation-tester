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
package com.valantic.intellij.plugin.mutation.configuration;

import com.valantic.intellij.plugin.mutation.icons.Icons;
import com.valantic.intellij.plugin.mutation.localization.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mockStatic;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class MutationConfigurationTypeTest {

    private MutationConfigurationType underTest;

    private MockedStatic<Messages> messagesMockedStatic;

    @Before
    public void setUp() {
        messagesMockedStatic = mockStatic(Messages.class);
        underTest = new MutationConfigurationType();
    }

    @Test
    public void testGetDisplayName() {
        messagesMockedStatic.when(() -> Messages.getMessage("plugin.name")).thenReturn("pluginName");

        assertEquals("pluginName", underTest.getDisplayName());

        messagesMockedStatic.verify(() -> Messages.getMessage("plugin.name"));
    }

    @Test
    public void testGetConfigurationTypeDescription() {
        messagesMockedStatic.when(() -> Messages.getMessage("plugin.description")).thenReturn("pluginDescription");

        assertEquals("pluginDescription", underTest.getConfigurationTypeDescription());

        messagesMockedStatic.verify(() -> Messages.getMessage("plugin.description"));
    }

    @Test
    public void testGetIcon() {
        assertSame(Icons.MUTATIONx16, underTest.getIcon());
    }

    @Test
    public void testGetId() {
        assertEquals("MutationConfiguration", underTest.getId());
    }

    @Test
    public void testGetConfigurationFactories() {
        assertNotNull(underTest.getConfigurationFactories());
    }

    @After
    public void tearDown() {
        messagesMockedStatic.close();
    }

}
