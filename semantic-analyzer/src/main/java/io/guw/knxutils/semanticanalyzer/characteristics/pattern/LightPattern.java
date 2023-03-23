package io.guw.knxutils.semanticanalyzer.characteristics.pattern;

import io.guw.knxutils.knxprojectparser.DatapointType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static io.guw.knxutils.knxprojectparser.DatapointType.*;

/**
 * pattern 1: assume GAs a created as blocks of 5 GAs (0=OnOff, 1=Dim, 2=Value, 3=StatusOnOff, 4=StatusValue)
 */

@RequiredArgsConstructor
@Getter
public enum LightPattern {
    OnOff(0, Switch),
    Dim(1, ControlDimming),
    Value(2, Scaling),
    StatusOnOff(3, State),
    StatusValue(4, Scaling),
    ;
    private final int offset;

    private final DatapointType dpt;

}
