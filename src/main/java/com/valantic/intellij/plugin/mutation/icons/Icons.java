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
package com.valantic.intellij.plugin.mutation.icons;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.Optional;


/**
 * created by fabian.huesig on 2022-02-01
 */
public interface Icons {

    Icon MUTATION = createPNGImageIcon("/icons/mutation/mutation.png");
    Icon MUTATIONx40 = createPNGImageIcon("/icons/mutation/mutationx40.png");
    Icon MUTATIONx16 = createPNGImageIcon("/icons/mutation/mutationx16.png");
    Icon MUTATIONx13 = createPNGImageIcon("/icons/mutation/mutationx13.png");
    Icon MUTATIONx12 = createPNGImageIcon("/icons/mutation/mutationx12.png");

    Icon MUTATION_DISABLED = createPNGImageIcon("/icons/mutation/disabled/mutation-disabled.png");
    Icon MUTATION_DISABLEDx40 = createPNGImageIcon("/icons/mutation/disabled/mutation-disabledx40.png");
    Icon MUTATION_DISABLEDx16 = createPNGImageIcon("/icons/mutation/disabled/mutation-disabledx16.png");
    Icon MUTATION_DISABLEDx13 = createPNGImageIcon("/icons/mutation/disabled/mutation-disabledx13.png");
    Icon MUTATION_DISABLEDx12 = createPNGImageIcon("/icons/mutation/disabled/mutation-disabledx12.png");

    static ImageIcon createPNGImageIcon(String path) {
        return Optional.ofNullable(path)
                .map(Icons.class::getResource)
                .map(ImageIcon::new)
                .orElse(null);
    }
}
