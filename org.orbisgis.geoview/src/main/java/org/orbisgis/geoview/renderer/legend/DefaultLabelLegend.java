package org.orbisgis.geoview.renderer.legend;

import java.util.ArrayList;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class DefaultLabelLegend extends AbstractClassifiedLegend implements
		LabelLegend {

	private String labelSizeField;

	int fontSize = 10;

	@Override
	protected ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException, RenderException {
		ArrayList<Symbol> ret = new ArrayList<Symbol>();
		SpatialDataSourceDecorator sds = getDataSource();
		for (int i = 0; i < sds.getRowCount(); i++) {
			Value v = sds.getFieldValue(i, fieldIndex);
			Symbol symbol = SymbolFactory.createLabelSymbol(v.getAsString(),
					getSize(sds, i));
			ret.add(symbol);
		}

		return ret;
	}

	private int getSize(SpatialDataSourceDecorator sds, int rowIndex)
			throws RenderException, DriverException {
		if (labelSizeField == null) {
			return fontSize;
		} else {
			int fieldIndex = sds.getFieldIndexByName(labelSizeField);
			if (fieldIndex != -1) {
				throw new RenderException("The label size field '"
						+ labelSizeField + "' does not exist");
			} else {
				return sds.getFieldValue(rowIndex, fieldIndex).getAsInt();
			}
		}
	}

	public void setLabelSizeField(String fieldName) throws DriverException {
		this.labelSizeField = fieldName;
		super.invalidateCache();
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
