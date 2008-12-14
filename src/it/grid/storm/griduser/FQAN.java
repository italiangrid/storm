package it.grid.storm.griduser;


/**
 * Represents a single FQAN.  Provides methods to access the
 * individual parts of the FQAN, as well as the standard string
 * representation of an FQAN.
 *
 * This object is essentially immutable, as it provides no setter
 * methods.
 */
public class FQAN {
    private static final String ROLE_CAP_REGEX = "(?i)/(Role|Capabilit(y|ies))=";

    protected String _vo;
    protected String _group;
    protected String _role;
    protected String _capability;


    // --- constructors --- //

    /**
     * Code common to both constructors
     */
    private void setGroupRoleAndCapabilityFromFqan(String fqan) {
		String[] group_role_cap = fqan.split(ROLE_CAP_REGEX);
		_group = group_role_cap[0];
        	_role = group_role_cap[1];
	     	_capability = group_role_cap[2];
    }

    /**
     * Constructor, taking a single FQAN passed as string; assumes VO
     * name is first part of group name.
     */
    public FQAN(final String fqan) {
		assert (null != fqan);

		_vo = fqan.split("/")[1];
		setGroupRoleAndCapabilityFromFqan(fqan);
    }

    /**
     * Constructor, taking VO name and a FQAN passed as string.
     *
     * @deprecated  It's not yet clear if the VO name is *required*
     *              to be the first part of the group name, or if this
     *              is just a convention that holds "usually" true...
     */
    public FQAN(final String vo, final String fqan) {
		assert (null != vo);
		assert (null != fqan);

		_vo = vo;

		setGroupRoleAndCapabilityFromFqan(fqan);
    }


    // --- public accessor methods --- //
    public String getVo() {
		return _vo;
    }

    public String getGroup() {
		return _group;
    }

    public String getRole() {
		return _role;
    }

    public String getCapability() {
		return _capability;
    }

    /**
     * Return the usual string representation of the FQAN.  That is,
     * if getGroup()=="egrid", getRole()=="Admin" and getCapability()=="NULL",
     * then return "/egrid/ROLE=Admin/CAPABILITY=NULL"
     */
    public String toString() {
		return getGroup()
			+ "/Role=" + getRole()
			+ "/Capability=" + getCapability();
    }
}
