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
package com.valantic.ide.plugin.pit.linemarker;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.valantic.ide.plugin.pit.action.MutationAction;
import com.valantic.ide.plugin.pit.icons.Icons;
import com.valantic.ide.plugin.pit.services.Services;
import com.valantic.ide.plugin.pit.services.impl.MessageService;
import com.valantic.ide.plugin.pit.services.impl.PsiService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * created by fabian.huesig on 2022-02-01
 */
@SuppressWarnings("unchecked")
public class MutationRunLineMarkerContributor extends RunLineMarkerContributor {

    private static final String EXECUTION_BUNDLE_MESSAGE_KEY = "run.text";

    private String targetClass;
    private String targetTest;

    private MessageService messageService = Services.getService(MessageService.class);
    private PsiService psiService = Services.getService(PsiService.class);


    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement element) {
        final Info[] info = new Info[1];
        Optional.of(element).filter(PsiIdentifier.class::isInstance).map(PsiIdentifier.class::cast)
                .map(PsiIdentifier::getParent).filter(PsiClass.class::isInstance).map(PsiClass.class::cast)
                .filter(psiService::isTestClass).ifPresent(psiClass -> {
                    this.targetTest = psiService.determineTargetTest(psiClass);
                    this.targetClass = psiService.determineTargetClass(this.targetTest, psiClass);
                    info[0] = getInfo(messageService.executionMessage(EXECUTION_BUNDLE_MESSAGE_KEY));
                });
        return info[0];
    }

    protected Info getInfo(final String tooltipProvider) {
        return new Info(Icons.MUTATIONx12, MutationAction.getSingletonActions(targetClass, targetTest), psiElement -> tooltipProvider);
    }
}
