import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val assertJVersion = "3.10.0"
val junitPlatformVersion = "1.2.0"
val junitJupiterVersion = "5.2.0"
val springShellVersion = "2.0.1.RELEASE"

version = "0.0.1-SNAPSHOT"
group = "net.lab0.nebula.reloaded"

buildscript {
  repositories {
    mavenCentral()
    jcenter()
  }
  dependencies {
    val kotlinVersion = "1.2.51"
    classpath("org.junit.platform:junit-platform-gradle-plugin:+")
  }
}

plugins {
  val kotlinVersion = "1.2.51"
  idea
  java
  id("org.jetbrains.kotlin.jvm") version kotlinVersion
  id("org.springframework.boot") version "2.0.3.RELEASE"
  id("io.spring.dependency-management") version "1.0.5.RELEASE"
  id ("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
}

apply {
  plugin("kotlin-spring")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

idea {
  module {
    isDownloadSources = true
  }
}

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  compile("org.jetbrains.kotlin:kotlin-reflect")

  compile("org.funktionale:funktionale-currying:1.2")

  compile("org.springframework:spring-core")
  compile("org.springframework.shell:spring-shell-starter:$springShellVersion")

  testImplementation("org.assertj:assertj-core:$assertJVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<Jar> {
  configurations["compileClasspath"].forEach { file: File ->
    from(zipTree(file.absoluteFile))
  }
  manifest {
    attributes(
        mapOf(
            "Main-Class" to "net.lab0.shell.Application"
        )
    )
  }
}

tasks {
  task<Exec>("htmlDeps") {
    dependsOn("htmlDependencyReport")
    val browser = "/usr/bin/sensible-browser"
    commandLine(browser, "build/reports/project/dependencies/index.html")
  }
}