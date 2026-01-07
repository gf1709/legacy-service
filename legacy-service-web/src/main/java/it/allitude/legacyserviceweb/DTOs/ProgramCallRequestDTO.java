package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.util.ArrayList;

import it.allitude.legacyserviceweb.models.ISeriesFieldValue;

public class ProgramCallRequestDTO implements Serializable, Comparable<ProgramCallRequestDTO>  {

	String program;
	String type;
	String command;
	String flagIO;
	String dsin;
	String dsout;
	String cid;
	String when;
	String times;

	ArrayList<ISeriesFieldValue> values;

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

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

	String getSerializedInput() {
		String res = "";
		for (ISeriesFieldValue i : values) {
			res += i.getValue().trim();
		}
		return res;
	}

	public boolean equals(ProgramCallRequestDTO aCall) {
		return this.getCid().trim().toUpperCase().equals(aCall.getCid().trim().toUpperCase())
				&& this.getCommand().trim().toUpperCase().equals(aCall.getCommand().trim().toUpperCase())
				&& this.getDsin().trim().toUpperCase().equals(aCall.getDsin().trim().toUpperCase())
				&& this.getDsout().trim().toUpperCase().equals(aCall.getDsout().trim().toUpperCase())
				&& this.getFlagIO().trim().toUpperCase().equals(aCall.getFlagIO().trim().toUpperCase())
				&& this.getProgram().trim().toUpperCase().equals(aCall.getProgram().trim().toUpperCase())
				&& this.getType().trim().toUpperCase().equals(aCall.getType().trim().toUpperCase())
				&& this.getSerializedInput().trim().toUpperCase()
						.equals(aCall.getSerializedInput().trim().toUpperCase());
	}

    public String getWhen() {
        return when;
    }

    public void setWhen(String ts) {
        this.when = ts;
    }

    @Override
    public int compareTo(ProgramCallRequestDTO o) {
		return this.getWhen().compareTo(o.getWhen());
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }
}
