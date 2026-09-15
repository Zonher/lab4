plugins { java }
group = "ru.nikita.lab3"
subprojects {
    apply(plugin = "java-library")
    repositories { mavenCentral() }
    val mockitoAgent = configurations.create("mockitoAgent")
    dependencies {
        "implementation"(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testImplementation"("org.mockito:mockito-junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
        mockitoAgent("org.mockito:mockito-core:5.23.0") { isTransitive = false }
    }
    tasks.withType<JavaCompile>().configureEach {
        options.release = 21
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }
    tasks.withType<Test>().configureEach {
        useJUnitPlatform { excludeTags("integration") }
        doFirst { jvmArgs("-javaagent:${mockitoAgent.asPath}") }
    }

}
