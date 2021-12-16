package github.stackunderflow111

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import spock.lang.TempDir
import spock.lang.Specification

class IntegrationWithJooq extends Specification {
    @TempDir File testProjectDir
    File settingsFile
    File buildFile

    def setup() {
        settingsFile = new File(testProjectDir, 'settings.gradle.kts')
        buildFile = new File(testProjectDir, 'build.gradle.kts')
    }

    def "runs generateJooq successfully"() {
        given:
        settingsFile << 'rootProject.name = "jooq-integration"'
        buildFile << new File("src/test/resources/gradle/jooq.gradle.kts.test").text

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withPluginClasspath()
                .withArguments('generateJooq')
                .build()
        def studentTableFile = new File(testProjectDir,
                'build/generated-src/jooq/main/org/jooq/generated/tables/Student.java')

        then:
        studentTableFile.exists()
        result.task(":generateJooq").outcome == SUCCESS
    }
}
