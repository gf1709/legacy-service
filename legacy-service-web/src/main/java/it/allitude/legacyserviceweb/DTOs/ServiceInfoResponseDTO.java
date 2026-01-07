package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class ServiceInfoResponseDTO implements Serializable {

    private String port;
    private String name;
    private String enabled;
    private String program;
    private String targa;
    private String ambiente;
    private String abi;

    public ServiceInfoResponseDTO() {
        port = "";
        name = "";
        enabled = "";
        program = "";
        targa = "";
        ambiente = "";
        abi = "";
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }


}
