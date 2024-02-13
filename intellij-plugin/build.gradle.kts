plugins {
    kotlin("jvm") version "1.9.22"
    id("java")
    id("org.jetbrains.intellij") version "1.17.1"
}

group = "fr.unilim"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("fr.unilim:codelinguo.common:1.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

intellij {
    version.set("2023.1.5")
    type.set("IC") // Target IDE Platform
    downloadSources.set(true)
    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("241.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}