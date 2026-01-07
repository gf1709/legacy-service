package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class GetSourceListResponseItem implements Serializable {


	private String library;
	private String sourceFile;
	private String sourceMember;
	private String sourceMemberDescription;

	public String getSourceMemberDescription() {
		return sourceMemberDescription;
	}
	public void setSourceMemberDescription(String sourceMemberDescription) {
		this.sourceMemberDescription = sourceMemberDescription;
	}
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

	public GetSourceListResponseItem(String library, String sourceFile, String sourceMember, String sourceMemberDescription) {
		this.library = library;
		this.sourceFile = sourceFile;
		this.sourceMember = sourceMember;
		this.sourceMemberDescription = sourceMemberDescription;
	}
	
}