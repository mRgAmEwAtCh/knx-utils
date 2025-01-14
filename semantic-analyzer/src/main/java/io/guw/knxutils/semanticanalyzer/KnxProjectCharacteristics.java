package io.guw.knxutils.semanticanalyzer;

import io.guw.knxutils.knxprojectparser.CommunicationObject;
import io.guw.knxutils.knxprojectparser.DatapointType;
import io.guw.knxutils.knxprojectparser.GroupAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * The characteristics of a KNX project.
 * <p>
 * Characteristics describe and apply patterns found within a KNX project.
 * Although those characteristics are supposed to be general applicable to
 * multiple projects, this class is designed to be contain state specific to a
 * single KNX project. Such state may be a search index or another form of
 * knowledge dictionary. Therefore it should not be re-used across multiple
 * analysis sessions of different projects.
 * </p>
 */
@Slf4j
public abstract class KnxProjectCharacteristics {

	private int warnings;

	/**
	 * Fills in missing information based on data available from the GA itself or
	 * implied by the characteristics.
	 * <p>
	 * This method will modify the specified {@link GroupAddress} directly. The
	 * default implementation will try to fill in missing
	 * {@link GroupAddress#getDatapointType()} if the GA is linked to a
	 * {@link CommunicationObject} with available DPT information.
	 * </p>
	 *
	 * @param ga the group address to complete
	 */
	public void fillInMissingInformation(GroupAddress ga) {
		for (CommunicationObject co : ga.getWritingCommunicationObjects()) {
			if (co.getDatapointType() != null) {
				if (ga.getDatapointType() == null) {
					log.debug("Update DPT to {} based on CO {} for GA {}", co.getDatapointType(), co, ga);
					ga.setDatapointType(co.getDatapointType());
				} else if (!ga.getDatapointType().equals(co.getDatapointType())) {
					log.warn("Found communication object with DPT {} which differs from expected {}\n  {}\n  GA {}",
							co.getDatapointType(), ga.getDatapointType(), co, ga);
					warnings++;
				}
			} else {
				log.warn("Found communication object without DPT\n  {}\n  GA {}", co, ga);
				warnings++;
			}
		}

		if ((ga.getName() == null) || ga.getName().isBlank()) {
			for (CommunicationObject co : ga.getWritingCommunicationObjects()) {
				if ((co.getDescription() != null) && !co.getDescription().isBlank()) {
					log.debug("Update name to '{}' based on CO {} for GA {}", co.getDescription(), co, ga);
					ga.setName(co.getDescription());
					break; // first one wins
				}
			}
		}

		if ((ga.getName() == null)
				|| ((ga.getName().isBlank()) && ((ga.getDescription() != null) && !ga.getDescription().isBlank()))) {
			log.debug("Update name to '{}' based on description for GA {}", ga.getDescription(), ga);
			ga.setName(ga.getDescription());
		}
	}

	/**
	 * Finds a status group address for the specified group address.
	 * <p>
	 * The status group address reports back status for a group address. For
	 * example, switching a the specified GA on or off would trigger a status update
	 * on the returned GA.
	 * </p>
	 * <p>
	 * In order for this method to return anything at all, all group addresses will
	 * be made available to the characteristics by calling {@link #learn(List)}.
	 * </p>
	 *
	 * @param primarySwitchGroupAddress the switch GA (as identified by
	 *                                  {@link #isPrimarySwitch(GroupAddress)}
	 * @return the matching status address (maybe <code>null</code>)
	 */
	public abstract GroupAddress findMatchingStatusGroupAddress(GroupAddress primarySwitchGroupAddress);

	/**
	 * Returns the name of a GA without potentially functional qualifiers (eg.,
	 * On/Off, etc.)
	 * <p>
	 * If additional GAs are provided the name will be verified to be a common match
	 * for all GAs. However, heuristics may be applied to allow a majority to match.
	 * </p>
	 *
	 * @param primaryGroupAddress      the primary group address
	 * @param additionalGroupAddresses additional group address to verify against
	 * @return the name
	 */
	public abstract String findName(GroupAddress primaryGroupAddress, GroupAddress... additionalGroupAddresses);

	int getWarnings() {
		return warnings;
	}

	/**
	 * Indicates if a GA is related to lighting (eg., switching, dimming, status
	 * etc.)
	 *
	 * @param ga a group address
	 * @return <code>true</code> if the specified GA is related to lighting
	 */
	public abstract boolean isLight(GroupAddress ga);

	/**
	 * Indicates if a GA is related to shutter (eg., Jalousie, Rolladen)
	 *
	 * @param ga a group address
	 * @return <code>true</code> if the specified GA is related to shutter
	 */
	public abstract boolean isShutter(GroupAddress ga);

	/**
	 * Indicates if a GA is a primary GA responsible for switching a light on/off.
	 * <p>
	 * This is useful to allow grouping of GA related to another GA. For example,
	 * controlling a light requires at least two GAs - one for on/off and another
	 * one to indicate its status. A dimmable light may require two additional GAs,
	 * eg. one for brighter or darker, another one for setting brightness directly
	 * and a third for sending the brightness status back to the KNX bus. All in all
	 * five GAs. For analysis purposes, there is one primary GA and all others a
	 * considered secondary.
	 * </p>
	 * <p>
	 * The default implementation decides based on DPT. Subclasses may override an
	 * provide a more specific implementation.
	 * </p>
	 *
	 * @param ga a group address
	 * @return <code>true</code> if the specified GA is a primary
	 */
	public boolean isPrimarySwitch(GroupAddress ga) {
		DatapointType dpt = DatapointType.findByKnxProjectValue(ga.getDatapointType());
		if (dpt == null) {
			return false;
		}

		return switch (dpt) {
			case Switch, UpDown, OpenClose -> true;
			default -> false;
		};
	}

	/**
	 * Submits a GA to the characteristics for learning purposes.
	 * <p>
	 * The characteristics implementation may build an index of the GAs for more
	 * efficient processing. Such an index may be used by <code>find...</code>
	 * methods and other
	 * methods in this class.
	 * </p>
	 *
	 * @param groupAddresses list of addresses to learn
	 */
	public abstract void learn(List<GroupAddress> groupAddresses);
}
