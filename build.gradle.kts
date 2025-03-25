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
plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("info.solidsoft.pitest") version "1.9.11"
    id("jacoco")
    id("org.sonarqube") version "4.0.0.2929"
}

group = "com.valantic"
version = "2.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    pluginConfiguration.ideaVersion.sinceBuild = "212.4746.92"
    pluginConfiguration.version = project.version.toString()
    buildSearchableOptions = false
    instrumentCode = true
    projectName = project.name
    intellijPlatform.pluginConfiguration.ideaVersion

    publishing {
        token = System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken")
    }
}

pitest {
    timestampedReports.set(false)
    jvmArgs.addAll("--add-opens", "java.base/java.io=ALL-UNNAMED")
    pitestVersion = "1.18.0"
    junit5PluginVersion = "1.2.1"
}

sonarqube {
    properties {
        property("sonar.projectKey", "pit-mutation-tester")
        property("sonar.organization", "fhuesig")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.tests", "src/test")
        property("sonar.scm.disabled", "true")
    }
}

jacoco.toolVersion = "0.8.8"

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.register<Delete>("deleteOldReports") {
    mustRunAfter("pitest")
    delete("samples/pitreport")
}

tasks.register<Copy>("copyPitestReport") {
    mustRunAfter("deleteOldReports")
    from("build/reports/pitest")
    into("samples/pitreport")
}

dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2024.3.1")
        bundledPlugins("com.intellij.java")
        pluginVerifier()
        zipSigner()
    }

    testImplementation("org.pitest:pitest:1.17.4")
    testImplementation("org.pitest:pitest-entry:1.17.4")
    testImplementation("org.pitest:pitest-command-line:1.17.4")
    testImplementation("org.pitest:pitest-junit5-plugin:1.2.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.mockito:mockito-junit-jupiter:5.3.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}
