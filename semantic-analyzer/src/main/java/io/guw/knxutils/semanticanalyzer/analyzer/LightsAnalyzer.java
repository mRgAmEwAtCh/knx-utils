package io.guw.knxutils.semanticanalyzer.analyzer;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.LightCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.DimmableLight;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Light;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Slf4j
public class LightsAnalyzer {
    private final LightCharacteristics characteristics = new LightCharacteristics();

    private final List<GroupAddress> groupAddresses;

    @Getter
    private List<Light> lights = new ArrayList<>();

    public void analyze(){
        // index all GAs
        characteristics.learn(groupAddresses);

        // find lights
        List<GroupAddress> lightGroupAddresses = groupAddresses.parallelStream().filter(characteristics::isLight)
                .collect(toList());

        // group light GAs based on primaries
        List<GroupAddress> primaryLightGroupAddresses = lightGroupAddresses.parallelStream()
                .filter(characteristics::isPrimarySwitch).collect(toList());

        // build potential lights
        primaryLightGroupAddresses.forEach(this::analyzeLight);
    }

    private void analyzeLight(GroupAddress ga) {
        GroupAddress statusGa = characteristics.findMatchingStatusGroupAddress(ga);
        if (statusGa == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        GroupAddress dimGa = characteristics.findMatchingDimGroupAddress(ga);
        GroupAddress brightnessGa = characteristics.findMatchingBrightnessGroupAddress(ga);
        GroupAddress brightnessStatusGa = characteristics.findMatchingBrightnessStatusGroupAddress(ga);
        if ((dimGa != null) && (brightnessGa != null) && (brightnessStatusGa != null)) {
            // use dimmable light
            String name = characteristics.findName(ga, statusGa, dimGa, brightnessGa, brightnessStatusGa);
            lights.add(DimmableLight.builder()
                    .name(name)
                    .primarySwitchGroupAddress(ga)
                    .statusGroupAddress(statusGa)
                    .dimGa(dimGa)
                    .brightnessStatusGa(brightnessStatusGa)
                    .brightnessGa(brightnessGa)
                    .build()
            );
        } else {
            // go with simple light
            String name = characteristics.findName(ga, statusGa);
            lights.add(Light.builder()
                    .name(name)
                    .primarySwitchGroupAddress(ga)
                    .statusGroupAddress(statusGa)
                    .build()
            );
        }
    }
}
