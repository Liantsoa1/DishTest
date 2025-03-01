package mapper;

import entity.Unit;

import static entity.Unit.*;

public class UnitMapper {
    public Unit mapFromResultSet(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        return switch (stringValue.trim().toUpperCase()) {
            case "G" -> G;
            case "L" -> L;
            case "U" -> U;
            default -> throw new IllegalArgumentException("Unknown Unit value " + stringValue);
        };
    }
}




