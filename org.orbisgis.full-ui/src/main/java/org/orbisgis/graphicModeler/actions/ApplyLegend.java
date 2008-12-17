package org.orbisgis.graphicModeler.actions;

import java.io.IOException;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.action.IAction;
import org.orbisgis.editors.map.MapContextManager;
import org.orbisgis.editors.map.tools.ToolUtilities;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.renderer.legend.carto.LabelLegend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.UniqueValueLegend;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;

public class ApplyLegend implements IAction {

	@Override
	public void actionPerformed() {
		try {
			// TODO get the correct layer
			MapContextManager mcm = Services
					.getService(MapContextManager.class);
			ILayer activeLayer = mcm.getActiveMapContext().getActiveLayer();
			SpatialDataSourceDecorator sds = activeLayer.getDataSource();

			UniqueValueLegend classification = LegendFactory
					.createUniqueValueLegend();
			classification.setClassificationField("type", sds);
			Symbol processSymbol = SymbolFactory
					.createImageSymbol(ApplyLegend.class
							.getResource("/org/orbisgis/images/process-big.png"));
			Symbol dataSourceSymbol = SymbolFactory
					.createImageSymbol(ApplyLegend.class
							.getResource("/org/orbisgis/images/graphic-database.png"));
			classification.addClassification(ValueFactory
					.createValue("process"), processSymbol, "Process");
			classification.addClassification(ValueFactory
					.createValue("data source"), dataSourceSymbol,
					"Data Source");

			LabelLegend label = LegendFactory.createLabelLegend();
			label.setClassificationField("id");
			label.setSmartPlacing(true);
			label.setFontSize(15);

			activeLayer.setLegend(classification, label);
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isEnabled() {
		MapContextManager mcm = Services.getService(MapContextManager.class);
		MapContext vc = mcm.getActiveMapContext();
		return vc != null && ToolUtilities.isActiveLayerEditable(vc)
				&& ToolUtilities.isActiveLayerVisible(vc)
				&& ToolUtilities.geometryTypeIs(vc, GeometryConstraint.POINT);
	}

	@Override
	public boolean isVisible() {
		return isEnabled();
	}
}
