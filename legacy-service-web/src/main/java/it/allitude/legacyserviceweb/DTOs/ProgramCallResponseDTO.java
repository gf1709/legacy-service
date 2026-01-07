package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.util.ArrayList;

import it.allitude.legacyserviceweb.models.ISeriesFieldValue;

public class ProgramCallResponseDTO implements Serializable {

	String program;
	String type;
	String command;
	String flagIO;
	String dsin;
	String dsout;
	String result;
	ArrayList<ISeriesFieldValue> values = new ArrayList<ISeriesFieldValue>();
	int serializationSize = 0;
	
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getFlagIO() {
		return flagIO;
	}
	public void setFlagIO(String flagIO) {
		this.flagIO = flagIO;
	}
	public String getDsin() {
		return dsin;
	}
	public void setDsin(String dsin) {
		this.dsin = dsin;
	}
	public String getDsout() {
		return dsout;
	}
	public void setDsout(String dsout) {
		this.dsout = dsout;
	}
	public ArrayList<ISeriesFieldValue> getValues() {
		return values;
	}
	public void setValues(ArrayList<ISeriesFieldValue> values) {
		this.values = values;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

    public int getSerializationSize() {
        return serializationSize;
    }

    public void setSerializationSize(int serializationSize) {
        this.serializationSize = serializationSize;
    }

}