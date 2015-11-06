package net.crimsonfist.crimsonfist;

/**
 * Created by nburn42 on 11/6/15.
 */
public enum CrimeType {
    AGGASSAULT(1),
    AUTOTHEFT(2),
    BURGLARY(3),
    HOMICIDE(4),
    LARCENY(5),
    RAPE(6);

    private int value;

    private CrimeType(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }

    public static CrimeType fromString(String string) {
        switch (string) {
            case "AGG ASSAULT":
                return AGGASSAULT;
            case "AUTO THEFT":
                return  AUTOTHEFT;
            case "BURGLARY":
                return  BURGLARY;
            case "HOMISIDE":
                return HOMICIDE;
            case "LARCENY":
                return LARCENY;
            case "RAPE":
                return RAPE;
        }
        return null;
    }
}
