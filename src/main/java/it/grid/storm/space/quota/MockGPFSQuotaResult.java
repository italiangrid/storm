package it.grid.storm.space.quota;

public class MockGPFSQuotaResult {

	public static String getMockOutput() {

		String result = "";
		result += "*** Report for USR GRP FILESET  quotas on gemss_test";
		result += "\n";
		result += "        Block Limits                                    |                     File Limits";
		result += "\n";
		result += "Name       type             KB      quota      limit   in_doubt    grace |    files   quota    limit in_doubt    grace entryType";
		result += "\n";
		result += "root       USR        15617856          0          0      42976     none |       56       0        0       34     none i         ";
		result += "\n";
		result += "bin        USR             224          0          0         32     none |        8       0        0        1     none i        ";
		result += "\n";
		result += "nobody     USR             192          0          0          0     none |        3       0        0        0     none i         ";
		result += "\n";
		result += "storm      USR           10528          0          0          0     none |     4056       0        0        0     none i         ";
		result += "\n";
		result += "root       GRP          215648          0          0      42976     none |       31       0        0       34     none i         ";
		result += "\n";
		result += "bin        GRP        15402432          0          0         32     none |       33       0        0        1     none i         ";
		result += "\n";
		result += "nobody     GRP             192          0          0          0     none |        3       0        0        0     none i         ";
		result += "\n";
		result += "storm-SA-write GRP               0          0          0          0     none |       59       0        0        0     none i         ";
		result += "\n";
		result += "storm-SA-read GRP               0          0          0          0     none |     1982       0        0        0     none i         ";
		result += "\n";
		result += "storm      GRP           10528          0          0          0     none |     2015       0        0        0     none i         ";
		result += "\n";
		result += "root       FILESET    15618336          0          0      43008     none |       68       0        0       35     none i         ";
		result += "\n";
		result += "data1      FILESET       10272 1073741824 1073741824          0     none |     2040       0        0        0     none e         ";
		result += "\n";
		result += "data2      FILESET         192 1073741824 1073741824          0     none |     2015       0        0        0     none e         ";
		result += "\n";
		result += "*** Report for USR GRP FILESET  quotas on gemss_test2";
		result += "\n";
		result += "        Block Limits                                    |                     File Limits";
		result += "\n";
		result += "Name       type             KB      quota      limit   in_doubt    grace |    files   quota    limit in_doubt    grace entryType";
		result += "\n";
		result += "root       USR        17406944          0          0       1856     none |       62       0        0        0     none i         ";
		result += "\n";
		result += "bin        USR             256          0          0          0     none |        9       0        0        0     none i         ";
		result += "\n";
		result += "nobody     USR              96          0          0          0     none |        3       0        0        0     none i         ";
		result += "\n";
		result += "storm      USR            3616          0          0          0     none |     2039       0        0        0     none i         ";
		result += "\n";
		result += "root       GRP          151360          0          0       1856     none |       39       0        0        0     none i         ";
		result += "\n";
		result += "bin        GRP        17255840          0          0          0     none |       32       0        0        0     none i         ";
		result += "\n";
		result += "nobody     GRP              96          0          0          0     none |        3       0        0        0     none i         ";
		result += "\n";
		result += "storm-SA-write GRP            3072          0          0          0     none |       31       0        0        0     none i         ";
		result += "\n";
		result += "storm-SA-read GRP               0          0          0          0     none |        1       0        0        0     none i         ";
		result += "\n";
		result += "storm      GRP             544          0          0          0     none |     2007       0        0        0     none i         ";
		result += "\n";
		result += "root       FILESET    17410400          0          0       1856     none |       78       0        0        0     none i         ";
		result += "\n";
		result += "data3      FILESET         256          0          0          0     none |     1019       0        0        0     none i        ";
		result += "\n";
		result += "data4      FILESET         256          0          0          0     none |     1016       0        0        0     none i";
		result += "\n";
		return result;
	}

	public static String getMockOutputLs() {

		String result = "";
		result += "Block Limits                                    |     File Limits";
		result += "\n";
		result += "Filesystem type             KB      quota      limit   in_doubt    grace |    files   quota    limit in_doubt    grace  Remarks";
		result += "\n";
		result += "gemss_test FILESET         512 2147483648 2147483648          0     none |     3128       0        0        0     none ";
		return result;
	}

}
