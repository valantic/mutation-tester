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
package com.valantic.ide.plugin.pit.configuration;

import com.valantic.ide.plugin.pit.icons.Icons;
import com.valantic.ide.plugin.pit.localization.Messages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockStatic;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class MutationConfigurationTypeTest {

    private MutationConfigurationType underTest;

    private MockedStatic<Messages> messagesMockedStatic;

    @BeforeEach
    void setUp() {
        messagesMockedStatic = mockStatic(Messages.class);
        underTest = new MutationConfigurationType();
    }

    @Test
    void testGetDisplayName() {
        messagesMockedStatic.when(() -> Messages.getMessage("plugin.name")).thenReturn("pluginName");

        assertEquals("pluginName", underTest.getDisplayName());

        messagesMockedStatic.verify(() -> Messages.getMessage("plugin.name"));
    }

    @Test
    void testGetConfigurationTypeDescription() {
        messagesMockedStatic.when(() -> Messages.getMessage("plugin.description")).thenReturn("pluginDescription");

        assertEquals("pluginDescription", underTest.getConfigurationTypeDescription());

        messagesMockedStatic.verify(() -> Messages.getMessage("plugin.description"));
    }

    @Test
    void testGetIcon() {
        assertSame(Icons.MUTATIONx16, underTest.getIcon());
    }

    @Test
    void testGetId() {
        assertEquals("MutationConfiguration", underTest.getId());
    }

    @Test
    void testGetConfigurationFactories() {
        assertNotNull(underTest.getConfigurationFactories());
    }

    @AfterEach
    void tearDown() {
        messagesMockedStatic.close();
    }

}
