package it.allitude.legacyserviceweb.DTOs;

import java.io.Serializable;
import java.util.ArrayList;

public class GetSourceResponseDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String library;
	private String sourceFile;
	private String sourceMember;
	private boolean explodeCOPY;
	private String sourceType;

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	private ArrayList<String> source;

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
		String sf = sourceFile.trim().toUpperCase();
		if (sf.equals("QRPGSRC") || sf.equals("QRBFSRC")) {
			sourceType = "RPG";
			return;
		} else if (sf.equals("QCLSRC")) {
			sourceType = "CL";
			return;
		} else if (sf.equals("QDDSSRC") || sf.equals("QDDFSRC")) {
			sourceType = "DDS";
			return;
		}
		else if (sf.equals("QDDSSRC") || sf.equals("QDDFSRC")) {
			sourceType = "DDS";
			return;
		} else if (sf.equals("QRPGLESRC")) {
			sourceType = "RPGLE";
			return;
		} else if (sf.equals("QCMDSRC")) {
			sourceType = "COMMAND";
			return;
		} else if (sf.equals("QCLESRC") || sf.equals("QCSRC")) {
			sourceType = "C";
			return;
		} else if (sf.equals("QCBLLESRC")) {
			sourceType = "CBLLE";
			return;
		} else if (sf.equals("QCPYCBLLE")) {
			sourceType = "CPYCBLLE";
			return;
		} else if (sf.equals("QLBLSRC")) {
			sourceType = "QLBLSRC";
			return;
		} else if (sf.equals("QQMQRYSRC")) {
			sourceType = "SQL";
			return;
		} else
			sourceType = "TXT";
	}

	public String getSourceMember() {
		return sourceMember;
	}

	public void setSourceMember(String sourceMember) {
		this.sourceMember = sourceMember;
	}

	public boolean getExplodeCOPY() {
		return explodeCOPY;
	}

	public void setExplodeCOPY(boolean explodeCOPY) {
		this.explodeCOPY = explodeCOPY;
	}

	public ArrayList<String> getSource() {
		return source;
	}

	public void setSource(ArrayList<String> source) {
		this.source = source;
	}

	public GetSourceResponseDTO(String library, String sourceFile, String sourceMember, boolean explodeCOPY,
			ArrayList<String> source) {
		this.setLibrary(library); 
		this.setSourceFile(sourceFile); ;
		this.setSourceMember(sourceMember);
		this.explodeCOPY = explodeCOPY;
		this.setSource(source);
	}

}