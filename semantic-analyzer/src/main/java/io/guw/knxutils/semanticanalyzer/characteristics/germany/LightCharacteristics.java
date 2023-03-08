package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.DatapointType;
import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.knxprojectparser.GroupAddressRange;
import io.guw.knxutils.semanticanalyzer.semanticmodel.meta.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static io.guw.knxutils.knxprojectparser.GroupAddress.*;
import static io.guw.knxutils.semanticanalyzer.characteristics.ga.pattern.LightPattern.*;
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
        List<GroupAddressRange> statusRanges = groupAddressRangeIndex.entrySet().stream()
                .filter((e) -> containsStatusTerm(e.getValue().nameTerms)).map(Map.Entry::getKey).toList();
        for (GroupAddressRange statusRange : statusRanges) {
            int part1, part2, part3;
            if (statusRange.getParent() == null) {
                part1 = getAddressPart1(statusRange.getStartInt());
                part2 = getAddressPart2(primarySwitchGroupAddress.getAddressInt());
                part3 = getAddressPart3(primarySwitchGroupAddress.getAddressInt());
            } else {
                part1 = getAddressPart1(primarySwitchGroupAddress.getAddressInt());
                part2 = getAddressPart2(statusRange.getStartInt());
                part3 = getAddressPart3(primarySwitchGroupAddress.getAddressInt());
            }
            String potentialStatusGa = formatAsThreePartAddress(part1, part2, part3);
            GroupAddress candidate = groupAddressByThreePartAddress.get(potentialStatusGa);
            if (candidate != null) {
                log.debug("Evaluating potential candidate for GA {}: {}", primarySwitchGroupAddress, candidate);
                if (isMatchOnNameAndDpt(candidate, primarySwitchGroupAddress, DatapointType.State)) {
                    log.debug("Found matching status for GA {}: {}", primarySwitchGroupAddress, candidate);
                    return candidate;
                }
            }
        }

        // give up
        return null;
    }

    @Override
    public boolean isLight(GroupAddress ga) {
        return     ModelType.LIGHT.matchesModelType(ga, groupAddressIndex.get(ga))
                || ModelType.DIMMABLE_LIGHT.matchesModelType(ga, groupAddressIndex.get(ga));
    }
}
