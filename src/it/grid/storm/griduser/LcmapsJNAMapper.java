/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.griduser;

import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.jna.lcmaps.LcmapsAccountInterface;
import it.grid.storm.jna.lcmaps.LcmapsPoolindexInterface;
import it.grid.storm.jna.lcmaps.lcmaps_account_info_t;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dibenedetto_m
 * 
 */
public class LcmapsJNAMapper implements MapperInterface
{

	private static final Object lock = new Object();
	/** To log LCMAPS failures. */
	private static final Logger log = LoggerFactory.getLogger(LcmapsJNAMapper.class);

	public LocalUser map(String dn, String[] fqans)
			throws CannotMapUserException {

		LocalUser mappedUser = null;
		synchronized(LcmapsJNAMapper.lock)
		{
			log.debug("Mapping user with dn = " + dn + " and fqans = " + ArrayUtils.toString(fqans));
			lcmaps_account_info_t account = new lcmaps_account_info_t();
			int retVal = LcmapsAccountInterface.INSTANCE.lcmaps_account_info_init(account);
			if(retVal != 0)
			{
				log.error("Unable to initialize lcmaps. Return value is " + retVal);
				throw new CannotMapUserException("Unable to initialize lcmaps. Return value is " + retVal);
			}
			int numFquans = (fqans == null ? 0 : fqans.length);
			retVal =
					 LcmapsPoolindexInterface.INSTANCE.lcmaps_return_account_without_gsi(dn, fqans,
						 numFquans, 0, account);
			if(retVal != 0)
			{
				log.error("Unable to map user dn <" + dn + "> fqans <" + ArrayUtils.toString(fqans)
					+ "> . Return value is " + retVal);
				throw new CannotMapUserException("Unable to map user dn <" + dn + "> fqans <"
					+ ArrayUtils.toString(fqans) + "> . Return value is " + retVal);
			}
			if(account.uid < 0)
			{
				log.error("Unacceptable lower than zero uid returned by Lcmaps : " + account.uid
					+ " . Mapping error");
				throw new CannotMapUserException("Unacceptable lower than zero uid returned by Lcmaps : "
					+ account.uid + " . Mapping error");
			}
			if(account.npgid < 0 || account.nsgid < 0)
			{
				log.error("Unacceptable primary or secondary gid array size returned by Lcmaps : primary = "
					+ account.npgid + ", secondary = " + account.nsgid + ",. Mapping error");
				throw new CannotMapUserException(
					"Unacceptable primary or secondary gid array size returned by Lcmaps : primary = "
						+ account.npgid + ", secondary = " + account.nsgid + ",. Mapping error");
			}
			int[] gids = null;
			int numGids = account.npgid + account.nsgid;
			if(numGids > account.npgid)
			{
				gids = new int[numGids];
				int index = 0;
				if(account.npgid > 0)
				{
					for(int gid : account.pgid_list.getPointer().getIntArray(0, account.npgid))
					{
						gids[index] = gid;
						index++;
					}
				}
				else
				{
					log.warn("No primary gid returned by Lcmaps! Mapping error");
				}
				for(int gid : account.sgid_list.getPointer().getIntArray(0, account.nsgid))
				{
					gids[index] = gid;
					index++;
				}
			}
			else
			{
				if(account.npgid > 0)
				{
					gids = account.pgid_list.getPointer().getIntArray(0, account.npgid);
				}
			}
			log.info("Mapped user to : <uid=" + account.uid + ",gids=" + ArrayUtils.toString(gids) + ">");
			mappedUser = new LocalUser(account.uid, gids, numGids);
		}
		return mappedUser;
	}
}
