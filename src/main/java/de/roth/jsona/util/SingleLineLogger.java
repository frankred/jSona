package de.roth.jsona.util;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Simple Logger that only prints out the Level and Message, based on
 * MyFormatter implementation from the following url:
 * http://kodejava.org/how-do-i-create-a-custom-logger-formatter/ (Wayan
 * Saryada)
 * 
 * @author Frank Roth
 * 
 */
public class SingleLineLogger extends Formatter {

	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder(32);
		builder.append("[");
		builder.append(record.getLevel()).append("] ");
		builder.append(formatMessage(record));
		builder.append("\n");
		return builder.toString();
	}

	public String getHead(Handler h) {
		return super.getHead(h);
	}

	public String getTail(Handler h) {
		return super.getTail(h);
	}
}