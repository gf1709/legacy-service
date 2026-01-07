package it.allitude.legacyserviceweb.models;

public class AmbienteBanca {

    private String abi;
    private String libreriaDati;

    public AmbienteBanca(String libreriaDati, String abi) {
        this.abi = abi;
        this.libreriaDati = libreriaDati;
    }
    
    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }

    public String getLibreriaDati() {
        return libreriaDati;
    }

    public void setLibreriaDati(String libreriaDati) {
        this.libreriaDati = libreriaDati;
    }

    public String getAmbiente() {
        return getLibreriaDati().substring(2, 4);
    }

    public String getTarga() {
        return getLibreriaDati().substring(4, 5);
    }    
}
