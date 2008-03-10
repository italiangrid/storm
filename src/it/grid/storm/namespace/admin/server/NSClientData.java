/*
 * This file is part of the QuickServer library
 * Copyright (C) 2003-2005 QuickServer.org
 *
 * Use, modification, copying and distribution of this software is subject to
 * the terms and conditions of the GNU Lesser General Public License.
 * You should have received a copy of the GNU LGP License along with this
 * library; if not, you can download a copy from <http://www.quickserver.org/>.
 *
 * For questions, suggestions, bug-reports, enhancement-requests etc.
 * visit http://www.quickserver.org
 *
 */

package it.grid.storm.namespace.admin.server;

import java.util.*;

import org.quickserver.net.server.*;
import it.grid.storm.namespace.admin.common.*;

/**
 * used by NSAuthenticatorHandler
 */
public class NSClientData
    implements ClientData {

    private String lastCommandSent = null;
    private long creationDate = -1L;
    private String loginID = null;
    private String password = null;
    private String username = null;
    private String welcomeMsg = null;
    private long lastConnection = -1L;
    private String fromWhere = null;

    public void setlastCommandSent(String lastCommandSent) {
        this.lastCommandSent = lastCommandSent;
    }

    public String getlastCommandSent() {
        return lastCommandSent;
    }

    public void setLoginID(String loginId) {
        this.loginID = loginId;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setWelcomeMsg(String welcomeMsg) {
        this.welcomeMsg = welcomeMsg;
    }

    public String getWelcomeMsg() {
        return welcomeMsg;
    }

    public void setLastConnectionTime(long date) {
        this.lastConnection = date;
    }

    public long getLastConnectionTime() {
        return lastConnection;
    }

    public void setCreationDate(long date) {
        this.creationDate = date;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setFromWhere(String fromLocality) {
        if (fromLocality == null) {
            this.fromWhere = "# First access!";
        }
        else {
            this.fromWhere = fromLocality;
        }
    }

    public String getFromWhere() {
        return this.fromWhere;
    }

    public String toString() {
        String result = "==================================\n";
        result += "Login ID    : " + loginID + NSMessageUtility.newline;
        result += "User Name   : " + username + NSMessageUtility.newline;
        result += "Password    : " + password + NSMessageUtility.newline;
        result += "Welcome msg : " + welcomeMsg + NSMessageUtility.newline;
        result += "Creation    : " + new Date(creationDate) + NSMessageUtility.newline;
        result += "Last connec : " + new Date(lastConnection) + NSMessageUtility.newline;
        result += "Last from   : " + fromWhere + NSMessageUtility.newline;

        return result;
    }

}
