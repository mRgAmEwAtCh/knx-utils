package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.meta.ModelType;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.joining;

@Slf4j
public class ShutterCharacteristics extends GenericCharacteristics{

    @Override
    public boolean isShutter(GroupAddress ga) {
        return ModelType.SHUTTER.matchesModelType(ga, groupAddressIndex.get(ga));
    }
}
