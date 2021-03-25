package it.grid.storm.synchcall.data.space;

import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousGetSpaceTokensInputData extends AbstractInputData
	implements GetSpaceTokensInputData {

	private final String spaceTokenAlias;

	public AnonymousGetSpaceTokensInputData(String spaceTokenAlias) {

		if (spaceTokenAlias == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: spaceTokenAlias="
					+ spaceTokenAlias);
		}
		this.spaceTokenAlias = spaceTokenAlias;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.space.GetSpaceTokensInputData#getSpaceTokenAlias
	 * ()
	 */
	@Override
	public String getSpaceTokenAlias() {

		return spaceTokenAlias;
	}

}
