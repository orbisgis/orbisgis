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

package org.orbisgis.view.map.toolbar;

import org.orbisgis.view.main.frames.ext.MainWindow;
import org.orbisgis.view.main.frames.ext.ToolBarAction;
import org.orbisgis.view.map.ext.MapEditorExtension;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tools.AutoCompletePolygonTool;
import org.orbisgis.view.map.tools.CutPolygonTool;
import org.orbisgis.view.map.tools.LineTool;
import org.orbisgis.view.map.tools.MoveVertexTool;
import org.orbisgis.view.map.tools.MultilineTool;
import org.orbisgis.view.map.tools.MultipointTool;
import org.orbisgis.view.map.tools.MultipolygonTool;
import org.orbisgis.view.map.tools.PointTool;
import org.orbisgis.view.map.tools.PolygonTool;
import org.orbisgis.view.map.tools.SplitLineStringTool;
import org.orbisgis.view.map.tools.SplitPolygonTool;
import org.orbisgis.view.map.tools.VertexAdditionTool;
import org.orbisgis.view.map.tools.VertexDeletionTool;

import javax.swing.Action;
import java.util.LinkedList;
import java.util.List;

/**
 * Generate the Actions for the Drawing ToolBar.
 * @author Nicolas Fortin
 */
public class DrawingToolBar implements ToolBarAction {
    private MapEditorExtension mapEditor;

    /**
     * Constructor, this service implementation, register and un-register itself.
     * @param mapEditor
     */
    public DrawingToolBar(MapEditorExtension mapEditor) {
        this.mapEditor = mapEditor;
    }

    @Override
    public List<Action> createActions(MainWindow target) {
        List<Action> actions = new LinkedList<Action>();
        actions.add(new ActionCancel(mapEditor));
        actions.add(new ActionUndo(mapEditor));
        actions.add(new ActionRedo(mapEditor));
        actions.add(new ActionDelete(mapEditor));
        add(actions,DRAW_AUTO_POLYGON, new AutoCompletePolygonTool());
        add(actions,DRAW_CUT_POLYGON, new CutPolygonTool());
        add(actions,DRAW_MULTI_POINT, new MultipointTool());
        add(actions,DRAW_MULTI_LINE, new MultilineTool());
        add(actions,DRAW_MULTI_POLYGON, new MultipolygonTool());
        add(actions,DRAW_POINT, new PointTool());
        add(actions,DRAW_LINE, new LineTool());
        add(actions,DRAW_POLYGON, new PolygonTool());
        add(actions,DRAW_SPLIT_LINESTRING, new SplitLineStringTool());
        add(actions,DRAW_SPLIT_POLYGON,new SplitPolygonTool());
        add(actions,DRAW_MOVE_VERTEX, new MoveVertexTool());
        add(actions,DRAW_VERTEX_ADDITION, new VertexAdditionTool());
        add(actions,DRAW_VERTEX_DELETION, new VertexDeletionTool());
        return actions;
    }
    private ActionAutomaton add(List<Action> actions,String ID,Automaton action) {
        ActionAutomaton newAction = new ActionDrawingAutomaton(ID,action,mapEditor);
        actions.add(newAction);
        return newAction;
    }
    @Override
    public void disposeActions(MainWindow target, List<Action> actions) {
        for(Action action : actions) {
            if(action instanceof ActionDisposable) {
                ((ActionDisposable) action).dispose();
            }
        }
    }
}
