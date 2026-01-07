package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class GetSourceListRequestDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String library;
	private String sourceFile;
	private String sourceMember;
	public String getLibrary() {
		return library;
	}
	public void setLibrary(String library) {
		this.library = library;
	}
	public String getSourceFile() {
		return sourceFile;
	}
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	public String getSourceMember() {
		return sourceMember;
	}
	public void setSourceMember(String sourceMember) {
		this.sourceMember = sourceMember;
	}
	public GetSourceListRequestDTO(String library, String sourceFile, String sourceMember) {
		this.library = library;
		this.sourceFile = sourceFile;
		this.sourceMember = sourceMember;
	}

}