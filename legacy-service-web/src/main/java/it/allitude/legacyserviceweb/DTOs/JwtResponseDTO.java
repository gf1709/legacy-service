package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JwtResponseDTO  implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;
	private final ArrayList<String> roles = new ArrayList<>();

	public JwtResponseDTO(String jwttoken, List<String> roles) {
		this.jwttoken = jwttoken;
		for (String roleString : roles) {
			this.roles.add(roleString.toLowerCase().trim());			
		}
	}

	public String getToken() {
		return this.jwttoken;
	}

    public ArrayList<String> getRoles() {
        return roles;
    }
}
