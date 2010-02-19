package org.orbisgis.plugins.core.ui.errors;

import java.util.ArrayList;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.windows.errors.ErrorMessage;
import org.orbisgis.plugins.errorManager.ErrorListener;
import org.orbisgis.plugins.errorManager.ErrorManager;

public class CacheMessages implements ErrorListener {

	private ArrayList<ErrorMessage> initMessages = new ArrayList<ErrorMessage>();
	private final ErrorManager errorService;

	public CacheMessages() {
		errorService = Services.getService(ErrorManager.class);
		errorService.addErrorListener(this);
	}

	public void removeCacheMessages() {
		errorService.removeErrorListener(this);
	}

	public void warning(String userMsg, Throwable e) {
		initMessages.add(new ErrorMessage(userMsg, e, false));
	}

	public void error(String userMsg, Throwable e) {
		initMessages.add(new ErrorMessage(userMsg, e, true));
	}

	public void printCacheMessages() {
		for (ErrorMessage msg : initMessages) {
			if (msg.isError()) {
				errorService.error(msg.getUserMessage(), msg.getException());
			} else {
				errorService.warning(msg.getUserMessage(), msg.getException());
			}
		}
	}
}
