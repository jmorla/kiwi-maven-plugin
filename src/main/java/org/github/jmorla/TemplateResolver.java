package org.github.jmorla;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * The {@code TemplateResolver} class is responsible for resolving template
 * paths and checking if they end with a specified file extension.
 * 
 * @author Jorge L. Morla
 * 
 */
public class TemplateResolver {
    
    private final Path baseDir;
    private final String prefix;


    /**
     * Constructs a {@code TemplateResolver} with the specified base directory and
     * template name prefix. The resolver is used to locate and process templates
     * within the given directory.
     * 
     * @param baseDir
     * @param prefix
     * 
     */
    public TemplateResolver(Path baseDir, String prefix) {
        if(!Files.exists(baseDir)) {
            throw new IllegalArgumentException("directory does not exists " + baseDir.toAbsolutePath());
        }
        this.baseDir = baseDir;
        this.prefix = prefix;
    }

    /**
     * Recursively scans the directory tree to find templates and returns an array
     * of resolved {@code Template} objects.
     */
    public Template[] resolve() throws IOException {
        List<Template> templates = new ArrayList<>();
        resolve(baseDir, templates);

        return templates.toArray(Template[]::new);

    }
    
    private void resolve(Path path, List<Template> templates) throws IOException {
        if (!Files.isDirectory(path)) {
            String pathStr = path.toString();
            if (pathStr.endsWith(prefix)) {
                Template template = new Template();
                template.setFile(path.toFile());
                template.setRelativePath(pathStr.replace(baseDir.toString() + "/", ""));
                templates.add(template);
            }
        } else {
            try (Stream<Path> stream = Files.list(path)) {
                Path[] paths = stream.toArray(Path[]::new);
                for (Path subPath : paths) {
                    resolve(subPath, templates);
                }
            }
            
        } 
    }
}
