package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.DatapointType;
import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.knxprojectparser.GroupAddressRange;
import io.guw.knxutils.semanticanalyzer.semanticmodel.meta.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static io.guw.knxutils.knxprojectparser.DatapointType.ControlDimming;
import static io.guw.knxutils.knxprojectparser.DatapointType.Scaling;
import static io.guw.knxutils.knxprojectparser.GroupAddress.*;
import static io.guw.knxutils.semanticanalyzer.characteristics.ga.pattern.LightPattern.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
/**
 * characteristics are equal so simple lights, just naming pattern changes
 */
public class PowerOutletCharacteristics extends LightCharacteristics{
    public boolean isPowerOutlet(GroupAddress ga) {
        return     ModelType.POWER_OUTLET.matchesModelType(ga, groupAddressIndex.get(ga));
    }
}
