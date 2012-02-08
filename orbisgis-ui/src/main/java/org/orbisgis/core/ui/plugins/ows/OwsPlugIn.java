/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.xml.parsers.ParserConfigurationException;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionListener;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.SIFDialog;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.geocognition.wizards.NewMap;
import org.xml.sax.SAXException;

/**
 *
 * @author cleglaun
 */
public class OwsPlugIn extends AbstractPlugIn {

    private JButton btn;
    private MapContext mapContext;
    private SIFDialog importOwsDialog;
    public static final String URL_GET_ONE_OWS = "http://poulpe.heig-vd.ch/scapc2/serviceapi/web/index.php/context/";

    public OwsPlugIn() {
        this.btn = new JButton("Import OWS");
    }

    @Override
    public void initialize(PlugInContext context) throws Exception {
        WorkbenchContext wbcontext = context.getWorkbenchContext();
        wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this, btn, context);
        
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                UIPanel panel;
                try {
                    panel = new OwsImportPanel(new OwsFileImportListenerImpl(), 
                            new OwsServiceImpl());
                    importOwsDialog = UIFactory.getSimpleDialog(panel);
                    importOwsDialog.setModal(true);
                    importOwsDialog.pack();
                    importOwsDialog.setLocationRelativeTo(null);
                    importOwsDialog.setVisible(true);

                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(OwsPlugIn.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SAXException ex) {
                    Logger.getLogger(OwsPlugIn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    }

    @Override
    public boolean execute(PlugInContext context) throws Exception {
        return true;
    }

    @Override
    public boolean isEnabled() {
        boolean isEnabled = getPlugInContext().getMapContext() == null;
        btn.setEnabled(isEnabled);
        return isEnabled;
    }

    public class OwsFileImportListenerImpl implements OwsFileImportListener {

        @Override
        public void fireOwsExtracted(final List<ILayer> layers) {
            // TODO: Update map context
            Logger.getLogger(OwsPlugIn.class.getName()).log(Level.INFO, "{0} layer(s) imported.", layers.size());
            
            getPlugInContext().getGeocognition().addGeocognitionListener(new GeocognitionListener() {
                @Override
                public boolean elementRemoving(Geocognition geocognition, GeocognitionElement element) {
                    return true;
                }

                @Override
                public void elementRemoved(Geocognition geocognition, GeocognitionElement element) {
                }

                @Override
                public void elementAdded(Geocognition geocognition, GeocognitionElement parent, GeocognitionElement newElement) {
                    mapContext = (MapContext) newElement.getObject();
                }

                @Override
                public void elementMoved(Geocognition geocognition, GeocognitionElement element, GeocognitionElement oldParent) {
                }
            });
            
            getPlugInContext().getEditorManager().addEditorListener(new EditorListener() {
                @Override
                public void activeEditorChanged(IEditor previous, IEditor current) {
                }

                @Override
                public void activeEditorClosed(IEditor editor, String editorId) {
                }

                @Override
                public boolean activeEditorClosing(IEditor editor, String editorId) {
                    return true;
                }

                @Override
                public void elementLoaded(IEditor editor, Component comp) {
                    for (ILayer layer : layers) {
                        try {
                            mapContext.getLayerModel().addLayer(layer);

                        } catch (LayerException ex) {
                            Logger.getLogger(OwsPlugIn.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    importOwsDialog.setVisible(false);
                }
            });
            
            getPlugInContext().executeGeocognitionElement(new NewMap());
        }
    }
}
