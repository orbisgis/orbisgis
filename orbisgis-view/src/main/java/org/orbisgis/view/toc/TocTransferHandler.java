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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;


/**
 * Toc Drag&Drop operation
 */
public class TocTransferHandler extends TransferHandler {

    @Override
    public boolean canImport(TransferSupport ts) {
        return super.canImport(ts);
    }

    @Override
    public boolean canImport(JComponent jc, DataFlavor[] dfs) {
        return super.canImport(jc, dfs);
    }

    @Override
    protected Transferable createTransferable(JComponent jc) {
        return super.createTransferable(jc);
    }

    @Override
    public void exportAsDrag(JComponent jc, InputEvent ie, int i) {
        super.exportAsDrag(jc, ie, i);
    }

    @Override
    protected void exportDone(JComponent jc, Transferable t, int i) {
        super.exportDone(jc, t, i);
    }

    @Override
    public void exportToClipboard(JComponent jc, Clipboard clpbrd, int i) throws IllegalStateException {
        super.exportToClipboard(jc, clpbrd, i);
    }

    @Override
    public int getSourceActions(JComponent jc) {
        return super.getSourceActions(jc);
    }

    @Override
    public Icon getVisualRepresentation(Transferable t) {
        return super.getVisualRepresentation(t);
    }

    @Override
    public boolean importData(TransferSupport ts) {
        return super.importData(ts);
    }

    @Override
    public boolean importData(JComponent jc, Transferable t) {
        return super.importData(jc, t);
    }
    
}
