fun properties(key: String) = project.findProperty(key).toString()
buildscript {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
    }
}

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.springframework.boot") version "3.0.2"
    id("java-library")
    id("idea")
    id("maven-publish")
    id("signing")
}

allprojects {
    group = properties("projectGroup")
    version = properties("projectVersion")

    // Configure project's dependencies
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
    }
}

dependencies {
    // Spring dependencies
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-autoconfigure")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 测试依赖
    compileOnly("com.google.code.gson:gson:2.10.1")
    testImplementation("com.google.code.gson:gson:2.10.1")
}

tasks {
    withType<JavaCompile> {
        doFirst {
            println("当前Java版本为：$sourceCompatibility")
        }
        options.encoding = "UTF-8"
    }
    test {
        useJUnitPlatform()
    }
    bootJar {
        enabled = false
    }
    jar {
        enabled = true
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withJavadocJar()
        withSourcesJar()
    }
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(properties("projectDescription"))
                url.set(properties("projectUrl"))
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.mit.edu/~amini/LICENSE.md")
                    }
                }
                developers {
                    developer {
                        id.set(properties("projectAuthorId"))
                        name.set(properties("projectAuthorName"))
                        email.set(properties("projectAuthorEmail"))
                    }
                }
                scm {
                    connection.set(properties("projectScmUrl"))
                    developerConnection.set(properties("projectScmUrl"))
                    url.set(properties("projectUrl"))
                }
            }
        }
    }
}

signing {
    setRequired {
        !version.toString().endsWith("-SNAPSHOT") && gradle.taskGraph.hasTask("publishToSonatype")
    }
    useInMemoryPgpKeys(
        System.getenv("GPG_KEY_ID"),
        System.getenv("GPG_PRIVATE_KEY"),
        System.getenv("GPG_PRIVATE_KEY_PASSWORD")
    )
    sign(publishing.publications["mavenJava"])
}