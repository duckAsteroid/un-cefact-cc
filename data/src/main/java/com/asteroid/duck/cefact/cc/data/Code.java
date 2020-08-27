package com.asteroid.duck.cefact.cc.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Code {
    private int groupNumber;
    private String sector;
    private int groupID;
    private String quantity;
    private String levelCategory;
    private Status status;
    private String commonCode;
    private String name;
    private String conversionFactor;
    private String symbol;
    private String description;

    public static final int NUM_FIELDS = 11;

    public static class CodeBuilder {
        public CodeBuilder setByIndex(int index, String value) {
            switch (index) {
                case 0: return groupNumber(Integer.parseInt(value));
                case 1: return sector(value);
                case 2: return groupID(Integer.parseInt(value));
                case 3: return quantity(value);
                case 4: return levelCategory(levelCategory);
                case 5: return status(Status.forCode(value).orElseThrow());
                case 6: return commonCode(commonCode);
                case 7: return name(name);
                case 8: return conversionFactor(conversionFactor);
                case 9: return symbol(symbol);
                case 10: return description(description);
                default: throw new IllegalArgumentException("Unknown index");
            }
        }
    }
}
