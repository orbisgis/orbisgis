/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.editors.mapEditor;

import com.vividsolutions.jts.geom.Envelope;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.opengis.se._2_0.core.AbstractStyleType;
import net.opengis.sld._2.Layer;
import net.opengis.sld._2.ObjectFactory;
import net.opengis.sld._2.StyledLayerDescriptorType;
import net.opengis.sld._2.UserStyle;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.multiInputPanel.InputType;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.sif.multiInputPanel.StringType;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;

public class ExportMapAsSLDRequestPlugIn extends AbstractPlugIn {

    public boolean execute(PlugInContext context) throws Exception {
        MapEditorPlugIn mapEditor = (MapEditorPlugIn) getPlugInContext().getActiveEditor();
        MapContext mapContext = (MapContext) mapEditor.getElement().getObject();

        final SaveFilePanel outputXMLPanel = new SaveFilePanel(
                "org.orbisgis.core.ui.editorViews.toc.actions.ImportStyle",
                "Choose a destination");

        outputXMLPanel.addFilter("sld", "Styled Layer Descriptor Format (*.sld)");

        if (UIFactory.showDialog(outputXMLPanel)) {
            String sldFile = outputXMLPanel.getSelectedFile().getAbsolutePath();

            ILayer root = mapContext.getLayerModel();
            MapTransform mt = mapEditor.getMapTransform();
            Envelope envelope = mt.getAdjustedExtent();

            ILayer[] layers = root.getLayersRecursively();

            ObjectFactory of = new ObjectFactory();

            StyledLayerDescriptorType sld = of.createStyledLayerDescriptorType();
            List<Layer> sldLayers = sld.getLayer();

            for (ILayer layer : layers) {
                if (layer.isVisible() && envelope.intersects(layer.getEnvelope())) {
                    Layer sldLayer = of.createLayer();
                    sldLayer.setName(layer.getName());
                    List<Style> styles = layer.getStyles();
                    List<JAXBElement<? extends net.opengis.sld._2.StyleType>> style = sldLayer.getStyle();
                    for(Style s : styles){
                        UserStyle userStyle = of.createUserStyle();
                        userStyle.setAbstractStyle(s.getJAXBElement());
                        style.add(of.createUserStyle(userStyle));
                    }
                    sldLayers.add(sldLayer);
                }
            }

            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(StyledLayerDescriptorType.class, AbstractStyleType.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                JAXBElement<StyledLayerDescriptorType> jaxbSld = of.createStyledLayerDescriptor(sld);

                marshaller.marshal(jaxbSld, new FileOutputStream(sldFile));
            } catch (FileNotFoundException ex) {
                Logger.getLogger("SLDEXport").log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger("SLDEXport").log(Level.SEVERE, null, ex);
            }


            double imgWidth = mt.getWidth();
            double imgHeight = mt.getHeight();

            double imgDpi = mt.getDpi();
            double targetedDpi = 300;

            int targetWidth = (int)(imgWidth / imgDpi * targetedDpi + 0.5);
            int targetHeight = (int)(imgHeight / imgDpi * targetedDpi + 0.5);

            String request = "?service=wms&"
                    + "version=2.0.0&"
                    + "request=GetMap&"
                    + "pixelsize=0.084&"
                    + "width=" + targetWidth
                    + "&"
                    + "height=" + targetHeight
                    + "&"
                    + "BBox="
                    + envelope.getMinX() + ","
                    + envelope.getMinY() + ","
                    + envelope.getMaxX() + ","
                    + envelope.getMaxY() + "&"
                    + "sld=file://" + sldFile;

            MultiInputPanel mip = new MultiInputPanel("SLD Request");
            InputType sit = new StringType();
            mip.addInput("REQUEST", "ll", request, sit);
            UIFactory.showOkDialog(mip);
        }

        return true;
    }


    public void initialize(PlugInContext context) throws Exception {
        WorkbenchContext wbContext = context.getWorkbenchContext();
        WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getMapEditor();
        context.getFeatureInstaller().addPopupMenuItem(frame, this,
                                                       new String[]{Names.POPUP_MAP_EXPORT_SLD},
                                                       Names.POPUP_MAP_EXPORT_SLD_GROUP, false, null, wbContext);
    }


    public boolean isEnabled() {
        MapEditorPlugIn mapEditor = null;
        if ((mapEditor = getPlugInContext().getMapEditor()) != null) {
            MapContext mc = (MapContext) mapEditor.getElement().getObject();
            return mc.getLayerModel().getLayerCount() >= 1;
        }
        return false;
    }


}
