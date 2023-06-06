plugins {
    id("java")
}

group = "dev.marfien"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.inject:guice:7.0.0")
    implementation("org.jetbrains:annotations:24.0.1")

    implementation("com.google.auto.service:auto-service:1.1.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}