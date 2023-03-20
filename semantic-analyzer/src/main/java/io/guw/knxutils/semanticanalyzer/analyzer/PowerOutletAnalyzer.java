package io.guw.knxutils.semanticanalyzer.analyzer;


import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.LightCharacteristics;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.PowerOutletCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.PowerOutlet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Slf4j
public class PowerOutletAnalyzer {
    private final PowerOutletCharacteristics characteristics = new PowerOutletCharacteristics(){};

    private final List<GroupAddress> groupAddresses;

    @Getter
    private List<PowerOutlet> powerOutlets = new ArrayList<>();

    public void analyze(){
        // index all GAs
        characteristics.learn(groupAddresses);

        // find lights
        List<GroupAddress> lightGroupAddresses = groupAddresses.parallelStream().filter(characteristics::isPowerOutlet)
                .toList();

        // group light GAs based on primaries
        List<GroupAddress> primaryLightGroupAddresses = lightGroupAddresses.parallelStream()
                .filter(characteristics::isPrimarySwitch).toList();

        // build potential lights
        primaryLightGroupAddresses.forEach(this::analyzeLight);
    }

    private void analyzeLight(GroupAddress ga) {
        GroupAddress statusGa = characteristics.findMatchingStatusGroupAddress(ga);
        if (statusGa == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        String name = characteristics.findName(ga, statusGa);
        powerOutlets.add(PowerOutlet.builder()
                .name(name)
                .primarySwitchGroupAddress(ga)
                .statusGroupAddress(statusGa)
                .build()
        );
    }
}
