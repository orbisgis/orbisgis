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
package org.orbisgis.sqlconsole.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import static org.orbisgis.sif.CommentUtil.SQL_COMMENT_CHARACTER;

/**
 * Drag functions orders as a Transferable String
 * @author Nicolas Fortin
 */
public class FunctionListTransferHandler extends TransferHandler {
        private static final long serialVersionUID = 1L;
        protected final static I18n I18N = I18nFactory.getI18n(FunctionListTransferHandler.class);
        public static final String SIGNATURES = I18n.marktr("Signature(s):");

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
     * @param sqlCommand SQL command
     */
    protected void formatFunctionComment(StringBuilder s,
                                         String sqlCommand) {        
        s.append(sqlCommand).append("\n");
    }
}
