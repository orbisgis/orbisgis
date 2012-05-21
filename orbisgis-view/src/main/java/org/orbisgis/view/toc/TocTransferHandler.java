/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.toc;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.map.MapTransferHandler;


/**
 * Tree Toc Drag&Drop operation
 * Swing Handler for dragging EditableElement
 * Import MapElement(MapContext), EditableSource(Register a layer),
 * EditableLayer (move layer)
 * 
 * Export EditableLayer
 */
public class TocTransferHandler extends MapTransferHandler {

    Toc toc;

    public TocTransferHandler(Toc toc) {
        this.toc = toc;
    }

    @Override
    protected boolean canImportEditableElement(EditableElement editableElement) {
        return editableElement.getTypeId().equals(EditableLayer.EDITABLE_LAYER_TYPE) ||
                super.canImportEditableElement(editableElement);
    }

    @Override
    public int getSourceActions(JComponent jc) {
        return COPY_OR_MOVE;
    }  
    
    /**
     * Move layers inside the toc
     * @param jc
     * @return A drag&drop content
     */
    @Override
    protected Transferable createTransferable(JComponent jc) {
        //Copy the selection into a TransferableLayer
        List<ILayer> selectedLayers = toc.getSelectedLayers();
        List<EditableLayer> selectedEditableLayer = new ArrayList<EditableLayer>(selectedLayers.size());
        for(ILayer layer : selectedLayers) {
            selectedEditableLayer.add(new EditableLayer(toc.getMapElement(), layer));
        }
        return new TransferableLayer(toc.getMapElement(),selectedEditableLayer );
    }


}
