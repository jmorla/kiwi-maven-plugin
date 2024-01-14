package org.github.jmorla;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.github.jmorla.kiwicompiler.KiwiGenerator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Kiwi Generate Mojo
 * 
 * @author Jorge L. Morla
 */
@Mojo(name = "generate", 
        defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.RUNTIME,
        threadSafe = true
)
public class KiwiGenerateMojo extends AbstractMojo {

    /**
     * Output Template Directory
     */
    @Parameter(defaultValue = "${project.build.directory}/classes/templates", property = "outputDirectory", required = true)
    private File templateOutputDirectory;

    /**
     * Resources directory
     */
    @Parameter(defaultValue = "${project.build.resources[0].directory}/templates", property = "templateDirectory")
    private File templateDirectory;

    /**
     * Generated Js Directory
     * 
     */
    @Parameter(defaultValue = "${project.build.resources[0].directory}/static/generated", property = "generatedJsDirectory")
    private File generatedJsDirectory;

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


    @Parameter(property = "includeReactImports", required = true, defaultValue = "true")
    private boolean includeReactImports;


    @Parameter(property = "useDefaultImports", required = true, defaultValue = "true")
    private boolean useDefaultsImports;


    @Parameter(property = "baseImportPath", required = true, defaultValue = "@components")
    private String baseImportPath;

    /**
     * Kiwi Generator
    */
    private KiwiGenerator generator;

    
    @Override
    public void execute() throws MojoExecutionException {

        if (!templateOutputDirectory.exists()) {
            templateOutputDirectory.mkdirs();
        }

        generator = KiwiGenerator.with()
                .baseImportPath(baseImportPath)
                .defaultImports(useDefaultsImports)
                .includeReactImports(includeReactImports)
                .build();


        try {
            Template[] templates = getTemplates();
            for (Template template : templates) {
                generateSource(template);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new MojoExecutionException(ex.getMessage());
        }

    }

    private void generateSource(Template template) throws IOException {
        FileReader source = new FileReader(template.getFile());
        if (!generator.constainsDirectives(source)) {
            return;
        }
        source.close();

        source = new FileReader(template.getFile());

        Path templateOutputFile = templateOutputDirectory.toPath().resolve(template.getRelativePath());
        Path generatedJsDirectoryPath = generatedJsDirectory.toPath();
        Path generatedJsFilePath = generatedJsDirectoryPath.resolve(template.getRelativePath()
                .replace(templatePrefix, templatePrefix.startsWith(".") ? ".js" : "js"));

        Path directoryPath = Path.of(generatedJsFilePath.toString()
                .replace(generatedJsFilePath.getFileName().toString(), ""));

        Files.createDirectories(directoryPath);

        if (Files.notExists(generatedJsFilePath)) {
            Files.createFile(generatedJsFilePath);
        }

        FileWriter templatedWriter = new FileWriter(templateOutputFile.toFile());
        FileWriter jsSourceWriter = new FileWriter(generatedJsFilePath.toFile());

        generator.generate(source, templatedWriter, jsSourceWriter);
        templatedWriter.flush();
        jsSourceWriter.flush();

        templatedWriter.close();
        jsSourceWriter.close();

    }

    private Template[] getTemplates() throws MojoExecutionException {
        try {
            TemplateResolver templateResolver = new TemplateResolver(templateDirectory.toPath(), templatePrefix);
            return templateResolver.resolve();
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage());
        }
    }
}
