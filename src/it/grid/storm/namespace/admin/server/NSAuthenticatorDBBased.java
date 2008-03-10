package it.grid.storm.namespace.admin.server;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.quickserver.net.*;
import org.quickserver.net.server.*;
import it.grid.storm.namespace.admin.common.*;

public class NSAuthenticatorDBBased
    extends QuickAuthenticationHandler {
    private static Logger logger = Logger.getLogger(NSAuthenticatorDBBased.class.getName());

    public AuthStatus askAuthentication(ClientHandler handler) throws IOException, AppException {
        NSClientData data = (NSClientData) handler.getClientData();
        data.setlastCommandSent("U");
        handler.sendClientMsg("User Name :");
        logger.finest("ASK AUTHENTICATION");
        return null;
    }

    public AuthStatus handleAuthentication(ClientHandler handler, String command) throws IOException, AppException {
        NSClientData data = (NSClientData) handler.getClientData();
        if (data.getlastCommandSent().equals("U")) {
            data.setLoginID(command);
            data.setlastCommandSent("P");
            handler.sendClientMsg("Password :");
            logger.finest("HANDLE AUTHENTICATION");
        }
        else if (data.getlastCommandSent().equals("P")) {
            data.setPassword(command);
            logger.finest("HANDLE AUTHORIZATION");
            String loginId = data.getLoginID();
            String pwd = data.getPassword();
            logger.finest("  LOGIN : '" + loginId + "' PASSWD : '" + pwd + "'");
            if (validate(handler, loginId, pwd)) {
                handler.sendClientMsg("Auth OK");
                logger.finest("AUTH OK");
                data.setPassword(null);

                //Send to client some information rows
                sendGreetings(handler);

                return AuthStatus.SUCCESS;
            }
            else {
                handler.sendClientMsg("Auth Failed");
                data.setPassword(null);
                logger.finest("AUTH FAILED");
                return AuthStatus.FAILURE;
            }
        }
        else {
            throw new AppException("Unknown LastAsked!");
        }

        return null;
    }

    /**
     * sendClientWelcome
     */
    private void sendGreetings(ClientHandler handler) {
        //Build message
        NSClientData data = (NSClientData) handler.getClientData();
        String welcomeMsg = data.getWelcomeMsg() + " " + data.getUsername();
        String hostAddressMsg = "Your HOST Address is " + handler.getHostAddress();
        String lastMsg = "Your last connection was in " + new Date(data.getLastConnectionTime());
        lastMsg += " from " + data.getFromWhere();
        String responseMsg = welcomeMsg + NSMessageUtility.newline;
        responseMsg += hostAddressMsg + NSMessageUtility.newline;
        responseMsg += lastMsg + NSMessageUtility.newline;
        //Send message to Client
        NSAdminMsgUtil.sendMessage(handler, responseMsg);
        //Update data of Client into DB
        long newAccessTime = System.currentTimeMillis();
        NSAdminDBUtil.updateAuthorizedClientData(data, newAccessTime, handler.getHostAddress());

    }

    protected static boolean validate(ClientHandler handler, String loginId, String password) {
        boolean authz = false;
        authz = NSAdminDBUtil.retrieveUser(handler, loginId, password);
        return authz;
    }
}
