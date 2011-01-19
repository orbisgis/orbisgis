package org.orbisgis.core.ui.errors;

import java.awt.Color;

import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorListener;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;
import org.orbisgis.core.ui.windows.errors.ErrorMessage;
import org.orbisgis.utils.I18N;

public class FilteringErrorListener implements ErrorListener {
	private ErrorManager errorService;
	private String lastMessage = null;
	private long lastTimeStamp = 0;
	private boolean ignoredMsgShown = false;

	public FilteringErrorListener() {
		errorService = Services.getService(ErrorManager.class);
		errorService.addErrorListener(this);
	}

	public void warning(String userMsg, Throwable e) {
		error(userMsg, e, false);
	}

	private boolean looksLikePrevious(String currentMsg) {
		if (lastMessage == null || (currentMsg == null)) {
			lastMessage = currentMsg;
			return false;
		} else {
			String currentMsgStart = currentMsg.substring(0, currentMsg
					.length() / 4);
			String currentMsgEnd = currentMsg.substring((3 * currentMsg
					.length()) / 4);
			return (lastMessage.startsWith(currentMsgStart) || lastMessage
					.endsWith(currentMsgEnd));
		}
	}

	private boolean shouldRepport(String msg) {
		if (looksLikePrevious(msg)
				&& (System.currentTimeMillis() - lastTimeStamp) < 5000) {
			if (!ignoredMsgShown) {
				ignoredMsgShown = true;
				reportOutputManager(new ErrorMessage(I18N.getString("orbisgis.org.orbisgis.errors.filteringErrorListener.similarError") //$NON-NLS-1$
						+ I18N.getString("orbisgis.org.orbisgis.errors.filteringErrorListener.messagesNotShown"), null, false)); //$NON-NLS-1$
			}
			return false;
		} else {
			lastMessage = msg;
			ignoredMsgShown = false;
			return true;
		}
	}

	private void error(String userMsg, Throwable e, boolean error) {
		ErrorMessage errorMessage = new ErrorMessage(userMsg, e, error);
		// Show the message to the user
		if (shouldRepport(userMsg)) {
			// Pipe the message to the output manager
			reportOutputManager(errorMessage);
		}
		lastTimeStamp = System.currentTimeMillis();
	}

	private void reportOutputManager(ErrorMessage errorMessage) {
		OutputManager om = Services.getService(OutputManager.class);		
		Color color;
		if (errorMessage.isError()) {
			color = Color.red;
		} else {
			color = new Color(128, 128, 0);
		}
		om.print(errorMessage.getLongMessage() + "\n", color); //$NON-NLS-1$
	}

	public void error(String userMsg, Throwable e) {
		error(userMsg, e, true);
	}
}
