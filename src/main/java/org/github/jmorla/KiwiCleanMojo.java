package org.github.jmorla;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
/**
 * Kiwi Generate Mojo
 * 
 * @author Jorge L. Morla
 */
@Mojo(name = "clean", 
        defaultPhase = LifecyclePhase.CLEAN, 
        requiresDependencyResolution = ResolutionScope.RUNTIME
)
public class KiwiCleanMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.resources[0].directory}/static/generated", property = "generatedJsDirectory")
    private File generatedJsDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (generatedJsDirectory.exists()) {
            deleteDirectory(generatedJsDirectory);
        }
    }

    private void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();

    }
    
}
