/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.windows.errors;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorMessage {

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"HH:mm:ss (dd/MM/yyyy)");

	private Throwable throwable;
	private String userMsg;
	private long date;
	private boolean isError;

	public ErrorMessage(String userMsg, Throwable t, boolean isError) {
		if (userMsg != null) {
			this.userMsg = userMsg;
		} else {
			this.userMsg = "";
		}
		this.throwable = t;
		this.date = System.currentTimeMillis();
		this.isError = isError;
	}

	public String getUserMessage() {
		return userMsg;
	}

	public String getDate() {
		return sdf.format(new Date(date));
	}

	public String getTrace() {
		if (throwable == null) {
			return userMsg;
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(bos);
			throwable.printStackTrace(ps);
			return new String(bos.toByteArray());
		}
	}

	public boolean isError() {
		return isError;
	}

	public String getLongMessage() {
		StringBuffer ret = new StringBuffer(getUserMessage());
		Throwable t = this.throwable;
		while (t != null) {
			ret.append("\nCaused by: ");
			String message = t.getMessage();
			if (message != null) {
				ret.append(message);
			} else {
				ret.append(t.getClass().getName());
			}
			if (t != t.getCause()) {
				t = t.getCause();
			} else {
				t = null;
			}
		}

		return ret.toString();
	}

	public Throwable getException() {
		return throwable;
	}
}
