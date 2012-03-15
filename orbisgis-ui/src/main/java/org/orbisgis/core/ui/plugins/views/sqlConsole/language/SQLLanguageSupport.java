/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
 * 
 * User support leader : Gwendall Petit, geomatic engineer.
 * 
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole.language;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.orbisgis.core.Services;

/**
 * Provides the support for Gdms SQL syntax in the console.
 * 
 * This class needs a registered SQLMetadataManager in order to work, i.e.
 * the code below should return a valid instance of SQLMetadataManager
 * <code>
 * SQLMetadataManager metManager = Services.getService(SQLMetadataManager.class);
 * </code>
 * 
 * This class installs the following on the text area
 *  - a Parser implementation that highlight error in the SQL
 *  - a CompletionProvider that autocompletes SQL queries
 * 
 * @author Antoine Gourlay
 */
public class SQLLanguageSupport extends AbstractLanguageSupport {

        private SQLParser parser;
        private SQLCompletionProvider compl;
        private SQLMetadataManager metManager;

        @Override
        public void install(RSyntaxTextArea textArea) {
                // common services
                metManager = Services.getService(SQLMetadataManager.class);

                // install completion
                compl = new SQLCompletionProvider(textArea, metManager);
                AutoCompletion c = compl.install();
                installImpl(textArea, c);
                
                // install parser
                parser = new SQLParser(textArea);
                textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
                textArea.addParser(parser);

                // install autocompletion
        }

        @Override
        public void uninstall(RSyntaxTextArea textArea) {
                // remove completion
                compl.freeExternalResources();
                uninstallImpl(textArea);
                
                // remove parser
                textArea.removeParser(parser);
                textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, null);
        }
}
