package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.DatapointType;
import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.knxprojectparser.GroupAddressRange;
import io.guw.knxutils.semanticanalyzer.characteristics.ga.pattern.ShutterPattern;
import io.guw.knxutils.semanticanalyzer.semanticmodel.meta.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.guw.knxutils.knxprojectparser.DatapointType.Scaling;
import static io.guw.knxutils.knxprojectparser.DatapointType.Switch;
import static io.guw.knxutils.knxprojectparser.GroupAddress.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static io.guw.knxutils.semanticanalyzer.characteristics.ga.pattern.ShutterPattern.*;

@Slf4j
// pattern 1: assume GAs a created as blocks of 10 GAs (0=UpDown, 1=Stop, 2=PositionHeight, 3=PostionSlate, 4=Shadow, 5=Lock, 6=StatusPositionHeight, 7=StatusPositionSlate, 8=unassigned, 9=unassigned)
public class ShutterCharacteristics extends GenericCharacteristics{

    Set<String> primaryKeyExclusionTerms = Set.of("sperr");

    @Override
    public boolean isShutter(GroupAddress ga) {
        return ModelType.SHUTTER.matchesModelType(ga, groupAddressIndex.get(ga)) || ModelType.BLINDS.matchesModelType(ga, groupAddressIndex.get(ga));
    }

    @Override
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

        return !doc.nameTerms.stream().anyMatch(primaryKeyExclusionTerms::contains);
    }

    public GroupAddress findMatchingLockGroupAddress(GroupAddress primarySwitchGroupAddress) {
        // pattern 1: assume GAs a created as blocks of 10 GAs (0=UpDown, 1=Stop, 2=PositionHeight, 3=PostionSlate, 4=Shadow, 5=Lock, 6=StatusPositionHeight, 7=StatusPositionSlate, 8=unassigned, 9=unassigned)
        // TODO: this should be configurable
        GroupAddress candidate = groupAddressByThreePartAddress
                .get(GroupAddress.formatAsThreePartAddress(primarySwitchGroupAddress.getAddressInt() + Lock.getOffset()));
        if (candidate != null) {
            log.debug("Evaluating potential candidate for GA {}: {}", primarySwitchGroupAddress, candidate);
            if (isMatchOnNameAndDpt(candidate, primarySwitchGroupAddress, Switch)) {
                return candidate;
            }
        }

        // give up
        return null;
    }

    public GroupAddress findMatchingStatusPositionHeightGroupAddress(GroupAddress primarySwitchGroupAddress) {
        // preselect based on common patterns
        GroupAddress candidate = groupAddressByThreePartAddress
                .get(GroupAddress.formatAsThreePartAddress(primarySwitchGroupAddress.getAddressInt() + StatusPositionHeight.getOffset()));
        if (candidate != null) {
            log.debug("Evaluating potential candidate for GA {}: {}", primarySwitchGroupAddress, candidate);
            if (isMatchOnNameAndDpt(candidate, primarySwitchGroupAddress, Scaling)) {
                return candidate;
            }
        }

        // pattern 2: status GA is in a different range
        List<GroupAddressRange> statusRanges = groupAddressRangeIndex.entrySet().stream()
                .filter((e) -> containsStatusTerm(e.getValue().nameTerms)).map(Map.Entry::getKey).collect(toList());
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
            candidate = groupAddressByThreePartAddress.get(potentialStatusGa);
            if (candidate != null) {
                log.debug("Evaluating potential candidate for GA {}: {}", primarySwitchGroupAddress, candidate);
                if (isMatchOnNameAndDpt(candidate, primarySwitchGroupAddress, DatapointType.Scaling)) {
                    log.debug("Found matching status for GA {}: {}", primarySwitchGroupAddress, candidate);
                    return candidate;
                }
            }
        }

        // give up
        return null;
    }
}
