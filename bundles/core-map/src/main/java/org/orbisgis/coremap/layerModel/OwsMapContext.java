/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.layerModel;

import com.vividsolutions.jts.geom.Envelope;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import net.opengis.ows._2.BoundingBoxType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows_context.LayerType;
import net.opengis.ows_context.OWSContextType;
import net.opengis.ows_context.ObjectFactory;
import net.opengis.ows_context.OnlineResourceType;
import net.opengis.ows_context.ResourceListType;
import net.opengis.ows_context.SLDType;
import net.opengis.ows_context.StyleListType;
import net.opengis.ows_context.StyleType;
import net.opengis.ows_context.URLType;
import org.apache.log4j.Logger;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.ImageRenderer;
import org.orbisgis.coremap.renderer.Renderer;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.common.Description;
import org.orbisgis.coremap.renderer.se.common.LocalizedText;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.URIUtility;
import org.orbisgis.utils.FileUtils;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Class that contains the status of the map view.
 *
 *
 *
 */
public final class OwsMapContext extends BeanMapContext {

        private static final I18n I18N = I18nFactory.getI18n(OwsMapContext.class, Locale.getDefault(), I18nFactory.FALLBACK);
        private static final Logger LOGGER = Logger.getLogger(OwsMapContext.class);
        private ArrayList<MapContextListener> listeners = new ArrayList<MapContextListener>();
        private OpenerListener openerListener;
        private boolean open = false;
        private OWSContextType jaxbMapContext = null; //Persistent form of the MapContext
        private long idTime;
        private DataManager dataManager;

        /**
         * Default constructor
         */
        public OwsMapContext(DataManager dataManager) {
                openerListener = new OpenerListener();
                setRootLayer(createLayerCollection("root"));

                //Create an empty map context
                jaxbMapContext = createJaxbMapContext();
                idTime = System.currentTimeMillis();
                this.dataManager = dataManager;
        }

        @Override
        public DataManager getDataManager() {
            return dataManager;
        }

        @Override
        public ILayer createLayer(String layerName, String tableRef) throws LayerException {
            try {
                try (Connection connection = dataManager.getDataSource().getConnection()) {
                    List<String> geoFields = SFSUtilities.getGeometryFields(connection, TableLocation.parse(tableRef));
                    if (!geoFields.isEmpty()) {
                        return new Layer(layerName, tableRef, dataManager);
                    } else {
                        throw new LayerException(I18N.tr("The source contains no spatial info"));
                    }
                }
            } catch (SQLException ex) {
                throw new LayerException("Cannot retrieve spatial metadata",ex);
            }
        }

        @Override
        public ILayer createLayer(String tableRef) throws LayerException {
                return createLayer(TableLocation.parse(tableRef).getTable(), tableRef);
        }

        @Override
        public ILayer createLayer(String layerName, URI source) throws LayerException {
            return new Layer(layerName, source, dataManager);
        }

        @Override
        public ILayer createLayer(URI source) throws LayerException {
            if(!source.isAbsolute()) {
                // If URI is not absolute ex URI.create("../folder/myfile.shp"), then create a canonical URI
                try {
                    source = new File(location != null ? new File(location) : new File("./"),
                            source.toString()).getCanonicalFile().toURI();
                } catch (IOException ex) {
                    throw new LayerException(ex);
                }
            }
            String layerName;
            try {
                layerName = FileUtils.getNameFromURI(source);
            } catch (UnsupportedOperationException ex) {
                try {
                    layerName = dataManager.findUniqueTableName(I18N.tr("Layer"));
                } catch (SQLException ex2) {
                    throw new LayerException(ex2);
                }
            }
            return createLayer(layerName, source);
        }

    @Override
        public ILayer createLayerCollection(String layerName) {
                return new LayerCollection(layerName);
        }

        private void setRootLayer(ILayer newRoot) {
                if (layerModel != null) {
                        layerModel.removeLayerListenerRecursively(openerListener);
                }
                super.setLayerModel(newRoot);
                layerModel.addLayerListenerRecursively(openerListener);
        }

        @Override
        protected void setLayerModel(ILayer newRoot) {
                setRootLayer(newRoot);
        }

        @Override
        public void addMapContextListener(MapContextListener listener) {
                listeners.add(listener);
        }

        @Override
        public void removeMapContextListener(MapContextListener listener) {
                listeners.remove(listener);
        }

        @Override
        public ILayer getLayerModel() {
                checkIsOpen();
                return super.getLayerModel();
        }

        @Override
        public long getIdTime() {
                return idTime;
        }

        @Override
        public ILayer[] getLayers() {
                checkIsOpen();
                return getLayerModel().getLayersRecursively();
        }

        @Override
        public Style[] getSelectedStyles() {
                checkIsOpen();
                return super.getSelectedStyles();
        }

        @Override
        public ILayer[] getSelectedLayers() {
                checkIsOpen();
                return super.getSelectedLayers();
        }

	@Override
	public void setSelectedStyles(Style[] selectedStyles) {
		checkIsOpen();
		super.setSelectedStyles(selectedStyles);
                //DEPRECATED LISTENERS
                for (MapContextListener listener : listeners) {
                        listener.layerSelectionChanged(this);
                }
        }

        @Override
        public void setSelectedLayers(ILayer[] selectedLayers) {
                checkIsOpen();
                ArrayList<ILayer> filtered = new ArrayList<ILayer>();
                for (ILayer layer : selectedLayers) {
                        if (layerModel.getLayerByName(layer.getName()) != null) {
                                filtered.add(layer);
                        }
                }
                super.setSelectedLayers(filtered.toArray(new ILayer[filtered.size()]));

                //DEPRECATED LISTENERS
                for (MapContextListener listener : listeners) {
                        listener.layerSelectionChanged(this);
                }
        }

        private final class OpenerListener extends LayerListenerAdapter {

                @Override
                public void layerAdded(LayerCollectionEvent e) {
                        if (isOpen()) {
                                for (final ILayer layer : e.getAffected()) {
                                        try {
                                                layer.open();
                                                layer.addLayerListenerRecursively(openerListener);
                                                // checking & possibly setting SRID
                                                // checkSRID(layer);
                                        } catch (LayerException ex) {
                                                LOGGER.error(
                                                        I18N.tr("Cannot open layer : {0} ", layer.getName()), ex);
                                                try {
                                                        layer.getParent().remove(layer);
                                                } catch (LayerException e1) {
                                                        LOGGER.error(
                                                                I18N.tr("Cannot remove the layer {0}", layer.getName()), ex);
                                                }
                                        }
                                }
                        }
                }

                @Override
                public void layerRemoved(LayerCollectionEvent e) {
                        HashSet<ILayer> newSelection = new HashSet<ILayer>();
                        newSelection.addAll(Arrays.asList(selectedLayers));
                        ILayer[] affected = e.getAffected();
                        for (final ILayer layer : affected) {
                                // Check active
                                if (activeLayer == layer) {
                                        setActiveLayer(null);
                                }

                                // Check selection
                                newSelection.remove(layer);
                                layer.removeLayerListenerRecursively(openerListener);
                                if (isOpen()) {
                                        try {
                                                layer.close();
                                        } catch (LayerException e1) {
                                                LOGGER.warn(I18N.tr("Cannot close layer {0}", layer.getName()), e1);
                                        }
                                }
                        }

                        setSelectedLayers(newSelection.toArray(new ILayer[newSelection.size()]));
                        // checkIfHasToResetSRID();
                }
        }

        @Override
        public boolean isLayerModelSpatial(){
                ILayer[] layers = getLayers();
                for(ILayer l : layers){
                        if(!l.acceptsChilds()){
                                return true;
                        }
                }
                return false;
        }

        /**
         * Implementation of public draw method
         *
         * @param mt Contain the extent and the image to draw on
         * @param pm Object to report process and check the cancelled condition
         * @param layer Draw recursively this layer
         * @throws IllegalStateException If the map is closed
         */
        private void drawImpl(MapTransform mt, ProgressMonitor pm, ILayer layer) throws IllegalStateException {
                checkIsOpen();
                Renderer renderer = new ImageRenderer();
                renderer.draw(mt, layer, pm);
        }

        @Override
        public void draw(MapTransform mt, ProgressMonitor pm, ILayer layer) {
                //Layer must be from this layer model
                if (!isLayerFromThisLayerModel(layer)) {
                        throw new IllegalStateException(I18N.tr("Layer provided for drawing is not from the map context layer model."));
                }
                drawImpl(mt, pm, layer);
        }

        @Override
        public void draw(MapTransform mt,
                ProgressMonitor pm) {
                drawImpl(mt, pm, getLayerModel());
        }

        /**
         * Search recursively for the specified layer in the layer model
         *
         * @param layer Searched layer
         * @return True if the layer is in the map context layer model
         */
        private boolean isLayerFromThisLayerModel(ILayer layer) {
                ILayer[] allLayers = getLayerModel().getLayersRecursively();
                return Arrays.asList(allLayers).contains(layer);
        }

        private void checkIsOpen() {
                if (!isOpen()) {
                        throw new IllegalStateException(
                                I18N.tr("The map is not open")); //$NON-NLS-1$
                }
        }

        @Override
        public ILayer getActiveLayer() {
                checkIsOpen();
                return activeLayer;
        }

        @Override
        public void setActiveLayer(ILayer activeLayer) {
                checkIsOpen();
                ILayer lastActive = this.activeLayer;
                this.activeLayer = activeLayer;

                propertyChangeSupport.firePropertyChange(PROP_ACTIVELAYER, lastActive, activeLayer);
        }

        private OWSContextType createJaxbMapContext() {
                //Create jaxbcontext data
                ObjectFactory ows_context_factory = new ObjectFactory();
                net.opengis.ows._2.ObjectFactory ows_factory = new net.opengis.ows._2.ObjectFactory();

                OWSContextType mapContextSerialisation = ows_context_factory.createOWSContextType();

                //GeneralType
                mapContextSerialisation.setGeneral(ows_context_factory.createGeneralType());

                //GeneralType:Bounding Box
                if (boundingBox != null) {
                        BoundingBoxType bbox = ows_factory.createBoundingBoxType();
                        bbox.getLowerCorner().add(boundingBox.getMinX());
                        bbox.getLowerCorner().add(boundingBox.getMinY());
                        bbox.getUpperCorner().add(boundingBox.getMaxX());
                        bbox.getUpperCorner().add(boundingBox.getMaxY());
                        mapContextSerialisation.getGeneral().setBoundingBox(ows_factory.createBoundingBox(bbox));
                }
                //GeneralType:Title
                Map<Locale,String> titles = description.getTitles();
                if(!titles.isEmpty()) {
                        //Take the first one
                        for(Entry<Locale,String> entry : titles.entrySet()) {
                                LanguageStringType title = ows_factory.createLanguageStringType();
                                title.setLang(LocalizedText.toLanguageTag(entry.getKey()));
                                title.setValue(entry.getValue());
                                mapContextSerialisation.getGeneral().setTitle(title);
                                break; //Ows-context does not support multi-lingual
                        }
                }
                //GeneralType:Abstract
                Map<Locale,String> abstracts = description.getAbstractTexts();
                if(!abstracts.isEmpty()) {
                        //Take the first one
                        for(Entry<Locale,String> entry : abstracts.entrySet()) {
                                LanguageStringType mapAbstract = ows_factory.createLanguageStringType();
                                mapAbstract.setLang(LocalizedText.toLanguageTag(entry.getKey()));
                                mapAbstract.setValue(entry.getValue());
                                mapContextSerialisation.getGeneral().setAbstract(mapAbstract);
                                break; //Ows-context does not support multi-lingual
                        }
                }
                //ResourceList
                ResourceListType rs = ows_context_factory.createResourceListType();
                mapContextSerialisation.setResourceList(rs);

                //LayerList
                if (layerModel != null) {
                        List<LayerType> rootLayerList = rs.getLayer();
                        ILayer[] rootLayers = layerModel.getChildren();
                        for (ILayer layer : rootLayers) {
                                if(layer.isSerializable()){
                                        rootLayerList.add(createJAXBFromLayer(layer, this));
                                }
                        }
                }
                return mapContextSerialisation;
        }

        private static LayerType createJAXBFromLayer(ILayer layer, MapContext mapContext) {
            ObjectFactory ows_context_factory = new ObjectFactory();
            LayerType layerType = ows_context_factory.createLayerType();
            Description description = layer.getDescription();
            description.initJAXBType(layerType);
            layerType.setHidden(!layer.isVisible());
            ILayer[] childrens = layer.getChildren();
            for(ILayer child : childrens) {
                if(child.isSerializable()){
                    layerType.getLayer().add(createJAXBFromLayer(child, mapContext));
                }
            }
            // If not a Layer Collection
            if(!(layer instanceof LayerCollection) && layer.getStyles()!=null) {
                StyleListType slt = ows_context_factory.createStyleListType();
                layerType.setStyleList(slt);
                for(Style style : layer.getStyles()) {
                    StyleType st = ows_context_factory.createStyleType();
                    slt.getStyle().add(st);
                    SLDType sltType = ows_context_factory.createSLDType();
                    st.setSLD(sltType);
                    sltType.setAbstractStyle(style.getJAXBElement());
                }
            }

            //Serialisation of dataSource as a DataUrl string
            //Create jaxb instances
            URLType dataURL = ows_context_factory.createURLType();
            if(!(layer instanceof LayerCollection)) {
                OnlineResourceType resource = ows_context_factory.createOnlineResourceType();
                dataURL.setOnlineResource(resource);

                String resourceSerialisation = "";
                URI srcUri = layer.getDataUri();
                if(srcUri!=null) {
                    // If file, use MapContext relative path
                    if(srcUri.getScheme().equalsIgnoreCase("file") && mapContext.getLocation() != null) {
                        srcUri = URIUtility.relativize(mapContext.getLocation(), srcUri);
                    }
                    resourceSerialisation = srcUri.toString();
                }
                resource.setHref(resourceSerialisation);
                if(resource.isSetHref()) {
                    layerType.setDataURL(dataURL);
                }
            }
            return layerType;
        }

        @Override
        public void read(InputStream in) throws IllegalArgumentException {
                try {
                        Unmarshaller unMarsh = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                        JAXBElement<OWSContextType> importedOwsContextType = (JAXBElement<OWSContextType>) unMarsh.unmarshal(in);
                        setJAXBObject(importedOwsContextType.getValue());
                } catch (JAXBException ex) {
                        throw new IllegalArgumentException(I18N.tr("Unable to read the provided map context"), ex);
                }
        }

        @Override
        public void write(OutputStream out) {
                ObjectFactory owsFactory = new ObjectFactory();
                try {
                        JAXBElement<OWSContextType> exportedOwsContextType = owsFactory.createOWSContext(getJAXBObject());
                        Marshaller marshaller = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createMarshaller();
                        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
                        marshaller.marshal(exportedOwsContextType, out);
                } catch (JAXBException ex) {
                        throw new IllegalArgumentException(I18N.tr("Error raised while exporting the map context"), ex);
                }
        }

        /**
         * 
         * @return The internal serialisation objects of the Map Context
         */
        public OWSContextType getJAXBObject() {
                if (jaxbMapContext == null) {
                        return createJaxbMapContext();
                }
                return jaxbMapContext;
        }

        private void setJAXBObject(OWSContextType jaxbObject) {
                if (isOpen()) {
                        throw new IllegalStateException(
                                I18N.tr("The map must be closed to invoke this method")); //$NON-NLS-1$
                }
                this.jaxbMapContext = (OWSContextType) jaxbObject;

        }

        @Override
        public void close(ProgressMonitor pm) {

                checkIsOpen();

                //Backup Consistent data
                jaxbMapContext = getJAXBObject();

                // Close the layers
                if (pm == null) {
                        pm = new NullProgressMonitor();
                }
                ILayer[] layers = layerModel.getLayersRecursively();
                for (int i = 0; i < layers.length; i++) {
                        pm.progressTo(i * 100 / layers.length);
                        if (!layers[i].acceptsChilds()) {
                                try {
                                        layers[i].close();
                                } catch (LayerException e) {
                                        LOGGER.error(I18N.tr("Cannot close layer {0}", layers[i].getName()));
                                }
                        }
                }
                layerModel.removeLayerListenerRecursively(openerListener);
                this.open = false;
        }

        /**
         * Recursive function to parse a layer tree
         *
         * @param lt
         * @param parentLayer
         */
        private void parseJaxbLayer(LayerType lt, ILayer parentLayer) throws LayerException {
                //Test if lt is a group
                if(!lt.getLayer().isEmpty() || (lt.getDataURL()==null )) {
                        //it will create a LayerCollection
                        parseJaxbLayerCollection(lt, parentLayer);
                } else {
                        //it corresponding to a leaf layer
                        //We need to read the data declaration and create the layer
                        URLType dataUrl = lt.getDataURL();
                        if (dataUrl != null) {
                                OnlineResourceType resType = dataUrl.getOnlineResource();
                                try {
                                        URI layerURI = new URI(resType.getHref());
                                        // The resource is given as relative to MapContext location
                                        if(!layerURI.isAbsolute() && getLocation()!=null) {
                                            try {
                                                // Resolve the relative resource ex: new Uri("myFile.shp")
                                                layerURI = getLocation().resolve(layerURI);
                                            } catch (IllegalArgumentException ex) {
                                                LOGGER.warn("Error while trying to find an absolute path for an external resource", ex);
                                            }
                                        }
                                        //Get table name
                                        ILayer leafLayer = createLayer(layerURI);
                                        leafLayer.setDescription(new Description(lt));
                                        leafLayer.setVisible(!lt.isHidden());
                                        //Parse styles
                                        if(lt.isSetStyleList()) {
                                                for(StyleType st : lt.getStyleList().getStyle()) {
                                                        if(st.isSetSLD()) {
                                                                if(st.getSLD().isSetAbstractStyle()) {
                                                                        leafLayer.addStyle(new Style((JAXBElement<net.opengis.se._2_0.core.StyleType>)st.getSLD().getAbstractStyle(), leafLayer));
                                                                }
                                                        }
                                                }
                                        }
                                        parentLayer.addLayer(leafLayer);
                                } catch (URISyntaxException ex) {
                                    throw new LayerException(I18N.tr("Unable to parse the href URI {0}.", resType.getHref()), ex);
                                } catch (InvalidStyle ex) {
                                        throw new LayerException(I18N.tr("Unable to load the description of the layer {0}", lt.getTitle().toString()), ex);
                                }
                        }
                }
        }

        /**
         * Recursive function to parse a layer tree
         *
         * @param lt
         * @param parentLayer
         */
        private void parseJaxbLayerCollection(LayerType lt, ILayer parentLayer) throws LayerException {
                LayerCollection layerCollection = new LayerCollection(lt);
                for (LayerType ltc : lt.getLayer()) {
                        try {
                                parseJaxbLayer(ltc, layerCollection);
                        } catch (LayerException ex) {
                                //The layer is not created if a layer exception is thrown
                                //Create a warning, because the MapContext is loaded
                                LOGGER.warn(I18N.tr("The layer has not been imported"), ex);
                        }
                }
                parentLayer.addLayer(layerCollection);
        }

        private void loadOwsContext() throws LayerException {
                if (jaxbMapContext != null) {
                        //Load bounding box
                        if (jaxbMapContext.getGeneral().getBoundingBox() != null) {
                                List<Double> lowerCorner = jaxbMapContext.getGeneral().getBoundingBox().getValue().getLowerCorner();
                                List<Double> upperCorner = jaxbMapContext.getGeneral().getBoundingBox().getValue().getUpperCorner();
                                if (lowerCorner.size() >= 2 && upperCorner.size() >= 2) {
                                        setBoundingBox(new Envelope(lowerCorner.get(0), upperCorner.get(0), lowerCorner.get(1), upperCorner.get(1)));
                                }
                        }                        
                        //Load title
                        Description nextDescription = new Description();
                        if(jaxbMapContext.getGeneral().getTitle() != null) {
                                LanguageStringType title = jaxbMapContext.getGeneral().getTitle();
                                Locale locale;
                                if(title.getLang()!=null) {
                                        locale = LocalizedText.forLanguageTag(title.getLang());
                                } else {
                                        locale = Locale.getDefault();
                                }
                                nextDescription.addTitle(locale,title.getValue());
                        }
                        //Load abstract
                        if(jaxbMapContext.getGeneral().getAbstract() != null) {
                                LanguageStringType mapAbstract = jaxbMapContext.getGeneral().getAbstract();
                                Locale locale;
                                if(mapAbstract.getLang()!=null) {
                                        locale = LocalizedText.forLanguageTag(mapAbstract.getLang());
                                } else {
                                        locale = Locale.getDefault();
                                }
                                nextDescription.addAbstract(locale,mapAbstract.getValue());
                        }
                        setDescription(nextDescription);
                        //Collect DataSource URI already loaded

                        //Load layers and DataSource
                        //Root layer correspond to ResourceList
                        setRootLayer(createLayerCollection("root"));
                        for (LayerType lt : jaxbMapContext.getResourceList().getLayer()) {
                                try {
                                        parseJaxbLayer(lt, getLayerModel());
                                } catch (LayerException ex) {
                                        //The layer is not created if a layer exception is thrown
                                        LOGGER.error(I18N.tr("The layer has not been imported"), ex);
                                }
                        }
                }
        }

        @Override
        public void open(ProgressMonitor pm) throws LayerException {

                if (isOpen()) {
                        throw new IllegalStateException(
                                I18N.tr("The map is already open"));
                }
                open = true;
                this.activeLayer = null;
                //Read the specified jaxbMapContext
                setSelectedLayers(new ILayer[0]);
                setSelectedStyles(new Style[0]);
                
                loadOwsContext();
                jaxbMapContext = null;
        }

        @Override
        public boolean isOpen() {
                return open;
        }

        /*
         * A mapcontext must have only one {@link CoordinateReferenceSystem} By
         * default the crs is set to null.
         */
        /*
         * public CoordinateReferenceSystem getCoordinateReferenceSystem() {
         * return crs; }
         *
         * /** Set a {@link CoordinateReferenceSystem} to the mapContext
         *
         * @param crs
         *//*
         * public void setCoordinateReferenceSystem(CoordinateReferenceSystem
         * crs) { this.crs = crs; }
         */

        /**
        @Override
        public void checkSelectionRefresh(final int[] selectedRows,
                final int[] oldSelectedRows, final DataSource dataSource) {
                Envelope env;
                env = getBoundingBox();
                boolean mustUpdate = false;
                try {
                        int geometryIndex = MetadataUtilities.getSpatialFieldIndex(dataSource.getMetadata());
                        for (int i = 0; i < selectedRows.length; i++) {
                                Geometry g = dataSource.getFieldValue(selectedRows[i],
                                        geometryIndex).getAsGeometry();
                                if (g.getEnvelopeInternal().intersects(env)) {
                                        // geometry is on screen -> update
                                        mustUpdate = true;
                                        break;
                                }
                        }
                        if (!mustUpdate) {
                                for (int i = 0; i < oldSelectedRows.length; i++) {
                                        Geometry g = dataSource.getFieldValue(oldSelectedRows[i],
                                                geometryIndex).getAsGeometry();
                                        if (g.getEnvelopeInternal().intersects(env)) {
                                                // old geometry was on screen -> update
                                                mustUpdate = true;
                                                break;
                                        }
                                }
                        }

                } catch (DriverException ex) {
                        mustUpdate = true;
                }
                setSelectionInducedRefresh(mustUpdate);
        }
        */
}
