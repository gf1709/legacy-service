package it.allitude.legacyserviceweb.DTOs;

import java.util.Date;

public class IFSListFileRequestDTO {

    String directory;
    String pattern;
    Date fromDate;
    Date toDate;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

}
