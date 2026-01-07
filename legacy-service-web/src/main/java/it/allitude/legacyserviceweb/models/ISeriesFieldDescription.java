package it.allitude.legacyserviceweb.models;

import java.io.Serializable;

public class ISeriesFieldDescription implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private int fieldNo;
	private String fieldName;
	private String fieldType;
	private int fieldLength;
	private int fieldScale;
	private String fieldDescription;

	public int getFieldNo() {
		return fieldNo;
	}
	public void setFieldNo(int fieldNo) {
		this.fieldNo = fieldNo;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public int getFieldLength() {
		return fieldLength;
	}
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}
	public int getFieldScale() {
		return fieldScale;
	}
	public void setFieldScale(int fieldScale) {
		this.fieldScale = fieldScale;
	}
	public String getFieldDescription() {
		return fieldDescription;
	}
	public void setFieldDescription(String fieldDescription) {
		this.fieldDescription = fieldDescription;
	}
}