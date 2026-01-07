package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class FFDRequestDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String library;

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library.toUpperCase();
	}

	private String ddsName;

	public String getDdsName() {
		return ddsName;
	}

	public void setDdsName(String ddsName) {
		this.ddsName = ddsName.toUpperCase();
	}

	public FFDRequestDTO() {
	}

	public FFDRequestDTO(String library, String ddsName) {
		setLibrary(library);
		setDdsName(ddsName);

	}

}