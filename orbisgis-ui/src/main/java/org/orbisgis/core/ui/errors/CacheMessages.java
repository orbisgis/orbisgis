/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.errors;

import java.util.ArrayList;

import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorListener;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.windows.errors.ErrorMessage;

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
