package net.corda.plugins;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class InternalPackageTest {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    @Before
    public void setup() throws IOException {
        File buildFile = testProjectDir.newFile("build.gradle");
        CopyUtils.copyResourceTo("internal-package/build.gradle", buildFile);
    }

    @Test
    public void testInternalPackageIsIgnored() throws IOException {
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withArguments("scanApi", "--info")
            .withPluginClasspath()
            .build();
        String output = result.getOutput();
        System.out.println(output);

        BuildTask scanApi = result.task(":scanApi");
        assertNotNull(scanApi);
        assertEquals(TaskOutcome.SUCCESS, scanApi.getOutcome());

        assertTrue(output.contains("net.corda.internal.InvisibleClass"));

        Path api = CopyUtils.pathOf(testProjectDir, "build", "api", "internal-package.txt");
        assertTrue(api.toFile().isFile());
        assertEquals(
                "public class net.corda.VisibleClass extends java.lang.Object\n" +
                        "  public <init>()\n" +
                        "##\n", CopyUtils.toString(api));
    }
}
