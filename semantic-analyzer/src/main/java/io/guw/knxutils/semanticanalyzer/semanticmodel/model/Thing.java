package io.guw.knxutils.semanticanalyzer.semanticmodel.model;

import io.guw.knxutils.knxprojectparser.GroupAddress;

public interface Thing {

    String getName();

    GroupAddress getPrimarySwitchGroupAddress();
}
