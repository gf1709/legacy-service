package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class JobDetailInfoResponseDTOOpenFileItem implements Serializable {
	String library;
	String file;
	String type;
	String member;
	String actgrp;
	String writeCount;
	String readCount;
	String rrn;
	public String getLibrary() {
		return library;
	}
	public void setLibrary(String library) {
		this.library = library;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String merber) {
		this.member = merber;
	}
	public String getActgrp() {
		return actgrp;
	}
	public void setActgrp(String actgrp) {
		this.actgrp = actgrp;
	}
	public String getWriteCount() {
		return writeCount;
	}
	public void setWriteCount(String writeCount) {
		this.writeCount = writeCount;
	}
	public String getReadCount() {
		return readCount;
	}
	public void setReadCount(String readCount) {
		this.readCount = readCount;
	}
	public String getRrn() {
		return rrn;
	}
	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

}