package io.guw.knxutils.semanticanalyzer.characteristics.pattern;

import io.guw.knxutils.knxprojectparser.DatapointType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static io.guw.knxutils.knxprojectparser.DatapointType.*;

/**
 * pattern 1: assume GAs a created as blocks of 10 GAs (0=onOff, 1=CurrentTemperature, 2=TargetTemperature, 3=StateCurrentTemperature, 4=StateTargetTemperature, 5=unassigned, 6=unassigned, 7=unassigned, 8=unassigned, 9=mode)
 * TODO: check mode switch (frost, eco, comfort etc.)
 */
@RequiredArgsConstructor
@Getter
public enum HeatingPattern {
    Value(0, Switch),
    CurrentTemperature(1, Temperature),
    TargetTemperature(2, Temperature),
    StateCurrentTemperature(3, Temperature),
    StateTargetTemperature(4, Temperature),
    StateMode(9, HVACMode),
    ;

    private final int offset;

    private final DatapointType dpt;

}
