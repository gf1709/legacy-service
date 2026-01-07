package it.allitude.legacyserviceweb.DTOs;

import java.io.IOException;
import java.io.Serializable;

import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.ObjectDoesNotExistException;

public class WRKOBJResponseDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String library;
	private String name;
	private String type;
	private String attribute;
	private String description;


	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
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

	public WRKOBJResponseDTO(ObjectDescription objectDescription) throws AS400Exception, AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException, ObjectDoesNotExistException {
		library = objectDescription.getLibrary();
		name = objectDescription.getName();
		type = objectDescription.getType();
		description = objectDescription.getValueAsString(ObjectDescription.TEXT_DESCRIPTION);
		try {
			attribute = objectDescription.getValueAsString(ObjectDescription.EXTENDED_ATTRIBUTE);
			if (attribute != null && attribute.length() > 0)
				attribute = "*" + attribute;
		} catch (AS400SecurityException | ErrorCompletingRequestException | InterruptedException | IOException
				| ObjectDoesNotExistException e) {
			e.printStackTrace();
		}
	}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}