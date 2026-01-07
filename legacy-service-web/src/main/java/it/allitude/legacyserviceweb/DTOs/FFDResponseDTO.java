package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.allitude.legacyserviceweb.models.ISeriesFieldDescription;

public class FFDResponseDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String library;
	private String ddsName;
	private List<ISeriesFieldDescription> fields = new ArrayList<ISeriesFieldDescription>();

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getLibrary() {
		return library;
	}
	public void setLibrary(String library) {
		this.library = library;
	}
	public String getDdsName() {
		return ddsName;
	}
	public void setDdsName(String ddsName) {
		this.ddsName = ddsName;
	}

	public List<ISeriesFieldDescription> getFields() {
		return fields;
	}
	public void setFields(List<ISeriesFieldDescription> fields) {
		this.fields = fields;
	}
	
}