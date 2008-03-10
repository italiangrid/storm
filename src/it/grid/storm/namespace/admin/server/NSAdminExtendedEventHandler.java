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

public class NSAdminExtendedEventHandler
    implements ClientExtendedEventHandler {
    private static Logger logger = Logger.getLogger(NSAdminExtendedEventHandler.class.getName());

    public void handleTimeout(ClientHandler handler) throws SocketException, IOException {
        handler.sendClientMsg("-ERR Timeout");
        if (true) {
            throw new SocketException();
        }
    }

    public void handleMaxAuthTry(ClientHandler handler) throws IOException {
        handler.sendClientMsg("-ERR Max Auth Try Reached");
    }

    public boolean handleMaxConnection(ClientHandler handler) throws IOException {
        //for now lets reject all excess clients
        if (true) {
            handler.sendClientMsg("Server Busy - Max Connection Reached");
            return false;
        }
        return true;
    }
}
