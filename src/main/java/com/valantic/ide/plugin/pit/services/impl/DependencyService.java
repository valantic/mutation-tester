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
 * Written by Fabian Hüsig, June, 2023
 */
package com.valantic.ide.plugin.pit.services.impl;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.Service;
import com.intellij.util.net.HTTPMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * created by fabian.huesig on 2023-06-06
 */
@Service
public final class DependencyService {
    private static final char SEPARATOR = '/';

    public String getArtifact(String repoUrl, String artifact) {
        final Path libPath = Path.of(PathManager.getPluginTempPath());
        final Path artifactPath = libPath.resolve(artifact.substring(artifact.lastIndexOf(SEPARATOR) + 1));

        if (!Files.exists(artifactPath)) {
            final String jarUrl = repoUrl + artifact;
            try {
                URI uri = URI.create(jarUrl);
                if (uri != null) {
                    URL url = uri.toURL();
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(HTTPMethod.GET.name());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Files.createDirectories(libPath);
                        Files.copy(url.openStream(), artifactPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return artifactPath.toString();
    }

}
