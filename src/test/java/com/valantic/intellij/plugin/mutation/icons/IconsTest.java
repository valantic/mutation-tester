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
package com.valantic.intellij.plugin.mutation.icons;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class IconsTest {
    @Test
    public void staticIcons() {
        assertNotNull(Icons.MUTATION);
        assertNotNull(Icons.MUTATIONx12);
        assertNotNull(Icons.MUTATIONx13);
        assertNotNull(Icons.MUTATIONx16);
        assertNotNull(Icons.MUTATIONx40);
        assertNotNull(Icons.MUTATION_DISABLED);
        assertNotNull(Icons.MUTATION_DISABLEDx12);
        assertNotNull(Icons.MUTATION_DISABLEDx13);
        assertNotNull(Icons.MUTATION_DISABLEDx16);
        assertNotNull(Icons.MUTATION_DISABLEDx40);
    }

}
