package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.parser.ParseException;
import org.orbisgis.progress.IProgressMonitor;

public class CreateViewOperator extends AbstractOperator implements Operator {

	private String viewName;
	private String statement;
	private DataSourceFactory dsf;

	public CreateViewOperator(String viewName, String statement,
			DataSourceFactory dsf) {
		this.viewName = viewName;
		this.statement = statement;
		this.dsf = dsf;
	}

	protected ObjectDriver getResultContents(IProgressMonitor pm) {

		try {
			dsf.getSourceManager().register(viewName, statement);
		} catch (SourceAlreadyExistsException e) {
			new ExecutionException("Cannot register view: " + viewName, e);
		} catch (ParseException e) {
			new ParseException("Cannot parse : " + statement);
		} catch (SemanticException e) {
			new SemanticException(
					"Cannot create view. The source already exists: "
							+ viewName);
		} catch (DriverException e) {
			new ExecutionException("Cannot create view:" + viewName, e);
		}
		return null;

	}

	public Metadata getResultMetadata() throws DriverException {
		return null;
	}

}
