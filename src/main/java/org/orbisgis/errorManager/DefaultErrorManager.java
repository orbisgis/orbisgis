package org.orbisgis.errorManager;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class DefaultErrorManager implements ErrorManager {

	private static Logger logger = Logger.getLogger(DefaultErrorManager.class);

	private ArrayList<ErrorListener> listeners = new ArrayList<ErrorListener>();

	public void error(String userMsg, Throwable exception) {
		try {
			logger.error(userMsg, exception);
			String userMessage = getUserMessage(userMsg, exception);
			for (ErrorListener listener : listeners) {
				listener.error(userMessage, exception);
			}
		} catch (Throwable t) {
			logger.error("Error while managing exception", t);
		}
	}

	private static String getUserMessage(String userMsg, Throwable exception) {
		String ret = userMsg;
		if (exception != null) {
			ret = ret + ": " + exception.getMessage();
			while (exception.getCause() != null) {
				exception = exception.getCause();
				ret = ret + ":\n" + exception.getMessage();
			}
		}

		return ret;
	}

	public void warning(String userMsg, Throwable exception) {
		try {
			logger.warn("warning: " + userMsg, exception);
			String userMessage = getUserMessage(userMsg, exception);
			for (ErrorListener listener : listeners) {
				listener.warning(userMessage, exception);
			}
		} catch (Throwable t) {
			logger.error("Error while managing exception", t);
		}
	}

	public void error(String userMsg) {
		error(userMsg, null);
	}

	public void addErrorListener(ErrorListener listener) {
		listeners.add(listener);
	}

	public void removeErrorListener(ErrorListener listener) {
		listeners.add(listener);
	}

	public void warning(String userMsg) {
		warning(userMsg, null);
	}

}
