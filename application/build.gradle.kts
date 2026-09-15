plugins { id("org.springframework.boot") version "4.1.1" }
dependencies {
    implementation(project(":common"))
    implementation("org.springframework:spring-tx")
    implementation(project(":service"))
    runtimeOnly(project(":dataaccess"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation(project(":dataaccess"))
    testImplementation("org.hibernate.orm:hibernate-core")
    testImplementation("org.springframework:spring-jdbc")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
tasks.bootJar { archiveFileName = "lab3-app.jar" }
tasks.register<Test>("integrationTest") {
    description = "HTTP, PostgreSQL, transaction and query-count checks against a separate test database"
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform { includeTags("integration"); excludeTags.clear() }
    shouldRunAfter(tasks.test)
}
