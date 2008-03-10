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

import org.quickserver.net.*;
import org.quickserver.net.server.*;

/**
 * PrepareHook
 * @author Akshathkumar Shetty
 */
public class PrepareHook
    implements ServerHook {
    private QuickServer namespaceServer;

    public String info() {
        return "Init Server Hook to setup logging.";
    }

    public void initHook(QuickServer quickserver) {
        this.namespaceServer = quickserver;
    }

    public boolean handleEvent(int event) {

        if (event == ServerHook.PRE_STARTUP) {
            System.out.println("Server PRE_STARTUP");
            return true;
        }
        else if (event == ServerHook.POST_STARTUP) {
            System.out.println("Server POST_STARTUP");
        }
        else if (event == ServerHook.PRE_SHUTDOWN) {
            System.out.println("Server PRE_SHUTDOWN");
        }
        else if (event == ServerHook.POST_SHUTDOWN) {
            System.out.println("Server POST_SHUTDOWN");
        }
        return false;
    }
}
