package it.grid.storm.namespace.admin.client;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;

import it.grid.storm.namespace.admin.common.*;

public class NSServerConnection {

    private static final int JAVA_BUFFER_SIZE = 1024;
    private final static String IN_TRAFFIC = "SERVER says     : '";
    private final static String OUT_TRAFFIC = "CLIENT response : '";
    private final static String COMMENT = " /** note   : ";

    //Status of connection
    private boolean connected = false;
    private boolean transmitting = false;

    //Reconnect attempt values
    private int maxReconnectAttempts = 0;
    private int reconnectAttempt = 0;
    private boolean allowReconnects = true;

    private static final Logger logger = Logger.getLogger("it.grid.storm.namespace.admin.client");
    static {
        System.setProperty("java.util.logging.config.file", "./etc/adminserver-log.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        }
        catch (Exception e) {
            System.err.println("Could not read logging configuration: " + e.getMessage());
            System.err.println("Proceeding...");
        }
    }

    //Connection parameter
    private ConnectionParameter connParam = null;

    //Connection classes
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    //Message
    private String serverVersion;

    /**
     * Default constructor
     */
    public NSServerConnection() throws IOException {

    }

    /**
     * Default TEST constructor
     */
    public NSServerConnection(boolean test) throws IOException {
        //Load connection parameter
        /**
         * @todo read from NSClient configuration
         * */
        String host = "localhost";
        int port = 4123;
        String loginId = "ritz";
        String userName = "Riccardo Zappi";
        String password = "ciccio";
        connParam = new ConnectionParameter(host, port, loginId, userName, password);
    }

    public NSServerConnection(ConnectionParameter param) {
        connParam = param;
    }

    public ConnectionParameter getConnectionParameter() {
        if (connParam == null) {
            connParam = new ConnectionParameter();
        }
        return connParam;
    }

    public void setConnectionParameter(ConnectionParameter param) {
        this.connParam = param;
    }

    /**
     *
     * @throws IOException
     */
    public void connect() throws IOException {

        logger.info("Connecting to " + connParam.getHost() + ":" + connParam.getPort());
        Inet4Address serverAddress = connParam.getServerAddress();
        logger.info("Hostname resolved: " + serverAddress);
        socket = new Socket();
        socket.setKeepAlive(true);
        InetSocketAddress server = new InetSocketAddress(serverAddress, connParam.getPort());
        socket.connect(server);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()), JAVA_BUFFER_SIZE);
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()), JAVA_BUFFER_SIZE);

        //Retrieve Server Banner
        String banner = retrieveBanner();
        logger.info(NSMessageUtility.newline + banner);

        //Handshake Phase
        String handshakeResult = handshake();
        logger.info(NSMessageUtility.newline + handshakeResult);
        /** @todo complete the case of
         *   1. 'Auth OK' msg
         *   2. 'Auth Failed' msg
         * */

        //Retrieve Greetings
        String greetings = retrieveGreetings();
        logger.info(NSMessageUtility.newline + greetings);

    }

    private String retrieveBanner() throws IOException {
        String banner = " --- no banner ---";
        try {
            NSServerResponse response = fetchServerResponse();
            banner = response.getMessageToPrint();
        }
        catch (CommandException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return banner;
    }

    /**
     *
     * @return String
     * @throws IOException
     */
    private String handshake() throws IOException {

        String handshakeResult;
        logger.finer(COMMENT + "=== Handshake phase ===");

        String logiIdAsk = in.readLine();
        logger.info(IN_TRAFFIC + logiIdAsk + "'");
        //Sending login ID
        sendResponse(connParam.getLoginId());

        String userPwdAsk = in.readLine();
        logger.info(IN_TRAFFIC + userPwdAsk + "'");
        //Sending user password
        sendResponse(connParam.getPassword());

        handshakeResult = in.readLine();
        logger.info(IN_TRAFFIC + handshakeResult + "'");

        return handshakeResult;
    }

    private String retrieveGreetings() throws IOException {
        String greetings = " --- no greetings ---";
        try {
            NSServerResponse response = fetchServerResponse();
            greetings = response.getMessageToPrint();
        }
        catch (CommandException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return greetings;
    }

    /**
     *
     * @param response String
     * @throws IOException
     */
    private void sendResponse(String response) throws IOException {
        if (response != null) {
            logger.finer(OUT_TRAFFIC + response + "'");
            out.write(response);
            out.write("\n");
            out.flush();
        }
    }

    /**
     *
     * @param cmd String
     * @param params String[]
     * @throws IOException
     */
    private void sendCmdAndParams(String cmd, String[] param) throws IOException {
        if (cmd != null) {
            String str = cmd;
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    str = str + " " + param;
                }
            }
            logger.fine(OUT_TRAFFIC + str);
            out.write(str);
            out.write("\n");
            out.flush();
        }
    }

    public NSServerResponse execute(String commandLine) throws IOException, CommandException {
        /** @todo separe CMD and the parameters */
        String cmd = "";
        String[] param = null;
        return execute(cmd, param);
    }

    public NSServerResponse execute(String command, String[] param) throws IOException, CommandException {
        checkBeforeExecute();

        NSServerResponse serverResponse = null;
        try {
            //Send the command and relative parameters
            sendCmdAndParams(command, param);

            //Change ServerConnection STATUS
            startReception();

            //Retrieve the FIRST line of response
            serverResponse = fetchServerResponse();

        }
        catch (IOException ioe) {
            logger.warning("Error reading from server: " + ioe.getMessage());
            attemptReconnection(ioe);
            serverResponse = execute(command, param);
        }

        /** @todo Manage the CMD 'QUIT' which does not contemplate an answer.
         */

        return serverResponse;
    }

    private void attemptReconnection(IOException disconnectCause) throws IOException {
        while (allowReconnects && reconnectAttempt < maxReconnectAttempts) {
            // Discard the old socket
            socket = null;

            // Exponential backoff.
            int sleepTime = 2 << reconnectAttempt;
            logger.warning("Sleeping " + sleepTime + " seconds before reconnecting...");
            try {
                Thread.sleep(sleepTime * 1000);
            }
            catch (InterruptedException e) {
                // Ignore
            }

            try {
                connect();
                // It worked.
                reconnectAttempt = 0;
                return;
            }
            catch (IOException ioe) {
                logger.warning("Error connecting to server: " + ioe.getMessage());
                // Failed.
                reconnectAttempt++;
                disconnectCause = ioe;
            }
        }
        IOException e = new IOException(
            disconnectCause == null ?
            "Server closed connection" :
            disconnectCause.getMessage());
        e.initCause(disconnectCause);
        throw e;
    }

    /**
     * @throws IOException
     * @throws CommandException
     */
    private void checkBeforeExecute() throws IOException, CommandException {
        if (socket == null) {
            throw new IllegalStateException("Client not connected");
        }
        if (transmitting) {
            throw new CommandException("Command in execution, incoming data must be read before next command.");
        }
    }

    /**
     *
     * @return String
     * @throws IOException
     */
    public NSServerResponse fetchServerResponse() throws IOException, CommandException {
        NSServerResponse response = new NSServerResponse();
        if (transmitting) {
            throw new IOException("No more data available");
        }

        //########  Retrieve Heading ##########
        String heading = in.readLine();
        logger.fine("First line length : " + heading.length());

        if (!isHeadingRow(heading)) {
            skipToEOT();
            throw new IOException("Received wrong msg from server. Skipped it.");
        }
        //########  Retrieve Error Code ##########
        byte errorCode = retrieveErrorCode(heading);
        if (errorCode != 0) {
            //Retrieve Error message
            response.setErrorMessage("..To complete this features..");
            skipToEOT();
            throw CommandException.getCmdException(errorCode);
        }

        //########  Retrieve Message ##########
        String newLine = in.readLine();
        Vector msg = new Vector();
        int nrMsgRow = 0;
        if (isFirstMsgRow(newLine)) {
            nrMsgRow = 1;

            boolean addRow = true;
            while (addRow) {
                msg.addElement(purgeControlChar(newLine));
                newLine = in.readLine();
                if (isLastRow(newLine) || (newLine == null)) {
                    addRow = false;
                }
                nrMsgRow++;
            }
        }

        //########## Check if the end of MSG is well done
        if (! (isLastRow(newLine))) {
            throw CommandException.UNASPECTED_EOT;
        }

        //Build of NS Server Response
        response.setErrorCode(errorCode);
        response.setMessageToPrint(msg);

        return response;
    }

    private boolean isEmptyLine(String line) {
        if ( (line == null) || (line.length() == 0)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * ISO Control char is the first char ALL THE TIME (if there)
     *
     * @param line String
     * @return String
     */
    private String purgeControlChar(String line) {
        String result = null;
        if (! (isEmptyLine(line))) {
            if (isSpecialRow(line) && (! (isLastRow(line)))) {
                result = line.substring(1);
            }
            else {
                result = line;
            }
        }

        return result;
    }

    /**
     * Return true if the first character is an ISO control char.
     *
     * @param line String
     * @return boolean
     */
    private boolean isSpecialRow(String line) {
        boolean result = false;
        if (! (isEmptyLine(line))) {
            result = Character.isISOControl(line.charAt(0));
        }
        return result;
    }

    private boolean isHeadingRow(String line) {
        boolean result = false;
        if (isSpecialRow(line)) {
            result = (line.charAt(0) == NSMessageUtility.SOH);
        }
        return result;
    }

    private boolean isFirstMsgRow(String line) {
        boolean result = false;
        if (isSpecialRow(line)) {
            result = (line.charAt(0) == NSMessageUtility.STX);
        }
        return result;
    }

    private boolean isLastRow(String line) {
        boolean result = false;
        if (isSpecialRow(line)) {
            if (line.length() > 1) {
                result = (line.charAt(0) == NSMessageUtility.ETX) && (line.charAt(1) == NSMessageUtility.EOT);
            }
        }
        return result;
    }

    private byte retrieveErrorCode(String headingRow) throws CommandException {
        byte result = 0;
        //Heading row is null
        if (headingRow == null) {
            throw new CommandException("No error code available.");
        }
        //The row is not a heading row
        if (! (isHeadingRow(headingRow))) {
            throw new CommandException("Message line does not belongs to Heading.");
        }
        //The heading row is too short
        if (headingRow.length() < 2) {
            throw new CommandException("Heading line is too short.");
        }
        char errorCodeChar = headingRow.charAt(1);
        result = decodeErrorCode(errorCodeChar);
        return result;
    }

    public byte decodeErrorCode(char errorCodeChar) {
        ByteBuffer bbuf = null;
        // Create the encoder for ISO-8859-1
        Charset charset = Charset.forName("ISO-8859-1");
        CharsetEncoder encoder = charset.newEncoder();
        try {
            // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
            // The new ByteBuffer is ready to be read.
            bbuf = encoder.encode(CharBuffer.wrap("" + errorCodeChar));

        }
        catch (CharacterCodingException e) {
            e.printStackTrace();
        }
        if (bbuf != null) {
            return bbuf.array()[0];
        }
        else {
            return 0;
        }
    }

    public void skipToEOT() throws IOException {
        /** @todo readline until find EOT char */
    }

    public void setMaxReconnectAttempts(int maxReconnectAttempts) {
        this.maxReconnectAttempts = maxReconnectAttempts;
    }

    public void setAllowReconnects(boolean allowReconnects) {
        this.allowReconnects = allowReconnects;
    }

    public boolean isConnected() {
        if (socket == null) {
            connected = false;
        }
        else if (socket.isClosed()) {
            connected = false;
        }
        return connected;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                out.write("quit");
                out.write("\n");
                out.flush();
                connected = false;
            }
            catch (Exception e) {
                // Ignore.
                logger.fine("Error sending quit command: " + e.getMessage());
            }
        }
        terminateSession();
    }

    private void terminateSession() {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException e) {
                // Ignore
            }
        }
        socket = null;
        in = null;
        out = null;
    }

    private void startReception() {
        transmitting = true;
    }

    private void endReception() {
        transmitting = false;
    }

    public boolean isTransmitting() {
        return transmitting;
    }

    public boolean eot() {
        return! (transmitting);
    }

    public static void main(String[] args) {
        NSServerConnection test = null;

        try {
            test = new NSServerConnection();
            byte[] bytes = {
                1, 12, 56};
            String a = new String(bytes, "ISO-8859-1");
            System.out.println("Lunghezza della stringa : " + a.length());
            System.out.println("Stringa : " + a);
            System.out.println("Is a special row? " + test.isSpecialRow(a));
            System.out.println("Is a heading row? " + test.isHeadingRow(a));
            byte res = test.decodeErrorCode(a.charAt(0));
            System.out.println("RES[0] = " + res);
            byte errorCode = test.decodeErrorCode(a.charAt(1));
            System.out.println("Error code : " + errorCode);
            errorCode = test.retrieveErrorCode(a);
            System.out.println("Error code : " + errorCode);
            byte[] bb = {
                2, 4, 44};
            a = new String(bb, "ISO-8859-1");
            System.out.println("Is a special row? " + test.isSpecialRow(a));
            System.out.println("Is a heading row? " + test.isHeadingRow(a));
            System.out.println("Is a first MSG row? " + test.isFirstMsgRow(a));
            System.out.println("Is the last row? " + test.isLastRow(a));

        }
        catch (UnsupportedEncodingException ex1) {
            ex1.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (CommandException ex) {
            /** @todo Handle this exception */
            ex.printStackTrace();
        }

    }

    /***************************************
     * INNER CLASS : Connection Parameter
     */

    class ConnectionParameter {
        private String host;
        private int port;
        private String loginId;
        private String userName;
        private String password;
        private Inet4Address serverAddress;

        public ConnectionParameter() {

        }

        public ConnectionParameter(String host, int port, String loginId, String userName, String password) throws
            IOException {
            this.host = host;
            this.port = port;
            this.loginId = loginId;
            this.userName = userName;
            this.password = password;

            this.serverAddress = getByNameIPv4(host);

        }

        /**
         * In machines with IPv6 enabled a call to InetAddress.getByName() might
         * return the IPv6 address instead of the IPv4. Using the IPv6 address to
         * connect will most likely result in failure. Therefore, it is important to
         * always use the IPv4 address. This method gets all IPs associated with a
         * machine and returns the first one that is IPv4.
         *
         * @param hostname
         * @return
         * @throws UnknownHostException
         */
        private Inet4Address getByNameIPv4(String hostname) throws UnknownHostException {
            if ( (hostname == null) || (hostname.equals(""))) {
                hostname = "localhost";
            }
            InetAddress[] hosts = InetAddress.getAllByName(hostname);
            for (int i = 0; i < hosts.length; i++) {
                if (hosts[i] instanceof Inet4Address) {
                    return (Inet4Address) hosts[i];
                }
            }
            throw new UnknownHostException("Host " + hostname + " does not have an IPv4 address");
        }

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return this.port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getLoginId() {
            return this.loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getUserName() {
            return this.userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String passwd) {
            this.password = passwd;
        }

        public Inet4Address getServerAddress() {
            return this.serverAddress;
        }

    }

}
