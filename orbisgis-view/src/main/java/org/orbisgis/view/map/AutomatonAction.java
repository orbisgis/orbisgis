/**
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

package org.orbisgis.view.map;

import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.map.ext.AutomatonHolder;
import org.orbisgis.view.map.ext.MapEditorExtension;
import org.orbisgis.view.map.tool.Automaton;
import javax.swing.Action;
import java.awt.event.ActionEvent;

/**
 * Link an Automaton with the MapEditor
 * @author Nicolas Fortin
 */
public class AutomatonAction extends DefaultAction implements AutomatonHolder {
    private Automaton automaton;
    private MapEditorExtension extension;
    public AutomatonAction(String actionId, Automaton automaton, MapEditorExtension extension) {
        super(actionId, automaton.getName());
        this.extension = extension;
        this.automaton = automaton;
        putValue(Action.SMALL_ICON,automaton.getImageIcon());
        putValue(Action.LARGE_ICON_KEY,automaton.getImageIcon());
        putValue(Action.SHORT_DESCRIPTION,automaton.getTooltip());
        putValue(ActionTools.TOGGLE_GROUP,"automatons"); //radio group
        if(extension.getToolManager()!=null) {
            putValue(Action.SELECTED_KEY,this.automaton.getName().equals(extension.getToolManager().getTool().getName()));
        }
    }

    @Override
    public boolean isEnabled() {
        return automaton.isEnabled(extension.getMapContext(),extension.getToolManager());
    }

    /**
     * Get the automaton hold by this action.
     * @return Automation instance
     */
    public Automaton getAutomaton() {
        return automaton;
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(getValue(Action.SELECTED_KEY).equals(Boolean.TRUE) && extension.getToolManager()!=null) {
            extension.getToolManager().setTool(automaton);
        }
    }
}
