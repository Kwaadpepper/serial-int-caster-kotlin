plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    id("java-library")
    id("maven-publish")
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
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
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.kwaadpepper"
            artifactId = "serial-int-caster"
            version = "2.0.0"

            from(components["java"])

            pom {
                name.set("Serial Int Caster")
                description.set("Encode or decode an integer to or from a string serial")
                url.set("https://github.com/Kwaadpepper/serial-int-caster-kotlin")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("kwaadpepper")
                        name.set("Jérémy Munsch")
                        email.set("github@jeremydev.ovh")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Kwaadpepper/serial-int-caster-kotlin.git")
                    developerConnection.set("scm:git:ssh://git@github.com/Kwaadpepper/serial-int-caster-kotlin.git")
                    url.set("https://github.com/Kwaadpepper/serial-int-caster-kotlin")
                }
            }
        }
    }
}

nexusPublishing {
    this.repositories {
        create("central") {
            nexusUrl.set(uri("https://central.sonatype.com/api/v1/publisher/"))
            username.set(System.getenv("MAVEN_CENTRAL_USERNAME"))
            password.set(System.getenv("MAVEN_CENTRAL_PASSWORD"))
        }
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}
