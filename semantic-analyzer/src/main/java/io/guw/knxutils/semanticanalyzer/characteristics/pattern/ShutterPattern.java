package io.guw.knxutils.semanticanalyzer.characteristics.pattern;

import io.guw.knxutils.knxprojectparser.DatapointType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static io.guw.knxutils.knxprojectparser.DatapointType.*;

/**
 * pattern 1: assume GAs a created as blocks of 10 GAs (0=UpDown, 1=Stop, 2=PositionHeight, 3=PostionSlate, 4=Shadow, 5=Lock, 6=StatusPositionHeight, 7=StatusPositionSlate, 8=unassigned, 9=unassigned)
 */
@RequiredArgsConstructor
@Getter
public enum ShutterPattern {
    UpDown(0, DatapointType.UpDown),
    Stop(1, MoveStep),
    PositionHeight(2, Scaling),
    PostionSlate(3, Scaling),
    Shadow(4, Switch),
    Lock(5, Switch),
    StatusPositionHeight(6, Scaling),
    StatusPositionSlate(7, Scaling),
    ;

    private final int offset;

    private final DatapointType dpt;

}
