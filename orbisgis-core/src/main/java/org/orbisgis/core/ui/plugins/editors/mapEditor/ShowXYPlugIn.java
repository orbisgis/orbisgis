package org.orbisgis.core.ui.plugins.editors.mapEditor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;

import javax.swing.JLabel;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.editor.PlugInEditorListener;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;

import com.vividsolutions.jts.geom.Coordinate;

import fr.cts.IllegalCoordinateException;
import fr.cts.Unit;
import fr.cts.crs.CoordinateReferenceSystem.Type;
import fr.cts.op.UnitConversion;
import fr.cts.util.AngleFormat;

public class ShowXYPlugIn extends AbstractPlugIn {	
	
	private JLabel showXY;
	private final static int MAX_DIGIT = 7;
	
	public boolean execute(PlugInContext context) throws Exception {
		return true;
	}	
	
	private MouseMotionAdapter mouseMotionAdapter =
		new MouseMotionAdapter()
	{
		public void mouseMoved(MouseEvent e)
		{
			String xCoord="",yCoord="";
			ToolManager toolManager = getPlugInContext().getToolManager();
			if( toolManager!=null ) {
				Point2D point = toolManager.getLastRealMousePosition();
				if(point!=null) {
					if(getPlugInContext().isGeographicCRS()) {
						xCoord = ("Lat:" + AngleFormat.LONGITUDE_FORMATTER.format(point.getX()));
						yCoord =  ("Lon:" + AngleFormat.LONGITUDE_FORMATTER.format(point.getY()));
					}
					else {
						xCoord = "X:" + (int) point.getX();
						yCoord = "Y:" + (int) point.getY();
					}
				}
			}
			showXY.setText(xCoord +  "   "  + yCoord);
		}
	};
	
	public void initialize(final PlugInContext context) throws Exception {		
		showXY = new JLabel();
		showXY.setEnabled(false);		
		EditorManager em = Services.getService(EditorManager.class);
		em.addEditorListener(new PlugInEditorListener(this,showXY,Names.MAP_TOOLBAR_PROJECTION,
								mouseMotionAdapter,context,true));
		
	}	

	public boolean isEnabled() {
		showXY.setText("0.0     0.0" );
		boolean isVisible = false;
		IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
		if (editor != null && editor instanceof MapEditorPlugIn && getPlugInContext().getMapEditor()!=null) {
			MapContext mc = (MapContext) editor.getElement().getObject();
			isVisible = mc.getLayerModel().getLayerCount() > 0;
		}	
		showXY.setEnabled(isVisible);		
		return isVisible;
	}
}
