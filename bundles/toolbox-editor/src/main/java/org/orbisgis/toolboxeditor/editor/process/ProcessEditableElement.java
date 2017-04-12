/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.toolboxeditor.editor.process;

import net.opengis.wps._2_0.*;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbiswps.client.api.utils.ProcessExecutionType;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * EditableElement of a process which contains all the information about a process instance
 * (data, process ...).
 *
 * @author Sylvain PALOMINOS
 */

public class ProcessEditableElement implements EditableElement {

    /** ProcessOffering containing the process represented. */
    private ProcessOffering processOffering;
    /** Indicates if the the element is open or not. */
    private boolean isOpen;
    /** List of listeners for the processState*/
    private List<PropertyChangeListener> propertyChangeListenerList;
    /** Map of the pre defined data of a process used to display default data */
    private Map<URI, Object> dataMap;
    private ProcessExecutionType type;
    private URI processURI;

    /**
     * Constructor of the EditableElement using the ProcessOfferings.
     *
     * @param processOffering Process offering coming from the Wps server.
     * @param defaultDataMap Map containing the default values for the process. The default values will automatically
     *                       fill the UI fields.
     */
    public ProcessEditableElement(ProcessOffering processOffering, URI processURI, Map<URI, Object> defaultDataMap){
        this.processOffering = processOffering;
        this.processURI = processURI;
        this.propertyChangeListenerList = new ArrayList<>();
        this.dataMap = defaultDataMap;
        type = ProcessExecutionType.STANDARD;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListenerList.add(listener);
    }

    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        propertyChangeListenerList.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListenerList.remove(listener);
    }

    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        propertyChangeListenerList.remove(listener);
    }

    @Override
    public String getId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void setModified(boolean modified) {}

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public String getTypeId() {
        return null;
    }

    @Override
    public void open(ProgressMonitor progressMonitor) throws UnsupportedOperationException, EditableElementException {
        isOpen = true;
    }

    @Override
    public void save() throws UnsupportedOperationException, EditableElementException {

    }

    @Override
    public void close(ProgressMonitor progressMonitor) throws UnsupportedOperationException, EditableElementException {
        isOpen = false;
    }

    @Override
    public Object getObject() throws UnsupportedOperationException {
        return processOffering;
    }

    /**
     * Returns the Map of the pre defined data.
     *
     * @return The Map of the pre defined data.
     */
    public Map<URI, Object> getDataMap() {
        return dataMap;
    }

    /**
     * Resets  and empty the data Map.
     */
    public void resetDataMap() {
        this.dataMap = new HashMap<>();
    }

    /**
     * Returns the process.
     *
     * @return The process.
     */
    public ProcessDescriptionType getProcess() {
        return getProcessOffering(null).getProcess();
    }

    /**
     * Returns the process.
     *
     * @return The process.
     */
    public URI getProcessURI() {
        return processURI;
    }

    /**
     * Returns the cache ProcessOffering object or get it from the WpsClient with the process URI.
     *
     * @return the ProcessOffering object.
     */
    public ProcessOffering getProcessOffering(WpsClientImpl wpsClient) {
        if(processOffering == null && wpsClient != null){
            List<ProcessOffering> processOfferingList = wpsClient.getProcessOffering(processURI);
            if(processOfferingList != null && !processOfferingList.isEmpty()) {
                processOffering = processOfferingList.get(0);
            }
        }
        return processOffering;
    }

    /**
     * Sets the default values associated to the ProcessOffering and to the process.
     *
     * @param defaultValues Map of the default values associated to the process.
     */
    public void setDefaultValues(Map<URI,Object> defaultValues) {
        dataMap.putAll(defaultValues);
    }

    public void setProcessExecutionType(ProcessExecutionType processExecutionType){
        this.type = processExecutionType;
    }

    public ProcessExecutionType getProcessExecutionType(){
        return type;
    }
}
