package io.guw.knxutils.semanticanalyzer.openhab.annotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OpenhabKnxBindingType {
    SWITCH("switch"),
    DIMMER("dimmer"),
    ROLLER_SHUTTER("rollershutter"),
    CONTACT("contact"),
    NUMBER("number"),
    STRING("string"),
    DATETIME("datetime"),
    ;

    final String name;
}
