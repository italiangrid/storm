package it.grid.storm.namespace.admin.client;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.apache.commons.cli.*;
import it.grid.storm.namespace.admin.client.NSServerConnection.*;
import it.grid.storm.namespace.admin.common.*;
import jline.*;

public class ConsoleClient {
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

    private static NSServerConnection nsClient;

    /**
     * Print usage and exits.
     */
    private static void showUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("NSClient [options] [server]\nOptions:", options);
        System.exit(1);
    }

    /**
     * @throws IOException
     * @throws ParseException
     *
     */
    private static NSServerConnection parseArgs(String[] args) throws IOException {
        NSServerConnection connection = new NSServerConnection();
        ConnectionParameter param = connection.getConnectionParameter();
        //create the command line parser
        CommandLineParser parser = new PosixParser();

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        //create the Options
        Options options = new Options();
        Option portOption = new Option("p", "port", true, "Server port");
        Option helpOption = new Option("h", "help", false, "Print usage and exit");
        options.addOption(portOption);
        options.addOption(helpOption);
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
            showUsage(options);
        }
        if (line.hasOption(helpOption.getOpt())) {
            showUsage(options);
        }

        String[] arguments = line.getArgs();

        // The command line values override the ones on the default configuration
        if (line.hasOption(portOption.getOpt())) {
            param.setPort(Integer.parseInt(line.getOptionValue(portOption.getOpt())));
        }
        if (arguments.length > 0) {
            param.setHost(arguments[0]);
        }
        return connection;
    }

    private static void doWork() throws IOException {
        ConsoleReader reader = new ConsoleReader();
        NSServerResponse response = null;
        // Set up command completion
        try {
            response = nsClient.execute("help");

            //Parsing the response message
            /** @todo check the error code! */
            String msgResponse = response.getMessageToPrint();
            Vector msgRows = response.getMessageRows();
            List cmds = new ArrayList();
            //Tokenize per Rows and "cmds.add(cmd);"

            String[] aux = (String[]) cmds.toArray(new String[cmds.size()]);
            reader.addCompletor(new SimpleCompletor(aux));
        }
        catch (Exception ex) {
            System.out.println("Error retrieving list of commands: " + ex.getMessage());
        }

        while (true) {
            System.out.print("Query> ");
            String cmd = reader.readLine();
            try {
                response = nsClient.execute(cmd);
            }
            catch (CommandException e) {
                // Non fatal error. Only a problem with the command
                System.out.println("Error " + e.getMessage() + "\n");
            }
            System.out.println(response.toString());

            if (!nsClient.isConnected()) {
                // The server might have disconnected  (if a quit was sent)
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        NSServerConnection nsClient = parseArgs(args);
        nsClient.setMaxReconnectAttempts(4);

        doWork();
    }
}
