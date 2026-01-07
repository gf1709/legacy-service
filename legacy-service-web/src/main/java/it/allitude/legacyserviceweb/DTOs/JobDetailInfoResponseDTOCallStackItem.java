package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;

public class JobDetailInfoResponseDTOCallStackItem implements Serializable {
	String level;
	String program;
	String programLib;
	String statement;
	String instructionNumber;
	String procedure;
	String module;
	String moduleLib;

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getProgramLib() {
		return programLib;
	}

	public void setProgramLib(String programLib) {
		this.programLib = programLib;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public String getInstructionNumber() {
		return instructionNumber;
	}

	public void setInstructionNumber(String instructionNumber) {
		this.instructionNumber = instructionNumber;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModuleLib() {
		return moduleLib;
	}

	public void setModuleLib(String moduleLib) {
		this.moduleLib = moduleLib;
	}
}