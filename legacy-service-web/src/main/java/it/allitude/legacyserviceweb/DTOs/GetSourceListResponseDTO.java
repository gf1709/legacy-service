package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.util.ArrayList;

public class GetSourceListResponseDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private ArrayList<GetSourceListResponseItem> sources;

	
	public ArrayList<GetSourceListResponseItem> getSources() {
		return sources;
	}


	public void setSources(ArrayList<GetSourceListResponseItem> sources) {
		this.sources = sources;
	}


	public GetSourceListResponseDTO(ArrayList<GetSourceListResponseItem> sources) {
		this.sources = sources;
	}
	
}