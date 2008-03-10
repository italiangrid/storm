package it.grid.storm;

import org.apache.log4j.Logger;
import java.net.*;
import java.io.*;

import it.grid.storm.config.Configuration;


/**
 * Class that represents a Multithreaded Server listening for administration
 * commands to be sent to StoRM.
 *
 * If it receives the string START, the picker, the spaceReservationServer and the
 * xmlrpcServer get started; if it receives the string STOP, the picker stops;
 * any other string interrupts the connection to this command server leaving StoRM
 * in whatever state is was on.
 *
 * @author  EGRID - ICTP Trieste; INFN - CNAF Bologna
 * @version 3.0
 * @Date    July 2005
 */
public class StoRMCommandServer {

    private StoRM storm; //only StoRM object that the command server administers!
    private int listeningPort; //command server binding port
    private ServerSocket server = null; //server socket of command server!
    private Logger log;

    public StoRMCommandServer(StoRM s) {
        //Warning! StoRM must be initialized _before_ anything else! This is because
        //of the configuration file that sets the properties that will subsequently
        //be used by all parts of StoRM, including the CommandServer itself!!!
        //likewise for logging!
        //
        //This is not a smart idea... should refactor properties for command
        //server to a different file/mechanism!!!
        //
        //Initialize handle to StoRM
        this.storm = s;
        //Initialize this log!
        log = Logger.getLogger("stormBoot");
        //set Listening port of StoRMCommandServer!
        this.listeningPort = Configuration.getInstance().getCommandServerBindingPort();
        //Start multithreaded listening server socket!
        startCommandServer();
    }

    /**
     * Private method that starts a listening ServerSocket, and handles multiple
     * requests by spawning different CommandExecuterThreads!
     */
    private void startCommandServer() {
        try {
            //
            //set listening port
            server = new ServerSocket(listeningPort);
            //
            //start new thread for ServerSocket listening!
            new Thread() {
                public void run() {
                    try {
                        while (true) {
                            //when a new connection is received, handle it in a different thread! This is a multithreaded command server!
                            new CommandExecuterThread(server.accept(), storm).start();
                        }
                    } catch (IOException e) {
                        //something went wrong with server.accept()!
                        log.fatal("UNEXPECTED ERROR! Something went wrong with server.accept()! "+e);
                        System.exit(1);
                    }
                }
            }.start();
        } catch (IOException e) {
            //could not bind to listeningPort!
            log.fatal("UNEXPECTED ERROR! Could not bind to listeningPort! "+e);
            System.exit(1);
        }
    }


    /**
     * Private class that represents a thread that gets started when a new connection to
     * this CommandServer is made: each client connecting to this CommandServer gets its
     * own thread for processing the commands sent to StoRM
     */
    private class CommandExecuterThread extends Thread {
        private StoRM storm;   //instance of StoRM to command!
        private Socket socket; //socket receiveing the communication with the client!

        /**
         * Constructor that requires the StoRM object to command, and the socket
         * through which the client sends the commands to execute.
         */
        private CommandExecuterThread(Socket socket, StoRM storm) {
            this.socket = socket;
            this.storm = storm;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //input stream from client!
                String inputLine;
                while ( (inputLine = in.readLine()) != null) {
                    if (inputLine.toUpperCase().equals("START")) {
                        //manage START command!
                        log.info("StoRM: starting Backend...");
                        storm.startPicker();
                        storm.startXmlRpcServer();
                        storm.startSpaceGC();
                        log.info("StoRM: Backend successfully started.");
                    } else if (inputLine.toUpperCase().equals("STOP")) {
                        //manage STOP command!
                        log.info("StoRM: stopping Backend...");
                        storm.stopPicker();
                        log.info("StoRM: Backend successfully stopped.");
                    } else if (inputLine.toUpperCase().equals("SHUTDOWN")) {
                        log.info("StoRM: Backend shutdown...");
                        in.close();
                        socket.close();
                        log.info("StoRM: Backend shutdown complete.");
                        System.exit(0);
                    } else {
                        //any other command breaks the connection, but the command server remains on!
                        break; //break while!
                    }
                }
                in.close();
                socket.close();
            } catch (IOException e) {
                //something went wrong with getting InputStream from socket, or with readLine(), or with any of the two close().
                log.error("UNEXPECTED ERROR! Something went wrong with getting InputStream from socket, or with readLine(), or with any of the two close() operations! " + e);
            }
        }
    }






    /**
     * Method that automatically starts a CommandServer listening on the port specified
     * in the configuration file.
     *
     * The command line accepts two parameters, both of which must be specified or
     * else the command line is completely ignored:
     *
     * StoRMCommandServer pathname_of_configuration_file refresh_rate_in_seconds
     *
     * For example:
     *        StoRMCommandServer /home/storm/backend/etc/storm.properties 5
     *
     * It starts the command server with configuration file found in
     * /home/storm/backend/etc/storm.properties, and refresh rate for
     * checking changes in the configuration file of 5 seconds. A value
     * of 0 disables refresh.
     *
     * If no command line parameters are specified, the behaviour is
     * dictated by the StoRM Class. Please refer there for further
     * information.
     */
    public static void main(String[] args) {
        String configurationPathname = "";
        int refresh = 0;

        if (args.length==0) {
            //Invoking without any command line parameter!
            System.out.println("StoRMCommandServer invoked without any command line parameter.");
        } else if (args.length==2) {
            //Invoked with two command line parameters as expected!
            configurationPathname = args[0];
            System.out.println("StoRMCommandServer invoked with two parameters.");
            System.out.println("Configuration file: "+configurationPathname);
            try {
                refresh = Integer.parseInt(args[1]);
                System.out.println("Configuration file refresh rate: "+refresh+" seconds");
            } catch (NumberFormatException e) {
                //the refresh rate was not an int!
                System.out.println("Configuration file refresh rate: NOT an integer! Disabling refresh by default!");
            }
        } else {
            //Invoked with either one or more than two parameters!
            System.out.print("StoRMCommandServer invoked with an invalid number of parameters. ");
            System.out.println("Ignoring all: continuing as though as none were present.");
        }

        System.out.println("Now booting StoRM...");
        StoRMCommandServer stormCmdServer = new StoRMCommandServer(new StoRM(configurationPathname,refresh));
    }
}
