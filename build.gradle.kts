plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "nu.csse.sqe"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Source: https://mvnrepository.com/artifact/org.easymock/easymock
    testImplementation("org.easymock:easymock:5.4.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.compileJava {
    options.release = 11
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("nu.csse.sqe.gui.RiskApplication")
}

javafx {
    version = "17.0.12"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
    )
}
