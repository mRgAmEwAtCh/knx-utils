package io.guw.knxutils.semanticanalyzer.characteristics.germany;

import io.guw.knxutils.knxprojectparser.DatapointType;
import io.guw.knxutils.knxprojectparser.GroupAddress;
import io.guw.knxutils.knxprojectparser.GroupAddressRange;
import io.guw.knxutils.semanticanalyzer.KnxProjectCharacteristicsAdapter;
import io.guw.knxutils.semanticanalyzer.luceneext.GermanAnalyzerWithDecompounder;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.*;

/**
 * Characteristics typically found in a KNX project using German language and
 * following best practises as published by KNX.org (aka. "KNX
 * Projektrichtlinien").
 */

@Slf4j
public class GenericCharacteristics extends KnxProjectCharacteristicsAdapter {

	public static class GroupAddressDocument {
		public Set<String> nameTerms;
	}

	static final Analyzer germanAnalyzer = new GermanAnalyzerWithDecompounder();
	final Map<GroupAddress, GroupAddressDocument> groupAddressIndex = new HashMap<>();
	final Map<GroupAddressRange, GroupAddressDocument> groupAddressRangeIndex = new HashMap<>();
	final Map<String, GroupAddress> groupAddressByThreePartAddress = new HashMap<>();
	final List<String> statusTerms = List.of("status", "ruckmeldung", "rm");

	boolean isMatchOnName(GroupAddress candidate, GroupAddress ga) {
		// prefix match
		// TODO: this should be configurable
		float prefixMatchRatio = calculatePrefixMatchRatio(candidate.getName(), ga.getName());
		if (prefixMatchRatio <= 0.6F) {
			log.debug(
					"Prefix mismatch for candidate {} with name '{}' comparing to primary {} with name '{}' (match {})",
					candidate, candidate.getName(), ga, ga.getName(), prefixMatchRatio);
			return false;
		}

		return true;
	}

	boolean isMatchOnNameAndDpt(GroupAddress candidate, GroupAddress primarySwitchGroupAddress, DatapointType dpt,
								DatapointType... dpts) {
		// prefix match
		if (!isMatchOnName(candidate, primarySwitchGroupAddress)) {
			return false;
		}

		// DPT match
		if ((candidate.getDatapointType() == null) || candidate.getDatapointType().isBlank()) {
			// TODO: this should be configurable
			log.warn("Accepting candidate with missing DPT {}", candidate);
			return true;
		}
		DatapointType candidateDpt = DatapointType.findByKnxProjectValue(candidate.getDatapointType());
		return (dpt == candidateDpt) || ((null != dpts) && (Arrays.stream(dpts).anyMatch((d) -> candidateDpt == d)));
	}

	float calculatePrefixMatchRatio(String candidateName, String primaryName) {
		// simple heuristic based on prefix matching
		int minLength = Math.min(candidateName.length(), primaryName.length());
		int commonPrefixLength = 0;
		for (; commonPrefixLength < minLength; commonPrefixLength++) {
			if (candidateName.charAt(commonPrefixLength) != primaryName.charAt(commonPrefixLength)) {
				break;
			}
		}
		return (float) commonPrefixLength / (float) minLength;
	}

	boolean containsStatusTerm(Set<String> terms) {
		return terms.stream().anyMatch(statusTerms::contains);
	}

	/**
	 * Returns potential candidates of a block.
	 * <p>
	 * Note, the returned list does not include the
	 * <code>startingGroupAddress</code>. Thus, it is guaranteed to has a maximum
	 * size of <code>blockLength - 1</code>. For example is start address is
	 * <code>1/0/0</code>,
	 * the result will contain <code>1/0/1</code>, <code>1/0/2</code>,
	 * <code>1/0/3</code> and <code>1/0/4</code>, assuming their names
	 * are similar enough.
	 * </p>
	 *
	 * @param startingGroupAddress a start address
	 * @param blockLength          the block length
	 * @return the candidates of a potential block with maximum block length as
	 *         specified, excluding the given start address
	 */
	List<GroupAddress> findGroupAddressBlockCandidates(GroupAddress startingGroupAddress, int blockLength) {
		List<GroupAddress> block = new ArrayList<>(blockLength);
		for (int i = 1; i < blockLength; i++) {
			GroupAddress candidate = groupAddressByThreePartAddress
					.get(GroupAddress.formatAsThreePartAddress(startingGroupAddress.getAddressInt() + i));
			if (candidate != null) {
				if (!isMatchOnName(candidate, startingGroupAddress)) {
					 log.debug(
							"Project doesn't seem to use expected block structure. Insignificant name matching for GA {} (with name '{}') and candidate GA {} with name '{}'.",
							startingGroupAddress, startingGroupAddress.getName(), candidate, candidate.getName());
					return block;
				}
				block.add(candidate);
			}
		}
		return block;
	}

	@Override
	public String findName(GroupAddress primaryGroupAddress, GroupAddress... additionalGroupAddresses) {
		String name = primaryGroupAddress.getName();
		START_AGAIN: if (additionalGroupAddresses != null) {
			for (GroupAddress ga : additionalGroupAddresses) {
				if (!ga.getName().startsWith(name)) {
					int lastSpace = name.lastIndexOf(' ');
					if (lastSpace > 0) {
						name = name.substring(0, lastSpace);
						break START_AGAIN;
					} else if (name.length() > 0) {
						name = name.substring(0, name.length() - 1);
						break START_AGAIN;
					} else {
						// give up, no common name
						return primaryGroupAddress.getName();
					}
				}
			}
		}
		return name;
	}

	GroupAddress findCandidate(GroupAddress primarySwitchGroupAddress, int offset, DatapointType dpt) {
		GroupAddress candidate = groupAddressByThreePartAddress
				.get(GroupAddress.formatAsThreePartAddress(primarySwitchGroupAddress.getAddressInt() + offset));
		if (candidate != null) {
			log.debug("Evaluating potential candidate for GA {}: {}", primarySwitchGroupAddress, candidate);
			if (isMatchOnNameAndDpt(candidate, primarySwitchGroupAddress, dpt)) {
				return candidate;
			}
		}
		return null;
	}

	Set<String> getTerms(String text) throws IOException {
		Set<String> terms = new LinkedHashSet<>(); // make sure we maintain order
		try (TokenStream ts = germanAnalyzer.tokenStream("", text)) {
			CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				terms.add(charTermAttribute.toString());
			}
			ts.end();
		}
		return terms;
	}

	private void index(GroupAddress ga) {
		try {
			GroupAddressDocument doc = new GroupAddressDocument();
			doc.nameTerms = getTerms(ga.getName());
			groupAddressIndex.put(ga, doc);

			GroupAddressRange range = ga.getGroupAddressRange();
			while ((range != null) && !groupAddressRangeIndex.containsKey(range)) {
				index(range);
				range = range.getParent();
			}
		} catch (IOException e) {
			 log.warn("Caught exception indexing GA {}", ga, e);
		}
	}

	private void index(GroupAddressRange range) {
		try {
			GroupAddressDocument rangeDoc = new GroupAddressDocument();
			rangeDoc.nameTerms = getTerms(range.getName());
			groupAddressRangeIndex.put(range, rangeDoc);
		} catch (IOException e) {
			 log.warn("Caught exception indexing group address range {}", range, e);
		}
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

		return !containsStatusTerm(doc.nameTerms);
	}

	@Override
	public void learn(List<GroupAddress> groupAddresses) {
		for (GroupAddress ga : groupAddresses) {
			index(ga);
			groupAddressByThreePartAddress.put(ga.getAddress(), ga);
		}
	}
}
