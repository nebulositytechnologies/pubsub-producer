plugins {
    id("java")
}

group = "org.nebulositytech"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("com.google.cloud:spring-cloud-gcp-starter")
    implementation(
        platform(
            "org.springframework" +
                    ".boot:spring-boot-dependencies:2.7.9"
        )
    )
    implementation("org.springframework.boot:spring-boot-starter-webflux")
//    {
//        exclude("org.springframework.boot:spring-boot-starter-logging")
//    }
//    implementation("org.springframework.boot:spring-boot-starter-log4j2") {
//        exclude("org.apache.logging.log4j:log4j-to-slf4j")
//    }
//    implementation("org.apache.logging.log4j:log4j-layout-template-json:2.20.0")
//    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
//    implementation("org.apache.logging.log4j:log4j-jul:2.20.0")
    implementation(
        platform(
            "com.google.cloud:spring-cloud-gcp-dependencies:3" +
                    ".4.5"
        )
    )
    implementation("com.google.cloud:spring-cloud-gcp-starter-pubsub")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.google.cloud:google-cloud-monitoring")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}