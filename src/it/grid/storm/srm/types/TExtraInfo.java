/**
 * This class represents the TExtraInfo additional data associated with the SRM request.
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.util.HashMap;
import java.util.Map;

public class TExtraInfo {

    public static String PNAME_EXTRAINFO = "extraInfo";

    private static String PNAME_KEY = "key";
    private static String PNAME_VALUE = "value";

    private String key = null;
    private String value = null;

    public TExtraInfo(){
      this.key = "unknown";
      this.value = "N/A";
    }

    public TExtraInfo(String key, String value)
			throws InvalidTExtraInfoAttributeException {

		if(key == null)
		{
			throw new InvalidTExtraInfoAttributeException(key);
		}
		this.key = key;
		this.value = value;
	}

    /**
     * @param inputParam
     * @param name
     * @return
     * @throws InvalidTExtraInfoAttributeException
     */
    public static TExtraInfo decode(Map inputParam, String name)
			throws InvalidTExtraInfoAttributeException {

		String k, val;
		Map param = (Map) inputParam.get(name);
		k = (String) param.get(TExtraInfo.PNAME_KEY);
		val = (String) param.get(TExtraInfo.PNAME_VALUE);
		return new TExtraInfo(k, val);
	}

    /**
     * @param param
     * @return
     * @throws InvalidTExtraInfoAttributeException
     */
    public static TExtraInfo decode(Map param)
			throws InvalidTExtraInfoAttributeException {

		String k, val;
		k = (String) param.get(TExtraInfo.PNAME_KEY);
		val = (String) param.get(TExtraInfo.PNAME_VALUE);
		return new TExtraInfo(k, val);
	}

    /**
     * @param outputParam
     * @param fieldName
     */
    public void encode(Map outputParam, String fieldName) {

		HashMap<String, String> param = new HashMap<String, String>();
		this.encode(param);
		outputParam.put(fieldName, param);
	}

    /**
     * @param outputParam
     */
    public void encode(Map outputParam) {

		outputParam.put(TExtraInfo.PNAME_KEY, (String) key);
		outputParam.put(TExtraInfo.PNAME_VALUE, (String) value);
	}

    public String toString() {
      return "<'"+this.key+"','"+this.value+"'>";
    }
}



