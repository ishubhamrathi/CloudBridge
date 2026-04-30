plugins {
    `java-library`
    `maven-publish`
}

group = "io.cloudbridge"
version = "0.1.0-SNAPSHOT"

val springBootVersion = "3.3.5"
val springFrameworkVersion = "6.1.14"
val awsSdkVersion = "2.28.24"
val azureServiceBusVersion = "7.17.6"
val gcpPubSubVersion = "1.136.0"
val ociSdkVersion = "3.68.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
    implementation("org.springframework:spring-context:$springFrameworkVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")

    implementation(platform("software.amazon.awssdk:bom:$awsSdkVersion"))
    implementation("software.amazon.awssdk:sqs")
    implementation("software.amazon.awssdk:dynamodb")

    implementation("com.azure:azure-messaging-servicebus:$azureServiceBusVersion")
    implementation("com.google.cloud:google-cloud-pubsub:$gcpPubSubVersion")
    implementation("com.oracle.oci.sdk:oci-java-sdk-queue:$ociSdkVersion")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.12.0")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "cloud-bridge-spring-boot-starter"

            pom {
                name.set("CloudBridge")
                description.set("Cloud-agnostic Spring Boot starter for messaging and basic key-value storage.")
                url.set("https://github.com/your-org/cloudbridge")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("cloudbridge")
                        name.set("CloudBridge Contributors")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/your-org/cloudbridge.git")
                    developerConnection.set("scm:git:ssh://git@github.com:your-org/cloudbridge.git")
                    url.set("https://github.com/your-org/cloudbridge")
                }
            }
        }
    }

    repositories {
        maven {
            name = "localBuildRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
