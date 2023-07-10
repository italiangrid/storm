/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package org.italiangrid.storm.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import it.grid.storm.util.SURLValidator;
import org.junit.Test;

public class TestSURLValidator {

  static final String validSURLs[] = {
    "srm://host.ciccio:8444/test//palla",
    "srm://host.ciccio/manager",
    "srm://host.com:8009//srm/managerv2?SFN=/test//ciccio",
    "srm://atlasse.lnf.infn.it/dpm/lnf.infn.it/home/atlas/atlasdatadisk/rucio/mc12_8TeV/52/d4/NTUP_TRUTH.01369588._000098.root.1",
    "srm://[2001:720:1210:f023::65]:8444/srm/managerv2?SFN=/lhcp/plus+minus-ciccio,also_with_a_comma.txt",
    "srm://[2001:720:1210:f023::65]:8444/ciccio+caio_[{}]/horrible.txt"
  };

  static final String invalidSURLs[] = {
    "invalid surl",
    "http://www.google.com",
    "https://www.cicciopalla.com",
    "srm://ciccio.srm.org/file with space",
    "",
    "drop database storm_BE_ISAM"
  };

  @Test
  public void testValid() {
    for (String surl : validSURLs) {
      assertTrue("Valid SURL considered invalid: " + surl, SURLValidator.valid(surl));
    }
  }

  @Test
  public void testInvalid() {
    for (String surl : invalidSURLs) {
      assertFalse("Invalid SURL considered valid: " + surl, SURLValidator.valid(surl));
    }
  }
}
