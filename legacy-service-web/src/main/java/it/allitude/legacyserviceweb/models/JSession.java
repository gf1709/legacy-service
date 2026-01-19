package it.allitude.legacyserviceweb.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JSession {
    private String user;
    private String terminal;
     
    @JsonIgnore 
    private String jwt;

    private String targaCassa;
    private String ambiente;
    private String terminaleApplicativo;

    private String libreriaTemporanea;
    private String sib_Directory;
    private String descrizione_breve_CR;
    private String cartellaAmbienteDati;
    private String descrizione_2_CR;
    private String cab;
    private String libreria4;
    private String libreria2;
    private String libreria3;
    private String libreriaRete;
    private String documentale_SIB2000;
    private String documentale_InfoBanking;
    private String libreriaCestino;
    private String cartellaFileTransfer;
    private String libreriaDatiBanca;
    private String ambienteDatiStorici;
    private String server_SID2000;
    private String abi_cin;
    private String ambienteDati;
    private String descrizione_1_CR;
    private String descrizioneFilialeBreve;
    private String abi;
    private String cab_senza_cin;
    private String libreria1;
    private String libreriaProcedure;
    private String descrizioneFiliale;
    private String codiceFiliale;
    private boolean isAdminUserValue;

    private List<String> getAdminUserList()
    {
        List<String> admins = new ArrayList<>();
        admins.add("FC0382");
        admins.add("FC0059");
        return admins;
    }
    
    public String getTargaCassa() {
        return targaCassa;
    }

    public void setTargaCassa(String targaCassa) {
        this.targaCassa = targaCassa;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getTerminaleApplicativo() {
        return terminaleApplicativo;
    }

    public void setTerminaleApplicativo(String terminaleApplicativo) {
        this.terminaleApplicativo = terminaleApplicativo;
    }

    public String getUser() {
        return user;
    }
    public boolean isAdminUser() {
        return isAdminUserValue;
    }

    public void setUser(String user) {
        this.user = user;
        if (getAdminUserList().contains(this.user.toUpperCase())) {
            this.isAdminUserValue = true;
        } else {
            this.isAdminUserValue = false;
        }
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public static JSession getCurrentSession() {
        JSession details = (JSession) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return details;
    }

    public String getLibreriaTemporanea() {
        return libreriaTemporanea;
    }

    public void setLibreriaTemporanea(String libreriaTemporanea) {
        this.libreriaTemporanea = libreriaTemporanea;
    }

    public String getSib_Directory() {
        return sib_Directory;
    }

    public void setSib_Directory(String sib_Directory) {
        this.sib_Directory = sib_Directory;
    }

    public String getDescrizione_breve_CR() {
        return descrizione_breve_CR;
    }

    public void setDescrizione_breve_CR(String descrizione_breve_CR) {
        this.descrizione_breve_CR = descrizione_breve_CR;
    }

    public String getCartellaAmbienteDati() {
        return cartellaAmbienteDati;
    }

    public void setCartellaAmbienteDati(String cartellaAmbienteDati) {
        this.cartellaAmbienteDati = cartellaAmbienteDati;
    }

    public String getDescrizione_2_CR() {
        return descrizione_2_CR;
    }

    public void setDescrizione_2_CR(String descrizione_2_CR) {
        this.descrizione_2_CR = descrizione_2_CR;
    }

    public String getCab() {
        return cab;
    }

    public void setCab(String cab) {
        this.cab = cab;
    }

    public String getLibreria4() {
        return libreria4;
    }

    public void setLibreria4(String libreria4) {
        this.libreria4 = libreria4;
    }

    public String getLibreria2() {
        return libreria2;
    }

    public void setLibreria2(String libreria2) {
        this.libreria2 = libreria2;
    }

    public String getLibreria3() {
        return libreria3;
    }

    public void setLibreria3(String libreria3) {
        this.libreria3 = libreria3;
    }

    public String getLibreriaRete() {
        return libreriaRete;
    }

    public void setLibreriaRete(String libreriaRete) {
        this.libreriaRete = libreriaRete;
    }

    public String getDocumentale_SIB2000() {
        return documentale_SIB2000;
    }

    public void setDocumentale_SIB2000(String documentale_SIB2000) {
        this.documentale_SIB2000 = documentale_SIB2000;
    }

    public String getDocumentale_InfoBanking() {
        return documentale_InfoBanking;
    }

    public void setDocumentale_InfoBanking(String documentale_InfoBanking) {
        this.documentale_InfoBanking = documentale_InfoBanking;
    }

    public String getLibreriaCestino() {
        return libreriaCestino;
    }

    public void setLibreriaCestino(String libreriaCestino) {
        this.libreriaCestino = libreriaCestino;
    }

    public String getCartellaFileTransfer() {
        return cartellaFileTransfer;
    }

    public void setCartellaFileTransfer(String cartellaFileTransfer) {
        this.cartellaFileTransfer = cartellaFileTransfer;
    }

    public String getLibreriaDatiBanca() {
        return libreriaDatiBanca;
    }

    public void setLibreriaDatiBanca(String libreriaDatiBanca) {
        this.libreriaDatiBanca = libreriaDatiBanca;
    }

    public String getAmbienteDatiStorici() {
        return ambienteDatiStorici;
    }

    public void setAmbienteDatiStorici(String ambienteDatiStorici) {
        this.ambienteDatiStorici = ambienteDatiStorici;
    }

    public String getServer_SID2000() {
        return server_SID2000;
    }

    public void setServer_SID2000(String server_SID2000) {
        this.server_SID2000 = server_SID2000;
    }

    public String getAbi_cin() {
        return abi_cin;
    }

    public void setAbi_cin(String abi_cin) {
        this.abi_cin = abi_cin;
    }

    public String getAmbienteDati() {
        return ambienteDati;
    }

    public void setAmbienteDati(String ambienteDati) {
        this.ambienteDati = ambienteDati;
    }

    public String getDescrizione_1_CR() {
        return descrizione_1_CR;
    }

    public void setDescrizione_1_CR(String descrizione_1_CR) {
        this.descrizione_1_CR = descrizione_1_CR;
    }

    public String getDescrizioneFilialeBreve() {
        return descrizioneFilialeBreve;
    }

    public void setDescrizioneFilialeBreve(String descrizioneFilialeBreve) {
        this.descrizioneFilialeBreve = descrizioneFilialeBreve;
    }

    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }

    public String getCab_senza_cin() {
        return cab_senza_cin;
    }

    public void setCab_senza_cin(String cab_senza_cin) {
        this.cab_senza_cin = cab_senza_cin;
    }

    public String getLibreria1() {
        return libreria1;
    }

    public void setLibreria1(String libreria1) {
        this.libreria1 = libreria1;
    }

    public String getLibreriaProcedure() {
        return libreriaProcedure;
    }

    public void setLibreriaProcedure(String libreriaProcedure) {
        this.libreriaProcedure = libreriaProcedure;
    }

    public String getDescrizioneFiliale() {
        return descrizioneFiliale;
    }

    public void setDescrizioneFiliale(String descrizioneFiliale) {
        this.descrizioneFiliale = descrizioneFiliale;
    }

    public String getCodiceFiliale() {
        return codiceFiliale;
    }

    public void setCodiceFiliale(String codiceFiliale) {
        this.codiceFiliale = codiceFiliale;
    }

}
