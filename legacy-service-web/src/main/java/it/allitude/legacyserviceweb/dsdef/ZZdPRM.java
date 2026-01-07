package it.allitude.legacyserviceweb.dsdef;

import java.beans.PropertyVetoException;
import java.io.UnsupportedEncodingException;

import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;

public class ZZdPRM extends RecordFormat {

    public static final String ZZPFPC = "ZZPFPC";
    public static final String ZZPCMD = "ZZPCMD";
    public static final String ZZPFIO = "ZZPFIO";
    public static final String ZZPDSI = "ZZPDSI";
    public static final String ZZPDSO = "ZZPDSO";
    public static final String ZZPCID = "ZZPCID";
    public static final String ZZPRTC = "ZZPRTC";

    public ZZdPRM() throws PropertyVetoException {
        super();
        this.setName("ZZ$PRM");

        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(1), ZZPFPC));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(8), ZZPCMD));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(1), ZZPFIO));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(8), ZZPDSI));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(8), ZZPDSO));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(8), ZZPCID));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(2), ZZPRTC));
    }

    public static Record from(byte[] someBytes) throws UnsupportedEncodingException, PropertyVetoException {
        ZZdPRM rf = new ZZdPRM();
        return rf.getNewRecord(someBytes);
    }


}
