/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.ParserConfigurationException;
import net.opengis.ows_context.OWSContextType;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.driver.DriverException;
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

    private JButton btn;
    private MapContext mapContext;
    private SIFDialog importOwsDialog;
    private SIFDialog credentialsDialog;
    private OWSContextImporter importer;

    public OwsPlugIn() {
        
        this.importer = new OWSContextImporterImpl();
        this.btn = new JButton(Names.BUTTON_IMPORT_OWC_TITLE);
    }
    

    @Override
    public void initialize(PlugInContext context) throws Exception {
        WorkbenchContext wbcontext = context.getWorkbenchContext();
        wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this, btn, context);
    }

    @Override
    public boolean execute(PlugInContext context) throws Exception {

        UIPanel panel;
        try {
            panel = new OwsImportPanel(new OwsFileImportListenerImpl(), 
                    new OwsServiceImpl());

            
            importOwsDialog = UIFactory.getSimpleDialog(panel);
            importOwsDialog.pack();
            importOwsDialog.setVisible(true);
           


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
        btn.setEnabled(isEnabled);
        return isEnabled;
    }

    public class OwsFileImportListenerImpl implements OwsFileImportListener {

        private final DataSourceFactory dsf = new DataSourceFactory();
        private final List<DBSource> unverifiedDbSources;
        private JAXBElement<OWSContextType> owsContext;
        private int nbDatasourcesToCheck;
        private int nbDataSourcesChecked;
        
        public OwsFileImportListenerImpl() {
            unverifiedDbSources = new ArrayList<DBSource>();
            nbDatasourcesToCheck = 0;
            nbDataSourcesChecked = 0;
        }
        
        /**
         * Checks whether the data sources can be opened and ask for 
         * credentials if they cannot
         */
        private void checkDbSources(List<DbConnectionString> sources) {
            
            DataManager dm = Services.getService(DataManager.class);
            SourceManager sm = dm.getSourceManager();
            
            nbDatasourcesToCheck = sources.size();
         
            for (DbConnectionString source : sources) {
                DBSource newDbSource = new DBSource(source.getHost(), source.getPort(),
                        source.getDb(), "postgress", "ieniiNg3", source.getTable(), "jdbc:postgresql");
                
                String idLayer = OwsContextUtils.generateSourceId(source);
                try {
                    DataSource ds = dsf.getDataSource(newDbSource);
                    ds.open();
                    sm.register(idLayer, newDbSource);
                } catch (DataSourceCreationException ex) {
                    Logger.getLogger(OwsPlugIn.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DriverException ex) {
                    OwsDataSourceCredentialsPanel credentialsPanel = new OwsDataSourceCredentialsPanel(newDbSource);
                    credentialsDialog = UIFactory.getSimpleDialog(credentialsPanel);
                    credentialsPanel.setCredentialsListener(new OwsDataSourceCredentialsRequiredListenerImpl());
                    credentialsDialog.pack();
                    credentialsDialog.setVisible(true);
                    
                    Logger.getLogger(OwsPlugIn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        @Override
        public void fireOwsExtracted(final JAXBElement<OWSContextType> owsContext) {

            unverifiedDbSources.clear();
            this.owsContext = owsContext;
            
            List<DbConnectionString> sources = importer.extractUndefinedDataSources(owsContext);
            checkDbSources(sources);
        }
        
        public class OwsDataSourceCredentialsRequiredListenerImpl implements
                OwsDataSourceCredentialsRequiredListener {


            public OwsDataSourceCredentialsRequiredListenerImpl() {
            }

            @Override
            public void credentialsOk(DBSource source) {
                OwsPlugIn.this.credentialsDialog.setVisible(false);
                OwsFileImportListenerImpl.this.nbDataSourcesChecked++;
                
                if (OwsFileImportListenerImpl.this.nbDataSourcesChecked == 
                        OwsFileImportListenerImpl.this.nbDatasourcesToCheck) {
                    
                    DataManager dm = Services.getService(DataManager.class);
                    SourceManager sm = dm.getSourceManager();

                    sm.register(OwsContextUtils.generateSourceId(source), source);

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
    }
}
