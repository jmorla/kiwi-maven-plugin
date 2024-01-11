package org.github.jmorla;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.github.jmorla.kiwicompiler.KiwiGenerator;

import io.mvnpm.esbuild.resolve.ExecutableResolver;

import java.io.File;
import java.io.IOException;

/**
 * Kiwi Generate Mojo
 * 
 * @author Jorge L. Morla
 */
@Mojo(name = "generate", 
    defaultPhase = LifecyclePhase.GENERATE_RESOURCES, 
    requiresDependencyResolution = ResolutionScope.RUNTIME, 
    threadSafe = true
)
public class KiwiGenerateMojo extends AbstractMojo {

    /**
     * Output directory
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = true)
    private File outputDirectory;

    /**
     * 
    */
    @Parameter(defaultValue = "${project.build.directory}/classes/templates", property = "outputTemplates", required = true)
    private File outputTemplate;

    /**
     * Resources directory
     */
    @Parameter(defaultValue = "${project.build.resources[0].directory}", property = "sourceDirectory")
    private File resourcesDirectory;

    /**
     * React directory
     */
    @Parameter(defaultValue = "${basedir}/src/main/react", property = "reactDirectory")
    private File reactDirectory;

    /*
     * Template prefix
    */
    @Parameter(property = "templatePrefix", required = true)
    private String templatePrefix;

    /**
     * Esbuild version
     */
    @Parameter(defaultValue = "0.18.20", property = "esbuildVersion")
    private String esbuildVersion;

    private KiwiGenerator generator = KiwiGenerator.withDefaults();

    @Override
    public void execute() throws MojoExecutionException {
        File outDir = outputDirectory;

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        Template[] templates = getTemplates();
        for (Template template : templates) {
            generateSources(template);
        }

    }

    private void generateSources(Template template) {
        System.out.println(template.getRelativePath());
    }

    private Template[] getTemplates() throws MojoExecutionException {
        try {
            TemplateResolver templateResolver = new TemplateResolver(resourcesDirectory.toPath(), templatePrefix);
            return templateResolver.resolve();
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage());
        }
    }

    private File getEsbuildExecutable() throws MojoExecutionException {
        try {
            return new ExecutableResolver().resolve(esbuildVersion).toFile();
        } catch (IOException e) {
            throw new MojoExecutionException("unable to locate esbuild:");
        }
    }
}
