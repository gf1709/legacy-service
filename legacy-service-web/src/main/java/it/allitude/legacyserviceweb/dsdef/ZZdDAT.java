package it.allitude.legacyserviceweb.dsdef;

import java.beans.PropertyVetoException;
import java.io.UnsupportedEncodingException;

import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;

public class ZZdDAT extends RecordFormat {

    public static final String ZZDO01 = "ZZDO01";
    public static final String ZZDO02 = "ZZDO02";
    public static final String ZZDO03 = "ZZDO03";
    public static final String ZZDO04 = "ZZDO04";
    public static final String ZZDO05 = "ZZDO05";
    public static final String ZZDO06 = "ZZDO06";
    public static final String ZZDO07 = "ZZDO07";
    public static final String ZZDO08 = "ZZDO08";
    public static final String ZZDO09 = "ZZDO09";
    public static final String ZZDO10 = "ZZDO10";
    public static final String ZZDO11 = "ZZDO11";
    public static final String ZZDO12 = "ZZDO12";
    public static final String ZZDO13 = "ZZDO13";
    public static final String ZZDO14 = "ZZDO14";
    public static final String ZZDO15 = "ZZDO15";

    public ZZdDAT() throws PropertyVetoException {
        super();
        this.setName("ZZ$DAT");

        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO01));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO02));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO03));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO04));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO05));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO06));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO07));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO08));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO09));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO10));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO11));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO12));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO13));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO14));
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(256), ZZDO15));
    }

    public static Record from(byte[] someBytes) throws UnsupportedEncodingException, PropertyVetoException {
        ZZdDAT rf = new ZZdDAT();
        return rf.getNewRecord(someBytes);
    }


}
