/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.beanshell;

import javax.swing.JComponent;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.MapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The beanshell docking panel
 * @author Nicolas Fortin
 */
public class BeanShellFrame implements EditorDockable {
        public static final String EDITOR_NAME = "BeanShell";
        private final static I18n I18N = I18nFactory.getI18n(BeanShellFrame.class);
        private DockingPanelParameters parameters = new DockingPanelParameters();
        private BshConsolePanel panel = new BshConsolePanel();
        private MapElement mapElement;

        public BeanShellFrame() {
                parameters.setName(EDITOR_NAME);
                parameters.setTitle(I18N.tr("BeanShell"));
                parameters.setTitleIcon(OrbisGISIcon.getIcon("page_white_cup"));
                parameters.setDockActions(panel.getActions().getActions());
        }
        public void dispose() {
                panel.freeResources();
        }
        @Override
        public DockingPanelParameters getDockingParameters() {
                return parameters;
        }

        @Override
        public JComponent getComponent() {
                return panel;
        }

        @Override
        public boolean match(EditableElement editableElement) {
                return editableElement instanceof MapElement;
        }

        @Override
        public EditableElement getEditableElement() {
                return mapElement;
        }

        @Override
        public void setEditableElement(EditableElement editableElement) {
                if(editableElement instanceof MapElement) {
                        mapElement = (MapElement) editableElement;
                        // Update the interpreter object
                        panel.setMapContext(mapElement.getMapContext());
                }
        }   
        
}
