/**
 * 
 */
package org.gdms.data;

import java.util.ArrayList;

public class BasicWarningListener implements WarningListener {

	public ArrayList<String> warnings = new ArrayList<String>();

	public void throwWarning(String msg, Throwable t, Object source) {
		this.warnings.add(msg);
	}

	public void throwWarning(String msg) {
		this.warnings.add(msg);
	}
}