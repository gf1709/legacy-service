package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.util.ArrayList;

public class JobListResponseDTO implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String name = "";
    private String user = "";
    private String userDescription = "";
    private String currentUser = "";
    private String currentUserDescription = "";
    private String number = "";
    private String status = "";
    private String function = "";
    private ArrayList<String> remoteAddresses = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public ArrayList<String> getRemoteAddresses() {
        return remoteAddresses;
    }

    public void setRemoteAddresses(ArrayList<String> remoteAddresses) {
        this.remoteAddresses = remoteAddresses;
    }
    public void addRemoteAddress(String remoteAddress) {
        remoteAddresses.add(remoteAddress);
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getCurrentUserDescription() {
        return currentUserDescription;
    }

    public void setCurrentUserDescription(String currentUserDescription) {
        this.currentUserDescription = currentUserDescription;
    }

}
