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
package com.valantic.ide.plugin.pit.localization;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * created by fabian.huesig on 2022-02-01
 */
public interface Messages {
    String BASE_PACKAGE = "messages.MessageBundle";

    static String getMessage(final String key) {
        return Optional.ofNullable(key)
                .map(Messages::getValue)
                .orElse(key);
    }

    static String getValue(final String key) {
        try {
            return ResourceBundle.getBundle(BASE_PACKAGE, Locale.ENGLISH).getString(key);
        } catch (MissingResourceException e) {
            // ignore exception
            return key;
        }
    }
}
