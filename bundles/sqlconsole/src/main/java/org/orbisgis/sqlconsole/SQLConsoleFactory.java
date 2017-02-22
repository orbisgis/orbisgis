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
package org.orbisgis.sqlconsole;

import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.sif.edition.EditorFactory;
import org.orbisgis.sqlconsole.api.SQLElement;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;


/**
 * Create a single instance of SQLConsole and
 * manage the declaration of the SQLMetadataManager service.
 *
 * @author Nicolas Fortin
 */
@Component(service = EditorFactory.class, immediate = true)
public class SQLConsoleFactory implements EditorFactory {

    public static final String factoryId = "SQLConsoleFactory";
    protected final static I18n I18N = I18nFactory.getI18n(SQLConsoleFactory.class);
    private ComponentFactory componentFactory;
    private List<ComponentInstance> instanceList = new ArrayList<>();

    /**
     * Default constructor
     */
    public SQLConsoleFactory() {

    }

    @Reference(target = "(component.factory="+SQLConsole.SERVICE_FACTORY_ID+")")
    public void setComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    public void unsetComponentFactory(ComponentFactory componentFactory) {
        this.componentFactory = null;
    }

    @Override
    public String getId() {
        return factoryId;
    }

    @Override
    public void dispose() {
    }

    @Deactivate
    public void deactivate() {
        for(ComponentInstance  componentInstance : instanceList) {
            componentInstance.dispose();
        }
        instanceList.clear();
    }

    @Override
    public DockingPanelLayout makeEditableLayout(EditableElement editable) {
        if(editable instanceof SQLElement) {
            return (SQLElement) editable;
        } else {
            return null;
        }
    }

    @Override
    public DockingPanelLayout makeEmptyLayout() {
        return new SQLElement();
    }

    @Override
    public boolean match(DockingPanelLayout layout) {
        return layout instanceof SQLElement;
    }

    @Override
    public EditorDockable create(DockingPanelLayout layout) {
        Dictionary<String,Object> initValues = new Hashtable<>();
        initValues.put("editableElement", layout);
        ComponentInstance sqlPanelFactory = componentFactory.newInstance(initValues);
        instanceList.add(sqlPanelFactory);
        return (SQLConsole) sqlPanelFactory.getInstance();
    }
}
