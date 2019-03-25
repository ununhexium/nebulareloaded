import com.github.rholder.gradle.task.OneJar
import groovy.xml.dom.DOMCategory.attributes
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "0.0.1-SNAPSHOT"
group = "net.lab0.nebula.reloaded"

buildscript {
  repositories {
    mavenCentral()
    jcenter()
  }
  dependencies {
    classpath("org.junit.platform:junit-platform-gradle-plugin:+")
    classpath("com.github.rholder:gradle-one-jar:1.0.4")
  }
}

plugins {
  val kotlinVersion = "1.2.41"
  idea
  java
  id("org.jetbrains.kotlin.jvm") version kotlinVersion
  id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
}

apply {
  plugin("kotlin-spring")
  plugin("gradle-one-jar")
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
  mavenLocal()
  mavenCentral()
  jcenter()
}

val classifier = "linux-x86_64"

dependencies {
  val assertJVersion = "3.10.0"
  val guavaVersion = "25.1-jre"
  val junitJupiterVersion = "5.2.0"

  compile("com.google.guava:guava:$guavaVersion")
//  compile("com.intellij:darculalaf:0.1")

//  compile("net.lab0.kotlin.more:morekotlin:0.1.2")

  compile("org.funktionale:funktionale-currying:1.2")

  compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  compile("org.jetbrains.kotlin:kotlin-reflect")

  compile("org.slf4j:slf4j-api:1.7.25")
  compile("org.slf4j:slf4j-simple:1.7.25")
  
  val jCudaVersion = "0.9.2"
  compile("org.jcuda:jcuda:0.9.2") {
    isTransitive = false
  }
  compile("org.jcuda", "jcuda-natives", jCudaVersion, classifier = classifier)

//  val lwjglVersion = "3.1.6"
//  compile("org.lwjgl:lwjgl-glfw:$lwjglVersion")
//  compile("org.lwjgl:lwjgl-opencl:$lwjglVersion")
//  compile("org.lwjgl:lwjgl-opencl:$lwjglVersion:native")
//  compile("org.lwjgl:lwjgl-opengl:$lwjglVersion")
//  compile("org.lwjgl:lwjgl-opengl:$lwjglVersion:native")>

//  compile("org.springframework:spring-core")
//  compile("org.springframework.shell:spring-shell-starter:$springShellVersion")

  testImplementation("org.assertj:assertj-core:$assertJVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<Test> {
  systemProperty("java.library.path", "/home/ununhexium/dev/lwjgl/natives")
}


tasks {
  task<Exec>("htmlDeps") {
    dependsOn("htmlDependencyReport")
    val browser = "/usr/bin/sensible-browser"
    commandLine(browser, "build/reports/project/dependencies/index.html")
  }

  val fatJar by creating(OneJar::class.java) {
    mainClass = "net.lab0.nebula.reloaded.ui.Start.java"
  }
}