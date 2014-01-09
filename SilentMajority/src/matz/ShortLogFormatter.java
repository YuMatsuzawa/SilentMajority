package matz;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ShortLogFormatter extends Formatter {

	/* LogRecordを適当に見やすくするFormatter．
	 * 1行に収まるようにしている．
	 */
	@Override
	public String format(LogRecord record) {
		StringBuffer message = new StringBuffer(131);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		
		message.append(df.format(record.getMillis()))
		.append("> ")
		.append(record.getMessage())
		.append("\t[")
		.append(record.getSourceClassName())
		.append("#")
		.append(record.getSourceMethodName())
		.append(":")
		.append(record.getThreadID())
		.append("]\n");
		
		return message.toString();
	}

}
