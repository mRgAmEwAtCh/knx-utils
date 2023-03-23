package io.guw.knxutils.semanticanalyzer.analyzer;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.HeatingCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Heating;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class HeatingAnalyzer {
    private final HeatingCharacteristics characteristics = new HeatingCharacteristics();

    private final List<GroupAddress> groupAddresses;

    @Getter
    private List<Heating> heatings = new ArrayList<>();

    public void analyze(){
        // index all GAs
        characteristics.learn(groupAddresses);

        // find shutters
        List<GroupAddress> shutterGroupAddresses = groupAddresses.parallelStream().filter(characteristics::isHeating)
                .toList();

        // group shutters GAs based on primaries
        List<GroupAddress> primaryShutterGroupAddresses = shutterGroupAddresses.parallelStream()
                .filter(characteristics::isPrimarySwitch).toList();

        // build potential shutters
        primaryShutterGroupAddresses.forEach(this::analyzeHeater);
    }

    private void analyzeHeater(GroupAddress ga) {
        GroupAddress currentTemperature = characteristics.findMatchingCurrentTemperatureGroupAddress(ga);
        if (currentTemperature == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress statusCurrentTemperature = characteristics.findMatchingStatusCurrentTemperatureGroupAddress(ga);
        if (statusCurrentTemperature == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress targetTemperature = characteristics.findMatchingTargetTemperatureGroupAddress(ga);
        if (targetTemperature == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress statusTargetTemperature = characteristics.findMatchingStatusTargetTemperatureGroupAddress(ga);
        if (statusTargetTemperature == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress stateMode = characteristics.findMatchingStateModeGroupAddress(ga);
        if (stateMode == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        //TODO switch between shutter and blinds
        String name = characteristics.findName(ga, currentTemperature);

        heatings.add(Heating.builder()
                .name(name)
                .primarySwitchGroupAddress(ga)
                .currentTemperatureGa(currentTemperature)
                .statusCurrentTemperatureGa(statusCurrentTemperature)
                .targetTemperatureGa(targetTemperature)
                .statusTargetTemperatureGa(statusTargetTemperature)
                .stateModeGa(stateMode)
                .build()
        );
    }
}
