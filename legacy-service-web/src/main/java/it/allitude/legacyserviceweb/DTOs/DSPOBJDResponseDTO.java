package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DSPOBJDResponseDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private final String library;
	private final String name;
	private final String type;
	private String description;
	private String creationDate;
	private String creationUser;
	private String changeDate;
	private String changeUser;
	private String attribute;
	private String owner;
	private String size;


    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description.trim();
    }

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		this.creationDate = simpleDateFormat.format(creationDate);		
	}

	public String getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		this.changeDate = simpleDateFormat.format(changeDate);				
	}

	public String getChangeUser() {
		return changeUser;
	}

	public void setChangeUser(String changeUser) {
		this.changeUser = changeUser;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = "*" + attribute.replace("*", "");
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getLibrary() {
		return library;
	}

	public String getName() {
		return name;
	}

	public String getType() {		
		return type;
	}

	public DSPOBJDResponseDTO(String library, String objectName, String objetType) {
		this.library = library;
		this.name = objectName;
		this.type = "*" + objetType.replace("*", "");
	}

}