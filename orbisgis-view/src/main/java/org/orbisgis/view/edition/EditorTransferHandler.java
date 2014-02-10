package org.orbisgis.view.edition;

import org.apache.log4j.Logger;
import org.orbisgis.core.events.EventException;
import org.orbisgis.core.events.Listener;
import org.orbisgis.core.events.ListenerContainer;
import org.orbisgis.viewapi.edition.EditableElement;
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
    static final private Logger GUILOGGER = Logger.getLogger("gui."+EditorTransferHandler.class);
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