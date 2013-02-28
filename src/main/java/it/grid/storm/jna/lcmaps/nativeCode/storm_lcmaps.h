/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef STORM_LCMAPS_H_
#define STORM_LCMAPS_H_

/*
 * return codes: 0 success
 * 				 1 error, lcmaps initialization failed
 * 				 2 error, lcmaps_account object creation failed
 * 				 3 error, lcmaps_return_account_without_gsi call failed
 * 				 4 error, no gids provided by the lcmaps_return_account_without_gsi call
 * 				 5 error, unexpected condition, this code should be nor reachable
 * */
signed int map_user(char* logfile, char *user_dn, char **fqan_list, int nfqan, int *uid, int *gid);

#endif /* STORM_LCMAPS_H_ */

