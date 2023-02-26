package io.guw.knxutils.semanticanalyzer.characteristics.ga.pattern;

import lombok.Getter;

/**
 * pattern 1: assume GAs a created as blocks of 10 GAs (0=UpDown, 1=Stop, 2=PositionHeight, 3=PostionSlate, 4=Shadow, 5=Lock, 6=StatusPositionHeight, 7=StatusPositionSlate, 8=unassigned, 9=unassigned)
 */
public enum ShutterPattern {
    UpDown(0),
    Stop(1),
    PositionHeight(2),
    PostionSlate(3),
    Shadow(4),
    Lock(5),
    StatusPositionHeight(6),
    StatusPositionSlate(7),
    ;

    @Getter
    private final int offset;

    ShutterPattern(int offset){
        this.offset = offset;
    }
}
