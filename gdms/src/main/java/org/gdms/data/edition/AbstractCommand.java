package org.gdms.data.edition;


public class AbstractCommand {
	protected int index;

	protected EditionDecorator dataSource;

	public AbstractCommand(int index, EditionDecorator dataSource) {
		super();
		this.index = index;
		this.dataSource = dataSource;
	}

	protected int getIndex() {
		return index;
	}

	protected void setIndex(int index) {
		this.index = index;
	}
}
