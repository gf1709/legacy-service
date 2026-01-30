package it.allitude.legacyserviceweb.models;

import java.util.ArrayList;

public class Roles {

    // Profilazione utente: da evolvere
    public static ArrayList<String> getRoles(String anUser) {
        ArrayList<String> admins = new ArrayList<>();
        admins.add("fc0382");
        admins.add("fc0059");
        ArrayList<String> roles = new ArrayList<>();
        roles.add("user");  // ruolo base aggiunto d'ufficio
        if (admins.contains(anUser.toLowerCase().trim())) {
            roles.add("admin");
        }
        return roles;
    }

}
