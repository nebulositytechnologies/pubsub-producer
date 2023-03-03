plugins {
    id("java")
    id("org.springframework.boot") version "2.7.9"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
}

group = "org.nebulositytech"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

extra["springCloudGcpVersion"] = "3.4.5"
extra["springCloudVersion"] = "2021.0.5"

dependencyManagement {
    imports {
        mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:${property("springCloudGcpVersion")}")
        mavenBom(
            "org.springframework.cloud:spring-cloud-dependencies:${
                property(
                    "springCloudVersion"
                )
            }"
        )
    }
}

configurations.all {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
    exclude("org.apache.logging.log4j", "log4j-to-slf4j")
}

dependencies {
    implementation("com.google.cloud:spring-cloud-gcp-starter-pubsub")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.google.cloud:google-cloud-monitoring")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.apache.logging.log4j:log4j-layout-template-json:2.17.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}