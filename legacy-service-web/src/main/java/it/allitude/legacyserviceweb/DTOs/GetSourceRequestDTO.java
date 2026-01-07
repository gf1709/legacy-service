package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class GetSourceRequestDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String library;
	private String sourceFile;
	private String sourceMember;
	private boolean explodeCOPY;
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
	public boolean isExplodeCOPY() {
		return explodeCOPY;
	}
	public void setExplodeCOPY(boolean exploreCOPY) {
		this.explodeCOPY = exploreCOPY;
	}
	public GetSourceRequestDTO(String library, String sourceFile, String sourceMember, boolean exploreCOPY) {
		this.library = library;
		this.sourceFile = sourceFile;
		this.sourceMember = sourceMember;
		this.explodeCOPY = exploreCOPY;
	}

}