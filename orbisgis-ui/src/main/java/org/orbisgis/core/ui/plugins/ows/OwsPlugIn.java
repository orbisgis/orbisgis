/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.awt.Component;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.ParserConfigurationException;
import net.opengis.ows_context.OWSContextType;
import org.gdms.data.db.DBSource;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
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
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.geocognition.wizards.NewMap;
import org.xml.sax.SAXException;

/**
 *
 * @author cleglaun
 */
public class OwsPlugIn extends AbstractPlugIn {

    private JButton cmdImportOwsContext;
    private MapContext mapContext;
    private SIFDialog dialogImportOws;
    private SIFDialog dialogCurrentCredentials;
    private OWSContextImporter importer;
    private static JAXBElement<OWSContextType> lastOwsContextImported;

    public OwsPlugIn() {
        
        this.importer = new OWSContextImporterImpl();
        this.cmdImportOwsContext = new JButton(Names.BUTTON_IMPORT_OWC_TITLE);
    }
    
    /**
     * Gets the JAXB tree representing the last ows context file that has
     * been imported. It will typically be used by the export plugin to retrieve
     * values that are not used by the orbis object model.
     * 
     * @return A JAXB ows context. Returns null if no ows context has been imported
     * or if the map has been closed.
     */
    public static JAXBElement<OWSContextType> getLastOwsContextImported() {
        return lastOwsContextImported;
    }
    

    @Override
    public void initialize(PlugInContext context) throws Exception {
        WorkbenchContext wbcontext = context.getWorkbenchContext();
        wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this, cmdImportOwsContext, context);
    }

    @Override
    public boolean execute(PlugInContext context) throws Exception {

        UIPanel panel;
        try {
            panel = new OwsImportPanel(new OwsFileImportListenerImpl(), 
                    new OwsServiceImpl());

            
            dialogImportOws = UIFactory.getSimpleDialog(panel);
            dialogImportOws.pack();
            dialogImportOws.setVisible(true);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OwsPlugIn.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OwsPlugIn.class.getName()).log(Level.SEVERE, null, ex);
        }    
        
        return true;
    }

    @Override
    public boolean isEnabled() {
        boolean isEnabled = getPlugInContext().getMapContext() == null;
        cmdImportOwsContext.setEnabled(isEnabled);
        return isEnabled;
    }

    /**
     * This listener is called when an ows context file has been
     * unmarshalled by JAXB. It checks if we have access to every data source.
     * If not, we prompt user in order that he can give credentials related
     * to each data source.
     */
    public class OwsFileImportListenerImpl implements OwsFileImportListener {

        private JAXBElement<OWSContextType> owsContext;
        private int nbDataSourcesChecked;
        private boolean layersAlreadyAdded;
        private List<DbConnectionString> sourcesToCheck;
        boolean userInteractionRequested;
        
        public OwsFileImportListenerImpl() {
        }
        
        private void createAndDisplayCredentialsDialog(DbConnectionString source) {
            DBSource newDbSource = new DBSource(source.getHost(), source.getPort(),
                    source.getDb(), "", "", source.getTable(), "jdbc:postgresql");
                    
            OwsDataSourceCredentialsPanel credentialsPanel = 
                    new OwsDataSourceCredentialsPanel(newDbSource, nbDataSourcesChecked + 1, sourcesToCheck.size());
            dialogCurrentCredentials = UIFactory.getSimpleDialog(credentialsPanel);
            credentialsPanel.setCredentialsListener(new OwsDataSourceCredentialsRequiredListenerImpl());
            dialogCurrentCredentials.pack();
            dialogCurrentCredentials.setVisible(true);
        }
        
        /**
         * Asks the user to enter a username and password for the next data source.
         */
        private void askForNextDataSourceCredentials() {
            
            DataManager dm = Services.getService(DataManager.class);
            SourceManager sm = dm.getSourceManager();
            
            while (!userInteractionRequested && (nbDataSourcesChecked < sourcesToCheck.size())) {
                
                DbConnectionString source = sourcesToCheck.get(nbDataSourcesChecked);
                if (sm.getSource(OwsContextUtils.generateSourceId(source)) == null) {
                    userInteractionRequested = true;
                    createAndDisplayCredentialsDialog(source);
                }
                else {
                    nbDataSourcesChecked++;
                }
            }
            
            if (nbDataSourcesChecked == sourcesToCheck.size()) {
                buildMapContext();
            }
        }
        
        /**
         * Called when an ows context has been extracted. The JAXB tree is stored
         * in {@link OwsPlugIn#lastOwsContextImported} so that it can be read
         * when the user is ready to export the same project. It avoids losing
         * pieces of information that are not persisted in the orbisgis data
         * model.
         * 
         * @param owsContext The imported JAXB tree
         */
        @Override
        public void fireOwsExtracted(final JAXBElement<OWSContextType> owsContext) {

            this.owsContext = owsContext;
            lastOwsContextImported = owsContext;
            
            nbDataSourcesChecked = 0;
            sourcesToCheck = importer.extractUndefinedDataSources(owsContext);
            if (sourcesToCheck.size() > 0) {
                askForNextDataSourceCredentials();
            }
            else {
                buildMapContext();
            }
        }
        
        /**
         * Extracts layers from the ows context and add them to a new MapContext.
         * WARNING: I'm not sure it's the most effective method to carry out
         * this task.
         */
        private void buildMapContext() {
            final List<ILayer> layers = OwsPlugIn.this.importer.extractLayers(OwsFileImportListenerImpl.this.owsContext);
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
                    lastOwsContextImported = null;
                }

                @Override
                public boolean activeEditorClosing(IEditor editor, String editorId) {
                    return true;
                }

                @Override
                public void elementLoaded(IEditor editor, Component comp) {
                    // WARNING: This is a trick to prevent layers from being
                    // added more than one time to the layer model
                    if (!layersAlreadyAdded) {
                        for (ILayer layer : layers) {
                            try {
                                mapContext.getLayerModel().addLayer(layer);

                            } catch (LayerException ex) {
                                Logger.getLogger(OwsPlugIn.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        layersAlreadyAdded = true;
                    }
                    
                    dialogImportOws.setVisible(false);
                }
            });

            getPlugInContext().executeGeocognitionElement(new NewMap());
        }
        
        /**
         * This class listens to a panel which allows user to give
         * a data source's credentials (for example OwsDataSourceCredentialsPanel).
         */
        public class OwsDataSourceCredentialsRequiredListenerImpl implements
                OwsDataSourceCredentialsRequiredListener {

            @Override
            public void credentialsOk(DBSource source) {
                userInteractionRequested = false;
                layersAlreadyAdded = false;
                dialogCurrentCredentials.setVisible(false);
                dialogCurrentCredentials = null;
                nbDataSourcesChecked++;
                
                DataManager dm = Services.getService(DataManager.class);
                SourceManager sm = dm.getSourceManager();

                sm.register(OwsContextUtils.generateSourceId(source), source);
                    
                askForNextDataSourceCredentials();
            }
        }
    }
}
