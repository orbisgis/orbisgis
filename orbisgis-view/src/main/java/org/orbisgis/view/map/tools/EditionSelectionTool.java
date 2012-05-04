package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.Observable;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.CannotChangeGeometryException;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.Handler;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;


public class EditionSelectionTool extends AbstractSelectionTool {

        AbstractButton button;

        public AbstractButton getButton() {
                return button;
        }

        public void setButton(AbstractButton button) {
                this.button = button;
        }

        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return ToolUtilities.isActiveLayerEditable(vc)
                        && ToolUtilities.isActiveLayerVisible(vc) && ToolUtilities.isSelectionGreaterOrEqualsThan(vc,1);
        }

        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        protected ILayer getLayer(MapContext mc) {
                return mc.getActiveLayer();
        }

        @Override
        public void transitionTo_OnePoint(MapContext mc, ToolManager tm) {
        }

        @Override
        public void transitionTo_TwoPoints(MapContext mc, ToolManager tm){
                
        }

        /**
         * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_MakeMove()
         */
        @Override
        public void transitionTo_MakeMove(MapContext mc, ToolManager tm)
                throws TransitionException, FinishedAutomatonException {
                DataSource ds = getLayer(mc).getDataSource();
                for (int i = 0; i < selected.size(); i++) {
                        Handler handler = selected.get(i);
                        Geometry g;
                        try {
                                g = handler.moveTo(tm.getValues()[0], tm.getValues()[1]);
                        } catch (CannotChangeGeometryException e1) {
                                throw new TransitionException(e1);
                        }

                        try {
                                ds.setGeometry(handler.getGeometryIndex(), g);
                        } catch (DriverException e) {
                                throw new TransitionException(e);
                        }
                }

                transition("empty"); //$NON-NLS-1$
        }

        /**
         * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_Movement(java.awt.Graphics)
         */
        @Override
        public void drawIn_Movement(Graphics g, MapContext vc, ToolManager tm)
                throws DrawingException {
                Point2D p = tm.getLastRealMousePosition();
                try {
                        for (int i = 0; i < selected.size(); i++) {
                                Handler handler = selected.get(i);
                                Geometry geom = handler.moveTo(p.getX(), p.getY());
                                tm.addGeomToDraw(geom);
                        }
                } catch (CannotChangeGeometryException e) {
                        throw new DrawingException(
                                I18N.tr("orbisgis.core.ui.editors.map.tool.selectionTool1") + ". " + e.getMessage()); //$NON-NLS-1$
                }
        }

        @Override
        public String getName() {
                return I18N.tr("Move vertex");
        }

        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("moveVertex");
        }

}
