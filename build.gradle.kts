plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "io.github.kwaadpepper"
version = "2.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("io.github.kwaadpepper", "serial-int-caster", "2.0.0")

    pom {
        name.set("Serial Int Caster")
        description.set("Encode or decode an integer to or from a string serial")
        inceptionYear.set("2023")
        url.set("https://github.com/Kwaadpepper/serial-int-caster-kotlin")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("kwaadpepper")
                name.set("Jérémy Munsch")
                email.set("github@jeremydev.ovh")
                url.set("https://github.com/Kwaadpepper")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/Kwaadpepper/serial-int-caster-kotlin.git")
            developerConnection.set("scm:git:ssh://git@github.com:Kwaadpepper/serial-int-caster-kotlin.git")
            url.set("https://github.com/Kwaadpepper/serial-int-caster-kotlin")
        }
    }
}
