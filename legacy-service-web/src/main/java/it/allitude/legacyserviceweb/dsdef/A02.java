package it.allitude.legacyserviceweb.dsdef;

import java.beans.PropertyVetoException;
import java.io.UnsupportedEncodingException;

import com.ibm.as400.access.AS400Date;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.DateFieldDescription;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;

public class A02 extends RecordFormat {

    public static final String A02SRC = "A02SRC";
    public static final String A02CAG = "A02CAG";
    public static final String A02COG = "A02COG";
    public static final String A02NOM = "A02NOM";
    public static final String A02DEA = "A02DEA";
    public static final String A02COA = "A02COA";
    public static final String A02SES = "A02SES";
    public static final String A02DTN = "A02DTN";
    public static final String A02LAN = "A02LAN";
    public static final String A02CAN = "A02CAN";
    public static final String A02PAN = "A02PAN";
    public static final String A02DTD = "A02DTD";
    public static final String A02IST = "A02IST";

    public A02() throws PropertyVetoException {
        super();
        this.setName("A02");

        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(1), A02SRC));// A 1
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(11), A02CAG));// A 11
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(40), A02COG));// A 40
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(40), A02NOM));// A 40
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(40), A02DEA));// A 40
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(40), A02COA));// A 40
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(1), A02SES));// A 1
        this.addFieldDescription(new DateFieldDescription(new AS400Date(), A02DTN));// L 10
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(50), A02LAN));// A 50
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(6), A02CAN));// A 6
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(3), A02PAN));// A 3
        this.addFieldDescription(new DateFieldDescription(new AS400Date(10), A02DTD));// L 10
        this.addFieldDescription(new CharacterFieldDescription(new AS400Text(6), A02IST));// A 6
    }

    public static Record from(byte[] someBytes) throws UnsupportedEncodingException, PropertyVetoException {
        A02 rf = new A02();
        return rf.getNewRecord(someBytes);
    }


}
