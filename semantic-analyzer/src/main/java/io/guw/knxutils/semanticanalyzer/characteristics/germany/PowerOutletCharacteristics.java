package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.meta.ModelType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/*
  characteristics are equal so simple lights, just naming pattern changes
 */
public class PowerOutletCharacteristics extends LightCharacteristics{
    public boolean isPowerOutlet(GroupAddress ga) {
        return     ModelType.POWER_OUTLET.matchesModelType(ga, groupAddressIndex.get(ga));
    }
}
