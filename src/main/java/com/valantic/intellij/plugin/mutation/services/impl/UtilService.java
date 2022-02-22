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
package com.valantic.intellij.plugin.mutation.services.impl;

import com.intellij.execution.ExecutionBundle;
import com.intellij.openapi.components.Service;
import com.intellij.util.SlowOperations;
import com.intellij.util.ThrowableRunnable;


/**
 * created by fabian.huesig on 2022-02-01
 */
@Service
public final class UtilService {

    @SuppressWarnings("unchecked")
    public void allowSlowOperations(final ThrowableRunnable throwableRunnable) {
        SlowOperations.allowSlowOperations(throwableRunnable);
    }

    public String executionMessage(final String messageKey) {
        return ExecutionBundle.message(messageKey);
    }

}