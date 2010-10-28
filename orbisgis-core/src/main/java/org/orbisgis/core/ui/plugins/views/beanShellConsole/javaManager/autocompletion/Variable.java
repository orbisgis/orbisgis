/**
 * 
 */
package org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.autocompletion;

class Variable {

	public String name;
	public Class<?> type;

	public Variable(String name, Class<?> varType) {
		super();
		this.name = name;
		this.type = varType;
	}

}