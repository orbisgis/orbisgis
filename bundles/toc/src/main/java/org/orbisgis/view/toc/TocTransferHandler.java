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
package org.orbisgis.view.toc;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.view.edition.EditorTransferHandler;
import org.orbisgis.view.edition.TransferableEditableElement;
import org.orbisgis.viewapi.edition.EditableElement;
import org.orbisgis.viewapi.edition.EditableSource;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * Tree Toc Drag&Drop operation.
 * Swing Handler for dragging EditableElement
 * Import MapElement(MapContext), EditableSource(Register a layer),
 * EditableLayer (move layer)
 * 
 * Export EditableLayer
 */
public class TocTransferHandler extends EditorTransferHandler {
    private static final Logger LOGGER = Logger.getLogger(TocTransferHandler.class);
    private static final I18n I18N = I18nFactory.getI18n(TocTransferHandler.class);
    private Toc toc;

    public TocTransferHandler(Toc toc) {
        this.toc = toc;
    }

    @Override
    protected boolean canImportEditableElement(EditableElement editableElement) {
        String type = editableElement.getTypeId();
        return type.equals(EditableLayer.EDITABLE_LAYER_TYPE) || type.equals(EditableSource.EDITABLE_RESOURCE_TYPE);
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

    @Override
    public boolean canImport(TransferSupport ts) {
        if(!ts.isDataFlavorSupported(TransferableEditableElement.editableElementFlavor)){
                return false;
        }
        JTree.DropLocation dl = (JTree.DropLocation) ts.getDropLocation();
        TreePath tp = dl.getPath();
        if(tp == null){
                //dropping on the root ! :-)
                return true;
        }
        Object last = tp.getLastPathComponent();
        try {
                Object t = ts.getTransferable().getTransferData(TransferableLayer.LAYER_FLAVOR);
                if(t instanceof EditableLayer[]){
                        EditableLayer[] editables = (EditableLayer[]) t;
                        if(last instanceof TocTreeNodeLayer){
                                ILayer l = ((TocTreeNodeLayer) last).getLayer();
                                //For each editable, we check we don't have a forbidden
                                //relationship.
                                for(EditableLayer e : editables){
                                        if(!canMoveIn(e, l)){
                                                return false;
                                        }
                                }
                        }

                }
        } catch (UnsupportedFlavorException ex) {
                LOGGER.error(I18N.tr("Can't recognize this flavour"), ex);
                return false;
        } catch (IOException ex) {
                LOGGER.error(I18N.tr("Problem during the transfer"), ex);
                return false;
        }
        return true;
    }

    /**
     * Returns true if the ILayer in {@code e} is not a parent of the ILayer
     * {@code lc}. Returns false otherwise.
     * @param e
     * @param lc
     * @return
     */
    private boolean canMoveIn(EditableLayer e, ILayer lc){
            final ILayer l = e.getLayer();
            ILayer tmp = lc;
            while(tmp.getParent() != null){
                    if(l == tmp){
                            return false;
                    }
                    tmp = tmp.getParent();
            }
            return true;
    }

}
