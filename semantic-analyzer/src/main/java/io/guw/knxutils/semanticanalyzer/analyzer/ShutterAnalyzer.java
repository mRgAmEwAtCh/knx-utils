package io.guw.knxutils.semanticanalyzer.analyzer;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.ShutterCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Blinds;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Shutter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ShutterAnalyzer {
    private final ShutterCharacteristics characteristics = new ShutterCharacteristics();

    private final List<GroupAddress> groupAddresses;

    @Getter
    private List<Shutter> shutters = new ArrayList<>();

    public void analyze(){
        // index all GAs
        characteristics.learn(groupAddresses);

        // find shutters
        List<GroupAddress> shutterGroupAddresses = groupAddresses.parallelStream().filter(characteristics::isShutter)
                .toList();

        // group shutters GAs based on primaries
        List<GroupAddress> primaryShutterGroupAddresses = shutterGroupAddresses.parallelStream()
                .filter(characteristics::isPrimarySwitch).toList();

        // build potential shutters
        primaryShutterGroupAddresses.forEach(this::analyzeShutter);
    }

    private void analyzeShutter(GroupAddress ga) {
        GroupAddress statusPositionHeight = characteristics.findMatchingStatusPositionHeightGroupAddress(ga);
        if (statusPositionHeight == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress stopGa = characteristics.findMatchingStopGroupAddress(ga);
        if (stopGa == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress positionHeight = characteristics.findMatchingPositionHeightGroupAddress(ga);
        if (positionHeight == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress lockGa = characteristics.findMatchingLockGroupAddress(ga);
        if (lockGa == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress slatePosition = characteristics.findMatchingPositionSlateGroupAddress(ga);
        GroupAddress shadow = characteristics.findMatchingShadowGroupAddress(ga);
        GroupAddress statusSlatePosition = characteristics.findMatchingStatusPositionSlateGroupAddress(ga);

        //TODO switch between shutter and blinds
        String name = characteristics.findName(ga, statusPositionHeight);

        if (slatePosition != null && shadow != null && statusSlatePosition != null){
            shutters.add(Blinds.builder()
                    .name(name)
                    .primarySwitchGroupAddress(ga)
                    .stopGroupAddress(stopGa)
                    .positionHeightGroupAddress(positionHeight)
                    .lockGroupAddress(lockGa)
                    .statusPositionHeightGroupAddress(statusPositionHeight)
                    .positionSlateGroupAddress(slatePosition)
                    .statusPositionSlateGroupAddress(statusSlatePosition)
                    .shadowGroupAddress(shadow)
                    .build()
            );
        }else{
            shutters.add(Shutter.builder()
                    .name(name)
                    .primarySwitchGroupAddress(ga)
                    .stopGroupAddress(stopGa)
                    .positionHeightGroupAddress(positionHeight)
                    .lockGroupAddress(lockGa)
                    .statusPositionHeightGroupAddress(statusPositionHeight)
                    .build()
            );
        }
    }
}
