package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class JwtRequestDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;
	
	private String username;
	private String password;
	private String session;

	public JwtRequestDTO()
	{
	}

	public JwtRequestDTO(String username, String password, String session) {
		this.setUsername(username);
		this.setPassword(password);
		this.setSession(session);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
}