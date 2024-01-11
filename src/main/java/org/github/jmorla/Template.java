package org.github.jmorla;

import java.io.File;

/**
 * Represents a template with kiwi directives
 * @author Jorge L. Morla
 * 
*/
public class Template {

    private String relativePath;

    private File file;

    
    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Template [relativePath=" + relativePath + ", file=" + file + "]";
    }

}
