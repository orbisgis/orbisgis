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

package org.orbisgis.mapeditor.map.toolbar;

import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mainframe.api.MainWindow;
import org.orbisgis.mainframe.api.ToolBarAction;
import org.orbisgis.mapeditor.map.tool.Automaton;
import org.orbisgis.mapeditor.map.tools.AutoCompletePolygonTool;
import org.orbisgis.mapeditor.map.tools.CutPolygonTool;
import org.orbisgis.mapeditor.map.tools.LineTool;
import org.orbisgis.mapeditor.map.tools.MoveVertexTool;
import org.orbisgis.mapeditor.map.tools.PointTool;
import org.orbisgis.mapeditor.map.tools.PolygonTool;
import org.orbisgis.mapeditorapi.MapEditorExtension;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.swing.Action;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Generate the Actions for the Drawing ToolBar.
 * @author Nicolas Fortin
 */
@Component
public class DrawingToolBar implements ToolBarAction {
    private MapEditorExtension mapEditor;
    private ExecutorService executorService;

    @Reference
    public void setMapEditor(MapEditorExtension mapEditor) {
        this.mapEditor = mapEditor;
    }

    public void unsetMapEditor(MapEditorExtension mapEditor) {
        this.mapEditor = null;
    }

    @Reference
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void unsetExecutorService(ExecutorService executorService) {
        this.executorService = null;
    }

    @Override
    public List<Action> createActions(MainWindow target) {
        List<Action> actions = new LinkedList<Action>();
        actions.add(new ActionStop(mapEditor));
        actions.add(new ActionUndo(mapEditor));
        actions.add(new ActionRedo(mapEditor));
        actions.add(new ActionDelete(mapEditor, executorService));
        // Autopolygon require selection of features to be available
        add(actions,DRAW_AUTO_POLYGON, new AutoCompletePolygonTool()).setTrackedLayersProperties(ILayer.PROP_SELECTION);
        add(actions,DRAW_CUT_POLYGON, new CutPolygonTool()).setTrackedLayersProperties(ILayer.PROP_SELECTION);
//        add(actions,DRAW_MULTI_POINT, new MultipointTool());
//        add(actions,DRAW_MULTI_LINE, new MultilineTool());
//        add(actions,DRAW_MULTI_POLYGON, new MultipolygonTool());
        add(actions,DRAW_POINT, new PointTool());
        add(actions,DRAW_LINE, new LineTool());
        add(actions,DRAW_POLYGON, new PolygonTool()).setTrackedMapContextProperties(MapContext.PROP_ACTIVELAYER);
//        add(actions,DRAW_SPLIT_LINESTRING, new SplitLineByPointTool());
//        add(actions,DRAW_SPLIT_LINE_BY_LINE, new SplitLineByLineTool());
//        add(actions,DRAW_SPLIT_POLYGON,new SplitPolygonTool());
        add(actions,DRAW_MOVE_VERTEX, new MoveVertexTool()).setTrackedLayersProperties(ILayer.PROP_SELECTION);
//        add(actions,DRAW_VERTEX_ADDITION, new VertexAdditionTool());
//        add(actions,DRAW_VERTEX_DELETION, new VertexDeletionTool());
        return actions;
    }

    private ActionAutomaton add(List<Action> actions,String ID,Automaton action) {
        ActionAutomaton newAction = new ActionDrawingAutomaton(ID, action, mapEditor);
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
