
plugins {
    kotlin("jvm") version "1.6.20"
}

group = "net.grandcentrix.qlik"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("ch.qos.logback:logback-core:1.2.11")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.17.1")
    implementation("org.slf4j:slf4j-api:1.7.36")

    implementation("org.apache.poi:poi:5.2.2")
    implementation("org.apache.poi:poi-ooxml:5.2.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.2")

}

tasks.test {
    useJUnitPlatform()
}