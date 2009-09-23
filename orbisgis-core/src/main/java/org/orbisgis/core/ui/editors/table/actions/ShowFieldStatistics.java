package org.orbisgis.core.ui.editors.table.actions;

import java.awt.Color;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
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

			DataSource ds = element.getDataSource();

			Metadata metadata = ds.getMetadata();

			String fieldName = metadata.getFieldName(selectedColumnIndex);
			DataSource dsResult = null;
			String query = null;
			int countSelection = element.getSelection().getSelectedRows().length;
			int[] selected = element.getSelection().getSelectedRows();
			if (selected.length > 0) {

				if (countSelection == ds.getRowCount()) {
					query = getQuery(fieldName, ds).append(" ;").toString();
				} else {
					query = getQuery(fieldName, ds, selected).append(" ;")
							.toString();
				}

			} else {
				query = getQuery(fieldName, element.getDataSource()).append(
						" ;").toString();

			}

			dsResult = dsf.getDataSourceFromSQL(query);

			OutputManager om = Services.getService(OutputManager.class);

			dsResult.open();
			Metadata metadataResult = dsResult.getMetadata();

			om.println("Statistics on field : " + fieldName, Color.red);
			for (int i = 0; i < dsResult.getRowCount(); i++) {

				for (int k = 0; k < metadataResult.getFieldCount(); k++) {
					om.println(metadataResult.getFieldName(k) + " : "
							+ dsResult.getFieldValue(i, k).getAsDouble());

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

	private StringBuffer getQuery(String fieldName, DataSource ds,
			int[] selected) {

		StringBuffer query = getQuery(fieldName, ds);

		try {
			int fieldIndex = ds.getFieldIndexByName(fieldName);
			int fieldType = ds.getFieldType(fieldIndex).getTypeCode();

			query.append(" WHERE ");

			int and = -1;
			for (int i = 0; i < selected.length; i++) {
				Value v = ds.getFieldValue(selected[i], ds
						.getFieldIndexByName(fieldName));
				if (i > 0) {
					query.append(" or ");
				}
				query.append(fieldName + " = ");
				switch (fieldType) {
				case Type.DOUBLE:
					query.append(v.getAsDouble());
					and++;
					break;
				case Type.INT:
					query.append(v.getAsInt());
					and++;
					break;
				case Type.FLOAT:
					query.append(v.getAsFloat());
					and++;
					break;
				case Type.SHORT:
					query.append(v.getAsShort());
					and++;
					break;
				case Type.LONG:
					query.append(v.getAsLong());
					and++;
					break;
				case Type.DATE:
					query.append("'" + v.getAsDate() + "'");
					and++;
					break;
				case Type.BOOLEAN:
					query.append("'" + v.getAsBoolean() + "'");
					and++;
					break;
				case Type.STRING:
					query.append("'" + v.getAsString() + "'");
					and++;
					break;
				default:
					break;
				}

			}

		} catch (DriverException e) {
			e.printStackTrace();
		}
		return query;
	}

	private StringBuffer getQuery(String fieldName, DataSource ds) {
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
		stringBuffer.append(" FROM  " + ds.getName());

		return stringBuffer;
	}
}
