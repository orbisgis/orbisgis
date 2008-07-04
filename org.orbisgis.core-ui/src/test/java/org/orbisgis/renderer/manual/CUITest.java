package org.orbisgis.renderer.manual;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.DataManager;
import org.orbisgis.DefaultDataManager;
import org.orbisgis.DefaultExtendedWorkspace;
import org.orbisgis.DefaultSymbolManager;
import org.orbisgis.ExtendedWorkspace;
import org.orbisgis.Services;
import org.orbisgis.SymbolManager;
import org.orbisgis.editorViews.toc.actions.cui.gui.ILegendPanelUI;
import org.orbisgis.editorViews.toc.actions.cui.gui.JPanelUniqueSymbolLegend;
import org.orbisgis.editorViews.toc.actions.cui.ui.LegendsPanel;
import org.orbisgis.errorManager.DefaultErrorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.pluginManager.workspace.DefaultWorkspace;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.sif.UIFactory;

public class CUITest {

	public static void main(String[] args) throws Throwable {
		DataSourceFactory dsf = new DataSourceFactory(
				"src/test/resources/sources", "src/test/resources/temp");

		Services.registerService("org.orbisgis.DataManager", DataManager.class,
				"", new DefaultDataManager(dsf));
		Services.registerService("org.orbisgis.SymbolManager",
				SymbolManager.class, "", new DefaultSymbolManager(new File(
						"collection.xml")));
		Services.registerService("org.orbisgis.Workspace", Workspace.class, "",
				new DefaultWorkspace() {

					@Override
					public File getFile(String name) {
						return new File(name);
					}

				});
		Services.registerService("org.orbisgis.ExtendedWorkspace",
				ExtendedWorkspace.class, "", new DefaultExtendedWorkspace() {

					@Override
					public File getFile(String name) {
						return new File(name);
					}

				});
		Services.registerService("org.orbisgis.ErrorManager",
				ErrorManager.class, "", new DefaultErrorManager() {

					@Override
					public void error(String userMsg) {
						System.err.println(userMsg);
					}

					@Override
					public void error(String userMsg, Throwable exception) {
						System.err.println(userMsg);
						exception.printStackTrace();
					}

				});

		ILayer layer = getDataManager().createLayer(
				new File("/home/gonzales/workspace"
						+ "/datas2tests/shp/smallshape2D/points.shp"));
		layer.open();
		Type typ = layer.getDataSource().getMetadata().getFieldType(
				layer.getDataSource().getSpatialFieldIndex());
		GeometryConstraint cons = (GeometryConstraint) typ
				.getConstraint(Constraint.GEOMETRY_TYPE);

		LegendsPanel pan = new LegendsPanel();
		pan
				.init(cons, layer.getVectorLegend(),
						new ILegendPanelUI[] { new JPanelUniqueSymbolLegend(
								true, pan) });
		if (UIFactory.showDialog(pan)) {
			try {
				layer.setLegend(pan.getLegends());
			} catch (DriverException e) {
				Services.getErrorManager().error("Driver exception ...", e);
			}
		}
		layer.close();

	}

	public static DataManager getDataManager() {
		return (DataManager) Services.getService("org.orbisgis.DataManager");
	}

}
