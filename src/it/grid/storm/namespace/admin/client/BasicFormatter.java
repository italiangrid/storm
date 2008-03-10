/*
 * Created on Dec 9, 2004
 */
package it.grid.storm.namespace.admin.client;

import java.util.logging.*;

public class BasicFormatter
    extends Formatter {
    public String format(LogRecord record) {
        return "[" + record.getLevel() + "] " + record.getMessage() + "\n";
    }
}
