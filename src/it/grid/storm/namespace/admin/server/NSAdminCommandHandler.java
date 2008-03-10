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

import java.io.*;
import java.net.*;
import java.util.logging.*;

import org.quickserver.net.server.*;
import it.grid.storm.common.resource.*;

public class NSAdminCommandHandler
    implements ClientCommandHandler, ClientEventHandler {
    private static Logger logger = Logger.getLogger(NSAdminCommandHandler.class.getName());

    //--ClientEventHandler
    public void gotConnected(ClientHandler handler) throws SocketTimeoutException, IOException {
        logger.fine("Connection opened : " + handler.getHostAddress());

        int rh = NSAdminServer.serverBundleHandle;
        String msg = BundleHandler.getString(rh, "server.welcome");

        NSAdminMsgUtil.sendMessage(handler, msg);
        logger.fine("Sent welcome message, starting handshake protocol");

    }

    public void lostConnection(ClientHandler handler) throws IOException {
        handler.sendSystemMsg("Connection lost : " + handler.getSocket().getInetAddress(), Level.FINE);
    }

    public void closingConnection(ClientHandler handler) throws IOException {
        logger.fine("Connection closed : " + handler.getSocket().getInetAddress());
    }

    /**
     *
     * @param handler ClientHandler
     * @param command String
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public void handleCommand(ClientHandler handler, String commandLine) throws SocketTimeoutException, IOException {
        int rh = NSAdminServer.serverBundleHandle;
        //QUIT COMMAND HANDLE
        if (commandLine.toLowerCase().equals("quit")) {
            String msg = BundleHandler.getString(rh, "server.command.quit.goodbye_msg");
            NSAdminMsgUtil.sendMessage(handler, msg);
            handler.closeConnection();
        }
        else if (commandLine.toLowerCase().equals("quit")) {

            /** @todo manage other commands! */

        }
    }

}
