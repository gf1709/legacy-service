package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class DSPOBJDRequestDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String library;
	private String objectName;
	private String objectType;

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public boolean isValid() {
		if (library == null || library.length() == 0)
			return false;
		if (objectName == null || objectName.length() == 0)
			return false;
		if (objectType == null || objectType.length() == 0)
			return false;
		return true;
	}

	public DSPOBJDRequestDTO(String library, String objetName, String objetType) {
		this.library = library;
		this.objectName = objetName;
		this.objectType = objetType;
	}

	public String getObjectType() {
		return objectType.replace("*", "");
	}

	public String getLibrary() {
		if (library == null || library.trim().equals(""))
			return "*ALL";
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getObjectName() {
		if (objectName == null || objectName.trim().equals(""))
			return "*ALL";
		return objectName;
	}

	public void setObjectName(String objetName) {
		this.objectName = objetName;
	}

}