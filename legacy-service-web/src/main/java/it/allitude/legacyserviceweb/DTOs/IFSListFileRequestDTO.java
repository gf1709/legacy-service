package it.allitude.legacyserviceweb.DTOs;

public class IFSListFileRequestDTO {

    String directory;
    String pattern;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
