package org.orbisgis.core.errorListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorMessage {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private Throwable throwable;
	private String userMsg;
	private long date;

	public ErrorMessage(String userMsg, Throwable t) {
		this.userMsg = userMsg;
		this.throwable = t;
		this.date = System.currentTimeMillis();
	}

	public String getUserMessage() {
		return userMsg;
	}

	public String getDate() {
		return sdf.format(new Date(date));
	}

	public String getTrace() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		throwable.printStackTrace(ps);
		return new String(bos.toByteArray());
	}
}
