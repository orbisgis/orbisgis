/**
 * 
 */
package org.gdms;

abstract class TestSource {
	protected String name;

	public TestSource(String name) {
		super();
		this.name = name;
	}

	public abstract void backup() throws Exception;
}