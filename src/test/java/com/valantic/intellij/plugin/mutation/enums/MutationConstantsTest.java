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
package com.valantic.intellij.plugin.mutation.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class MutationConstantsTest {

    @Test
    void testMutationConstants() {
        assertEquals(".", MutationConstants.PACKAGE_SEPARATOR.getValue());
        assertEquals("/", MutationConstants.PATH_SEPARATOR.getValue());
        assertEquals("/*$", MutationConstants.TRAILING_SLASH_REGEX.getValue());
        assertEquals("\\.java", MutationConstants.JAVA_FILE_SUFFIX_REGEX.getValue());
        assertEquals("\\.\\*", MutationConstants.WILDCARD_SUFFIX_REGEX.getValue());
        assertEquals(".*", MutationConstants.PACKAGE_WILDCARD_SUFFIX.getValue());
        assertEquals("Test", MutationConstants.TEST_CLASS_SUFFIX.getValue());
        assertEquals("*", MutationConstants.WILDCARD_SUFFIX.getValue());
    }

}
