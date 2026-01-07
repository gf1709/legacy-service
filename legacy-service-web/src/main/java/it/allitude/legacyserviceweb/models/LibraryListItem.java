package it.allitude.legacyserviceweb.models;

public class LibraryListItem {
    String library;
    String type;
    String description;
    public String getLibrary() {
        return library;
    }
    public void setLibrary(String library) {
        this.library = library;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LibraryListItem(String library, String type, String description) {
        this.library = library;
        this.type = type;
        this.description = description;
    }
}
