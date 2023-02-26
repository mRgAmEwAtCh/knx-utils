package io.guw.knxutils.semanticanalyzer.characteristics.ga.pattern;

import lombok.Getter;

/**
 * pattern 1: assume GAs a created as blocks of 5 GAs (0=OnOff, 1=Dim, 2=Value, 3=StatusOnOff, 4=StatusValue)
 */
public enum LightPattern {
    OnOff(0),
    Dim(1),
    Value(2),
    StatusOnOff(3),
    StatusValue(4),
    ;
    @Getter
    private int offset;

    LightPattern(int offset){

        this.offset = offset;
    }
}
