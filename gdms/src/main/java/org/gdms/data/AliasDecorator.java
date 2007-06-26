package org.gdms.data;


public class AliasDecorator extends AbstractDataSourceDecorator {

	private String tableAlias;

	public AliasDecorator(DataSource dataSource, String tableAlias) {
		super(dataSource);
		this.tableAlias = tableAlias;
	}

	@Override
	public String getAlias() {
		return tableAlias;
	}
}
