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
package org.orbisgis.view.sqlconsole.ui;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import static org.orbisgis.view.util.CommentUtil.SQL_COMMENT_CHARACTER;

/**
 * Drag functions orders as a Transferable String
 * @author Nicolas Fortin
 */
public class FunctionListTransferHandler extends TransferHandler {
        private static final long serialVersionUID = 1L;

        @Override
        public int getSourceActions(JComponent jc) {
                return COPY;
        }

        @Override
        protected Transferable createTransferable(JComponent jc) {
                if (jc instanceof FunctionList) {
                    FunctionList list = (FunctionList) jc;
                    StringBuilder stringBuilder = new StringBuilder();
                    for(FunctionElement functionElement : list.getSelectedValuesList()) {
                        formatFunctionComment(stringBuilder,
                                functionElement.getToolTip(),
                                functionElement.getSQLCommand());
                    }
                    return new StringSelection(stringBuilder.toString());
                } else {
                    return null;
                }
        }

    /**
     * Format function comment.
     *
     * @param s          StringBuilder
     * @param toolTip    Tooltip
     * @param sqlCommand SQL command
     */
    protected void formatFunctionComment(StringBuilder s,
                                         String toolTip,
                                         String sqlCommand) {
        s.append(SQL_COMMENT_CHARACTER);
        s.append(toolTip.replaceAll("\n", "\n" + SQL_COMMENT_CHARACTER)).append("\n");
        s.append(SQL_COMMENT_CHARACTER).append("Example usage:\n");
        s.append(sqlCommand).append("\n");
    }
}
