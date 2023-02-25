package io.guw.knxutils.semanticanalyzer;

import io.guw.knxutils.knxprojectparser.GroupAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class KnxProjectCharacteristicsAdapter extends KnxProjectCharacteristics {

	@Override
	public GroupAddress findMatchingBrightnessGroupAddress(GroupAddress primarySwitchGroupAddress) {
		return null;
	}

	@Override
	public GroupAddress findMatchingBrightnessStatusGroupAddress(GroupAddress primarySwitchGroupAddress) {
		return null;
	}

	@Override
	public GroupAddress findMatchingDimGroupAddress(GroupAddress primarySwitchGroupAddress) {
		return null;
	}

	@Override
	public GroupAddress findMatchingStatusGroupAddress(GroupAddress primarySwitchGroupAddress) {
		return null;
	}

	@Override
	public String findName(GroupAddress primaryGroupAddress, GroupAddress... additionalGroupAddresses) {
		return null;
	}

	@Override
	public boolean isLight(GroupAddress ga) {
		return false;
	}

	@Override
	public boolean isShutter(GroupAddress ga) {
		return false;
	}

	@Override
	public void learn(List<GroupAddress> groupAddresses) {

	}
}
