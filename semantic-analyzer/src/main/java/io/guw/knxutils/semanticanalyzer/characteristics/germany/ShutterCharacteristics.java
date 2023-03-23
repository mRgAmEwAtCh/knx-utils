package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.DatapointType;
import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.semanticmodel.meta.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static io.guw.knxutils.semanticanalyzer.characteristics.pattern.ShutterPattern.*;

@Slf4j
// pattern 1: assume GAs a created as blocks of 10 GAs (0=UpDown, 1=Stop, 2=PositionHeight, 3=PostionSlate, 4=Shadow, 5=Lock, 6=StatusPositionHeight, 7=StatusPositionSlate, 8=unassigned, 9=unassigned)
public class ShutterCharacteristics extends GenericCharacteristics{

    Set<String> primaryKeyExclusionTerms = Set.of("sperr", "lock");

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

        return doc.nameTerms.stream().noneMatch(primaryKeyExclusionTerms::contains);
    }

    public GroupAddress findMatchingStatusPositionHeightGroupAddress(GroupAddress primarySwitchGroupAddress) {
        // preselect based on common patterns
        GroupAddress candidate = findCandidate(primarySwitchGroupAddress, StatusPositionHeight.getOffset(), StatusPositionHeight.getDpt());
        if (candidate != null) return candidate;

        // pattern 2: status GA is in a different range
        return searchForStatusInPattern2(primarySwitchGroupAddress, DatapointType.Scaling);
    }

    public GroupAddress findMatchingLockGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, Lock.getOffset(), Lock.getDpt());
    }

    public GroupAddress findMatchingStopGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, Stop.getOffset(), Stop.getDpt());
    }

    public GroupAddress findMatchingPositionHeightGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, PositionHeight.getOffset(), PositionHeight.getDpt());
    }

    public GroupAddress findMatchingPositionSlateGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, PositionSlate.getOffset(), PositionSlate.getDpt());
    }

    public GroupAddress findMatchingShadowGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, Shadow.getOffset(), Shadow.getDpt());
    }

    public GroupAddress findMatchingStatusPositionSlateGroupAddress(GroupAddress primarySwitchGroupAddress) {
        return findCandidate(primarySwitchGroupAddress, StatusPositionSlate.getOffset(), StatusPositionSlate.getDpt());
    }
}
