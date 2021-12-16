plugins {
    `java-gradle-plugin`
}

group = "github.stackunderflow111"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("jooqTestcontainersPlugin") {
            id = "github.stackunderflow111.testcontainers"
            implementationClass = "github.stackunderflow111.TestcontainersPlugin"
        }
    }
}

dependencies {
    implementation("org.testcontainers:jdbc:1.16.2")
    implementation("org.flywaydb:flyway-core:8.2.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}