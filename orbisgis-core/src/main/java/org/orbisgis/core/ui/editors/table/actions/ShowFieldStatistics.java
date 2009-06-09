package org.orbisgis.core.ui.editors.table.actions;

import java.awt.Color;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.outputManager.OutputManager;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;

public class ShowFieldStatistics implements ITableColumnAction {

	@Override
	public boolean accepts(TableEditableElement element, int selectedColumn) {
		try {
			Metadata metadata = element.getDataSource().getMetadata();

			Type type = metadata.getFieldType(selectedColumn);

			int typeCode = type.getTypeCode();

			switch (typeCode) {
			case Type.BYTE:
			case Type.DOUBLE:
			case Type.FLOAT:
			case Type.INT:
			case Type.LONG:
			case Type.SHORT:
				return true;
			default:
				return false;
			}

		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot access field information", e);
		}

		return false;
	}

	@Override
	public void execute(TableEditableElement element, int selectedColumnIndex) {
		try {

			final DataSourceFactory dsf = ((DataManager) Services
					.getService(DataManager.class)).getDSF();
			Metadata metadata = element.getDataSource().getMetadata();

			String fieldName = "\"" + metadata.getFieldName(selectedColumnIndex)+ "\"";



			String query = getQuery(fieldName, element.getDataSource());

			DataSource dsResult = dsf.getDataSourceFromSQL(query);

			OutputManager om = Services.getService(OutputManager.class);

			dsResult.open();
			Metadata metadataResult = dsResult.getMetadata();


			om.println("Statistics on field : " + fieldName, Color.red);
			for (int i = 0; i < dsResult.getRowCount(); i++) {

				for (int k = 0; k < metadataResult.getFieldCount(); k++) {
				om.println(metadataResult.getFieldName(k) + " : " + dsResult.getFieldValue(i, k).getAsDouble());

			}

			}
			om.println("----------------------------------", Color.red);
			dsResult.close();

		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot access field information", e);
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}

	private String getQuery(String fieldName, DataSource ds) {
		StringBuffer stringBuffer = new StringBuffer("SELECT ");

		stringBuffer.append("COUNT(" + fieldName + ") as count");
		stringBuffer.append(" , ");
		stringBuffer.append("SUM(" + fieldName + ") as sum");
		stringBuffer.append(" , ");
		stringBuffer.append("MIN(" + fieldName + ") as min");
		stringBuffer.append(" , ");
		stringBuffer.append("MAX(" + fieldName + ") as max");
		stringBuffer.append(" , ");
		stringBuffer.append("AVG(" + fieldName + ") as mean");
		stringBuffer.append(" , ");
		stringBuffer.append("StandardDeviation(" + fieldName + ") as std");
		stringBuffer.append(" FROM  " + ds.getName() + " ;");

		return stringBuffer.toString();
	}
}
