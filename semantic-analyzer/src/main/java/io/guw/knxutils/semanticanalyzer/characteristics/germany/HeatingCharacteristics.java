package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.DatapointType;
import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.meta.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static io.guw.knxutils.semanticanalyzer.characteristics.pattern.HeatingPattern.*;

@Slf4j
// pattern 1: assume GAs a created as blocks of 10 GAs (0=UpDown, 1=Stop, 2=PositionHeight, 3=PostionSlate, 4=Shadow, 5=Lock, 6=StatusPositionHeight, 7=StatusPositionSlate, 8=unassigned, 9=unassigned) TODO: adapt spec
public class HeatingCharacteristics extends GenericCharacteristics{

    Set<String> primaryKeyExclusionTerms = Set.of("sperr", "lock");

    public boolean isHeating(GroupAddress ga) {
        return ModelType.HEATING.matchesModelType(ga, groupAddressIndex.get(ga));
    }

    /*@Override
    public boolean isPrimarySwitch(GroupAddress ga) {
        // pre-select based on DPT
        boolean isPotentialPrimary = super.isPrimarySwitch(ga);
        if (!isPotentialPrimary) {
            log.debug("Not a primary switch GA due to DPT mismatch: {}", ga);
            return false;
        }

        // filter out addresses with similar name but not primary purpose (eg., status)
        GroupAddressDocument doc = groupAddressIndex.get(ga);
        if (doc == null) {
            log.warn("No index available for GA: {}", ga);
            return false;
        }

        return doc.nameTerms.stream().noneMatch(primaryKeyExclusionTerms::contains);
    }*/

    public GroupAddress findMatchingStatusCurrentTemperatureGroupAddress(GroupAddress primarySwitchGroupAddress) {
        // preselect based on common patterns
        GroupAddress candidate = findCandidate(primarySwitchGroupAddress, StateCurrentTemperature.getOffset(), StateCurrentTemperature.getDpt());
        if (candidate != null) return candidate;

        // pattern 2: status GA is in a different range
        return searchForStatusInPattern2(findMatchingCurrentTemperatureGroupAddress(primarySwitchGroupAddress), DatapointType.Temperature);
    }
    public GroupAddress findMatchingCurrentTemperatureGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, CurrentTemperature.getOffset(), CurrentTemperature.getDpt());
    }

    public GroupAddress findMatchingStatusTargetTemperatureGroupAddress(GroupAddress primarySwitchGroupAddress) {
        // preselect based on common patterns
        GroupAddress candidate = findCandidate(primarySwitchGroupAddress, StateTargetTemperature.getOffset(), StateTargetTemperature.getDpt());
        if (candidate != null) return candidate;

        // pattern 2: status GA is in a different range
        return searchForStatusInPattern2(findMatchingTargetTemperatureGroupAddress(primarySwitchGroupAddress), DatapointType.Temperature);
    }
    public GroupAddress findMatchingTargetTemperatureGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, TargetTemperature.getOffset(), TargetTemperature.getDpt());
    }

    public GroupAddress findMatchingStateModeGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, StateMode.getOffset(), StateMode.getDpt());
    }
}