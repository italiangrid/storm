package it.grid.storm.namespace.admin.common;

import java.io.*;
import java.util.*;

public class NSServerResponse {

    private byte errorCode = -1;
    private String errorMessage = "Undefined. Please, consult error logs.";
    private int nrMsgRows = -1;
    private String messageToSend = null;
    private String messageToPrint = null;

    public NSServerResponse() {
    }

    public NSServerResponse(String msgToSend) {
        this.errorCode = 0; //No error
        this.errorMessage = "No error.";
        this.messageToSend = msgToSend;
        this.nrMsgRows = countNrRows();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(byte code) {
        this.errorCode = code;
    }

    public void setErrorMessage(String errMsg) {
        this.errorMessage = errMsg;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getNrMsgRows() {
        if (nrMsgRows < 0) {
            nrMsgRows = countNrRows();
        }
        return nrMsgRows;
    }

    public void setNrMsgRows(int nrRows) {
        this.nrMsgRows = nrRows;
    }

    public String getMessageToSend() {
        return messageToSend;
    }

    public String getMessageToPrint() {
        if (this.messageToPrint == null) {
            setMessageToPrint(this.messageToSend);
        }
        return this.messageToPrint;
    }

    public Vector getMessageRows() {
        Vector result = new Vector(nrMsgRows);
        Object[] msgRows = messageInRow();
        for (int i = 0; i < msgRows.length; i++) {
            result.add( (String) msgRows[i]);
        }
        return result;
    }

    public void setMessageToSend(String msg) {
        this.messageToSend = msg;
        this.nrMsgRows = countNrRows();
    }

    public void setMessageToPrint(Vector msg) {

        if (msg != null) {
            int nrRow = msg.size();
            String rowTotal = "" + nrRow;
            StringBuffer row = new StringBuffer();
            for (int rowIndex = 0; rowIndex < nrRow; rowIndex++) {
                row.append(foreword(rowIndex + 1, rowTotal));
                row.append( (String) msg.elementAt(rowIndex));
                row.append(NSMessageUtility.newline);
            }
            this.messageToPrint = row.toString();
        }
    }

    public void setMessageToPrint(String msgToSend) {
        if (msgToSend != null) {
            String rowTotal = "" + getNrMsgRows();
            int rowCount = 1;
            StringBuffer sb = new StringBuffer();
            String row;
            StringTokenizer msgParser = new StringTokenizer(msgToSend, NSMessageUtility.newline);
            while (msgParser.hasMoreTokens()) {
                row = msgParser.nextToken();
                sb.append(foreword(rowCount, rowTotal));
                sb.append('\'' + row + '\'');
                sb.append(NSMessageUtility.newline);
                rowCount++;
            }
            this.messageToPrint = sb.toString();
        }
    }

    private StringBuffer foreword(int row, String total) {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append("" + row);
        sb.append('/');
        sb.append(total);
        sb.append(']');
        sb.append(' ');
        return sb;
    }

    private int countNrRows() {
        int result = 0;
        if ( (this.messageToPrint == null) && (this.messageToSend == null)) {
            return result;
        }
        else {
            String msg = messageToPrint == null ? messageToSend : messageToPrint;
            StringTokenizer msgParser = new StringTokenizer(msg, NSMessageUtility.newline);
            while (msgParser.hasMoreTokens()) {
                msgParser.nextToken();
                result++;
            }

        }
        return result;
    }

    public String responseToSend() {
        String result = "";
        StringBuffer sb = new StringBuffer();
        //ISO Control Char : Start Of Heading (SOH)
        sb.append(NSMessageUtility.SOH);
        //Adding the Error Code (if ErrCode!=0 add the message)
        sb.append(encodeErrorCode(this.errorCode));
        if (errorCode != 0) { //Append the error message
            sb.append(errorMessage);
        }
        sb.append(NSMessageUtility.newline);
        //Adding the message
        sb.append(NSMessageUtility.STX);
        Object[] msgRows = messageInRow();
        for (int i = 0; i < msgRows.length; i++) {
            sb.append( (String) msgRows[i]);
            sb.append(NSMessageUtility.newline);
        }
        //Adding the last row (end of message)
        sb.append(NSMessageUtility.ETX);
        sb.append(NSMessageUtility.EOT);
        result = sb.toString();
        return result;
    }

    private Object[] messageInRow() {
        Vector resVector = new Vector(getNrMsgRows());
        int index = 0;
        String row;
        StringTokenizer msgParser = new StringTokenizer(this.messageToSend, NSMessageUtility.newline);
        while (msgParser.hasMoreTokens()) {
            row = msgParser.nextToken();
            resVector.add(index, row);
            index++;
        }
        return resVector.toArray();
    }

    private char encodeErrorCode(byte errorCode) {

        byte[] bb = {
            errorCode};
        String str = "-";
        try {
            str = new String(bb, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        char result = str.charAt(0);
        return result;

        /**
             ByteBuffer bbuf = ByteBuffer.allocate(1);
             bbuf.put(errorCode);
             CharBuffer result = null;
             // Create the encoder for ISO-8859-1
             Charset charset = Charset.forName("ISO-8859-1");
             CharsetDecoder decoder = charset.newDecoder();
             //CharsetEncoder encoder = charset.newEncoder();
             try {
                 // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
                 // The new ByteBuffer is ready to be read.
                 result = decoder.decode(bbuf);

             }
             catch (CharacterCodingException e) {
                 e.printStackTrace();
             }
             if (result != null) {
                 System.out.println("Lenght of CharBuffer of Error Code: "+result.length());
                 return result.array()[0];
             }
             else {
                 return '-';
             }
         **/
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (messageToPrint == null) {
            setMessageToPrint(this.messageToSend);
        }
        //*** First ROW is
         //  - ERR-<code> :  <error message>
         //  - EXEC-OK
         if (errorCode != 0) {
             sb.append("[err] ERR-" + errorCode + " : " + errorMessage);
         }
         else {
             sb.append("[ack] EXEC-OK");
         }
        sb.append(NSMessageUtility.newline);
        //**** Build message
         sb.append(getMessageToPrint());
        return sb.toString();
    }

    public static void main(String[] args) {
        NSServerResponse ns = new NSServerResponse();
        //            012345     67                         8901234567890123456789012345678901234     56
        String msg = "ciao #" + NSMessageUtility.newline + "questa è la prova di un messaggio. #"; //+ns.newline;
        /**
                 System.out.println("MSG : " + msg);
                 System.out.println("Nr ROW : " + ns.countNrRows(msg));
                 System.out.println("Last index : " + msg.lastIndexOf(NSMessageUtility.newline));
         **/
        ns = new NSServerResponse(msg);
        ns.setMessageToPrint(msg);
        System.out.println(ns.getMessageToPrint());

        Vector vMsg = new Vector();
        vMsg.add("Prima linea del messaggio");
        vMsg.add("Seconda linea del messaggio");
        ns.setMessageToPrint(vMsg);
        byte ec = 1;
        ns.setErrorCode(ec);
        ns.setErrorMessage("ERORRERE ");
        System.out.println(ns.toString());

        Vector vMsg2 = new Vector();
        vMsg2.add("Prima linea del messaggio");
        vMsg2.add("Seconda linea del messaggio");
        ns.setMessageToPrint(vMsg2);
        byte ec2 = 0;
        ns.setErrorCode(ec2);
        ns.setErrorMessage("ERORRERE ");
        System.out.println(ns.toString());

    }

}
