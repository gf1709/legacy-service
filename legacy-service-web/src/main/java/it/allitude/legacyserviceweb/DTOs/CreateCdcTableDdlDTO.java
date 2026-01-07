package it.allitude.legacyserviceweb.DTOs;

public class CreateCdcTableDdlDTO {
    String library;

    public String getLibrary() {
        return library.toUpperCase();
    }

    public void setLibrary(String library) {
        this.library = library.toUpperCase();
    }

    String file;

    public String getFile() {
        return file.toUpperCase();
    }

    public void setFile(String file) {
        this.file = file.toUpperCase();
    }
}
