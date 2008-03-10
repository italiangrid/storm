package it.grid.storm.namespace.admin.server;

import java.io.*;

import org.quickserver.net.server.*;
import it.grid.storm.namespace.admin.common.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
class NSAdminMsgUtil {

    private static NSAdminMsgUtil instance = null;

    private NSAdminMsgUtil() {
        //** Do something one time only!
    }

    private static NSAdminMsgUtil getInstance() {
        if (instance == null) {
            instance = new NSAdminMsgUtil();
        }
        return instance;
    }

    /************************************************************
     * PUBLIC methods
     */
    public static void sendMessage(ClientHandler client, String message) {
        instance = getInstance();

        NSServerResponse response = new NSServerResponse(message);
        System.out.println("rows: " + response.getNrMsgRows());
        System.out.println("ErrCode: " + response.getErrorCode());
        System.out.println("ErrMsg: " + response.getErrorMessage());
        System.out.println("MsgPrint: \n" + response.getMessageToPrint());
        try {
            client.sendClientMsg(response.responseToSend());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Unable to send the message to client");
        }
    }

}
