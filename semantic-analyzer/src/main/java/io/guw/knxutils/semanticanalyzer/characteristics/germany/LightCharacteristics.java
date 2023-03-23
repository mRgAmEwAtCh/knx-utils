package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.DatapointType;
import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.meta.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static io.guw.knxutils.semanticanalyzer.characteristics.pattern.LightPattern.*;
import static java.util.stream.Collectors.joining;

/**
 * pattern 1: assume GAs a created as blocks of 5 GAs (0=OnOff, 1=Dim, 2=Value, 3=StatusOnOff, 4=StatusValue)
 */
@Slf4j
public class LightCharacteristics extends GenericCharacteristics{

     public GroupAddress findMatchingBrightnessGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, Value.getOffset(), Value.getDpt());
    }

    public GroupAddress findMatchingDimGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, Dim.getOffset(), Dim.getDpt());
    }

    public GroupAddress findMatchingBrightnessStatusGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, StatusValue.getOffset(), StatusValue.getDpt());
    }

    @Override
    public GroupAddress findMatchingStatusGroupAddress(GroupAddress primarySwitchGroupAddress) {
        // preselect based on common patterns
        List<GroupAddress> candidates = findGroupAddressBlockCandidates(primarySwitchGroupAddress, 5).stream()
                .filter(ga -> DatapointType.findByKnxProjectValue(ga.getDatapointType()) == DatapointType.State)
                .toList();
        if (!candidates.isEmpty()) {
            if (candidates.size() == 1) {
                GroupAddress candidate = candidates.get(0);
                log.debug("Found matching status for GA {}: {}", primarySwitchGroupAddress, candidate);
                return candidate;
            }
            log.warn("Project is ambiguous. Found multiple matches with DPT {} for GA {}: {}", DatapointType.State,
                    primarySwitchGroupAddress, candidates.stream().map(GroupAddress::toString).collect(joining(", ")));
        } else {
            log.debug("No candidate indentified with DPT {} based on block pattern for GA {}", DatapointType.State,
                    primarySwitchGroupAddress);
        }

        // pattern 2: status GA is in a different range
        return searchForStatusInPattern2(primarySwitchGroupAddress, DatapointType.State);
    }

    @Override
    public boolean isLight(GroupAddress ga) {
        return     ModelType.LIGHT.matchesModelType(ga, groupAddressIndex.get(ga))
                || ModelType.DIMMABLE_LIGHT.matchesModelType(ga, groupAddressIndex.get(ga));
    }
}
