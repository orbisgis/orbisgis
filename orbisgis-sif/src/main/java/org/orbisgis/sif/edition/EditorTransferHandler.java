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
package org.orbisgis.sif.edition;

import org.orbisgis.commons.events.EventException;
import org.orbisgis.commons.events.ListenerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import javax.swing.TransferHandler;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author Nicolas Fortin
 */
public abstract class EditorTransferHandler  extends TransferHandler {
    private static final long serialVersionUID = 1L;
    static final private Logger GUILOGGER = LoggerFactory.getLogger("gui." + EditorTransferHandler.class);
    static final private I18n I18N = I18nFactory.getI18n(EditorTransferHandler.class);
    private ListenerContainer<EditableTransferEvent> transferEditableEvent = new ListenerContainer<>();

    /**
     * If this method return true, this transfer handler fire the transfer editable event
     * @param editableElement
     * @return True if this handler accept the editable element
     */
    protected abstract boolean canImportEditableElement(EditableElement editableElement);
    /**
     * MapEditor support MapElement and EditableSource only
     * @param editableArray Array Of Editable
     * @return
     */
    private boolean canImportEditableElements(EditableElement[] editableArray) {
        for(EditableElement ee : editableArray) {
            if(!canImportEditableElement(ee)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean canImport(TransferSupport ts) {
        return ts.isDataFlavorSupported(TransferableEditableElement.editableElementFlavor);
    }

    /**
     * To add and remove editable transfer listener
     * @return
     */
    public ListenerContainer<EditableTransferEvent> getTransferEditableEvent() {
        return transferEditableEvent;
    }


    @Override
    public boolean importData(TransferSupport ts) {
        //cancel the import if it is not a drop operation
        if(!ts.isDrop()) {
            return false;
        }

        if(ts.isDataFlavorSupported(TransferableEditableElement.editableElementFlavor)) {
            //This is an Editable Element
            try {
                //A transferable element
                Transferable trans = ts.getTransferable();
                EditableElement[] editableList = (EditableElement[])trans.getTransferData(TransferableEditableElement.editableElementFlavor);
                if(canImportEditableElements(editableList)) {
                    try {
                        //All elements are compatible
                        transferEditableEvent.callListeners(new EditableTransferEvent(editableList, ts.getDropLocation() ,ts.getComponent()));
                    } catch (EventException ex) {
                        GUILOGGER.error(I18N.tr("Error while drop Editable"),ex);
                        return false;
                    }
                    return true;
                }
            } catch (UnsupportedFlavorException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
        return false;
    }

}
