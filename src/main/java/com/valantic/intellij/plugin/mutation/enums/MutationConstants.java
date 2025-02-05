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

/**
 * created by fabian.huesig on 2022-02-01
 */
public enum MutationConstants {

    PACKAGE_SEPARATOR("."),
    PATH_SEPARATOR("/"),
    TRAILING_SLASH_REGEX("/*$"),
    JAVA_FILE_SUFFIX_REGEX("\\.java"),
    WILDCARD_SUFFIX_REGEX("\\.\\*"),
    PACKAGE_WILDCARD_SUFFIX(".*"),
    TEST_CLASS_SUFFIX("Test"),
    TEST_CLASS_PREFIX("Test"),
    WILDCARD_SUFFIX("*");

    private String value;

    MutationConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
