package io.guw.knxutils.semanticanalyzer.analyzer;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.semanticanalyzer.characteristics.germany.ShutterCharacteristics;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.DimmableLight;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Light;
import io.guw.knxutils.semanticanalyzer.semanticmodel.model.Shutter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
        List<GroupAddress> lightGroupAddresses = groupAddresses.parallelStream().filter(characteristics::isShutter)
                .collect(toList());

        // group shutters GAs based on primaries
        List<GroupAddress> primaryLightGroupAddresses = lightGroupAddresses.parallelStream()
                .filter(characteristics::isPrimarySwitch).collect(toList());

        // build potential shutters
        primaryLightGroupAddresses.forEach(this::analyzeShutter);
    }

    private void analyzeShutter(GroupAddress ga) {
        GroupAddress statusGa = characteristics.findMatchingStatusGroupAddress(ga);
        if (statusGa == null) {
            log.debug("Unable to find matching status GA for GA {} ({})", ga, ga.getName());
            return;
        }

        String name = characteristics.findName(ga, statusGa);
        shutters.add(Shutter.builder()
                .name(name)
                .primarySwitchGroupAddress(ga)
                .build()
        );
    }
}
