/**
 * 
 */
package org.gdms;

class TestSourceData {
	String nullField;
	boolean repeatedRows;
	String name;

	public TestSourceData(String name, String nullField,
			boolean repeatedRows) {
		super();
		this.name = name;
		this.nullField = nullField;
		this.repeatedRows = repeatedRows;
	}
}