import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.5.5" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("jvm") version "1.5.31" apply false
    kotlin("plugin.spring") version "1.5.31" apply false
    kotlin("plugin.jpa") version "1.5.31" apply false
}

group = "es.unizar"
version = "0.0.1-SNAPSHOT"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    repositories {
        mavenCentral()
        jcenter()
        google()
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // https://mvnrepository.com/artifact/com.google.zxing/javase
        "implementation"("com.google.zxing:javase:3.4.0")
        // https://mvnrepository.com/artifact/com.google.zxing/core
        "implementation"("com.google.zxing:core:3.4.0")
        // https://mvnrepository.com/artifact/com.google.zxing/zxing-parent
        "implementation"("com.google.zxing:zxing-parent:3.4.0")
        "implementation"("com.budiyev.android:code-scanner:2.1.0")
        "testImplementation"("org.mockito:mockito-inline:2.13.0")
        "implementation"("io.springfox:springfox-boot-starter:3.0.0")
        //"implementation"("io.springfox:springfox-swagger-ui:3.0.0")
    }
}

project(":core") {
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("org.springframework.boot:spring-boot-starter")
    }
    tasks.getByName<BootJar>("bootJar") {
        enabled = false
    }
}

project(":repositories") {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    dependencies {
        "implementation"(project(":core"))
        "implementation"("org.springframework.boot:spring-boot-starter-data-jpa")
    }
    tasks.getByName<BootJar>("bootJar") {
        enabled = false
    }
}

project(":delivery") {
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    dependencies {
        "implementation"(project(":core"))
        "implementation" ("org.apache.commons:commons-csv:1.5")
        "implementation"("org.springframework.boot:spring-boot-starter-hateoas")
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
        "implementation"("commons-validator:commons-validator:1.6")
        "implementation"("com.google.guava:guava:23.0")
        "implementation"( "com.google.code.gson:gson:2.8.5")
        "implementation"("org.springframework.boot:spring-boot-starter-amqp")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.springframework.amqp:spring-rabbit-test")
        "testImplementation"("org.mockito.kotlin:mockito-kotlin:3.2.0")
    }
    tasks.getByName<BootJar>("bootJar") {
        enabled = false
    }
}

project(":app") {
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    dependencies {
        "implementation"(project(":core"))
        "implementation"(project(":delivery"))
        "implementation"(project(":repositories"))
        "implementation"("org.springframework.boot:spring-boot-starter")
        "implementation"( "org.webjars:bootstrap:3.3.5")
        "implementation"("org.webjars:jquery:2.1.4")
        "runtimeOnly"("org.hsqldb:hsqldb")
        // https://mvnrepository.com/artifact/com.google.zxing/javase
        "implementation"("com.google.zxing:javase:3.4.0")
        // https://mvnrepository.com/artifact/com.google.zxing/core
        "implementation"("com.google.zxing:core:3.4.0")
        // https://mvnrepository.com/artifact/com.google.zxing/zxing-parent
        "implementation"("com.google.zxing:zxing-parent:3.4.0")
        "implementation"("com.budiyev.android:code-scanner:2.1.0")
        "implementation"("org.springframework.boot:spring-boot-starter-amqp")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.springframework.boot:spring-boot-starter-web")
        "testImplementation"("org.springframework.boot:spring-boot-starter-jdbc")
        "testImplementation"("org.mockito.kotlin:mockito-kotlin:3.2.0")
        "testImplementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
        "testImplementation"("org.apache.httpcomponents:httpclient")
    }
}
