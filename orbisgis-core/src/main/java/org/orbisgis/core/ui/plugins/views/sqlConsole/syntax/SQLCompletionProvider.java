/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
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
 **/
package org.orbisgis.core.ui.plugins.views.sqlConsole.syntax;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.VariableCompletion;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.DefaultType;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.TokenMgrError;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;

/**
 * This class provides auto-completion for a JTextComponent.
 *
 * @author Antoine Gourlay
 */
public class SQLCompletionProvider extends DefaultCompletionProvider implements CaretListener {

        private JTextComponent textC;
        private AutoCompletion auto;
        private static String[] imgBool = {"TRUE", "FALSE"};
        // Workaround to fix parser inconsistency
        private boolean idAdded;
        private String currentWord = "";
        private String rootText;

        /**
         * Default constructor
         * @param textC the JTextComponent that needs auto-completion.
         */
        public SQLCompletionProvider(JTextComponent textC) {
                this.textC = textC;
        }

        /**
         * This constructor allows the use of a fixed string like
         * "SELECT * FROM toto WHERE" to filter the completion inside the
         * JTextComponent. The content of the JTextComponent is added to rootText
         * and then processed by the completion parser.
         * @param textC the JTextComponent that needs auto-completion.
         * @param rootText a fixed string to be append before the completion starts
         */
        public SQLCompletionProvider(JTextComponent textC, String rootText) {
                this.textC = textC;
                this.rootText = rootText;
        }

        /**
         * Installs and enables auto-completion.
         */
        public void install() {
                // listen to the caret
                textC.addCaretListener(this);

                // for autocomplete to work
                this.setParameterizedCompletionParams('(', ", ", ')');
                auto = new AutoCompletion(this);
                auto.setAutoCompleteSingleChoices(true);
                auto.setShowDescWindow(true);
                auto.install(textC);
        }

        /**
         * Main listening event ; triggers the update of the completion list.
         * @param ce caret event
         */
        @Override
        public void caretUpdate(CaretEvent ce) {
                String content;

                // get all text to caret
                content = getTextContent();

                // closes the list when :
                // - right after a ; --> end of SQL Statement
                // - right after a * --> SELECT *
                // - in the middle of a 'long' space --> hides an already shown window when pressing " "
                // - right after a )
                if (content.endsWith(";") || content.endsWith("*") || content.endsWith("  ")
                        || content.endsWith(")")) {
                        auto.hideChildWindows();
                }


                doCompletion(content);
        }

        /**
         * Core completion method.
         * @param content text on which completion is done
         */
        private void doCompletion(String content) {
                // no completion in a comment
                if (isWithinComment()) {
                        clear();
                        return;
                }

                // get the correct part of the text
                String sql = getCurrentSQLStatement(content);

                // check if we moved enough the caret to have to refill the list
                String word = ReadCurrentWord(sql);

                if (word.startsWith(",")) {
                        clear();
                        addCompletions(getSourceNamesCompletion(false));
                        return;
                }

                currentWord = word;


                // special case : no completion after ) or ;
                if (word.equals(")") || word.equals(";")) {
                        clear();
                        return;
                }
                // special case : field completion
                if (word.startsWith(".")) {
                        clear();
                        doFieldsCompletion(sql.substring(0, sql.lastIndexOf(word) + 1));
                        return;

                        // hack to go around parser limitation
                } else if (!word.equals("(") && !word.startsWith(";")) {
                        sql = sql.substring(0, sql.lastIndexOf(word));
                }

                // hack to go around parser limitation (bis)
                int par = sql.lastIndexOf('(');
                int par2 = sql.lastIndexOf(')');
                if ((par > par2) && sql.startsWith("(SELECT", par)) {
                        sql = sql.substring(par + 1);
                }

                // hack to go around parser limitation (again)
                // hopefuly this will go away when we change the parser
                par = sql.indexOf('(');
                par2 = sql.indexOf(')');
                while (true) {
                        if (par == -1 || par2 == -1 || par2 < par) {
                                break;
                        }
                        sql = sql.substring(0, par) + "{}" + sql.substring(par2 + 1, sql.length());
                        par = sql.indexOf('(');
                        par2 = sql.indexOf(')');
                }
                sql = sql.replace("{}", "(0)");

                clear();

                // getting the engine and parsing the sql statement
                SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql.getBytes()));

                try {
                        parser.SQLScript();
                } catch (ParseException e) {
                        // adding select for nested queries
                        boolean tableFielsdToo = false;
                        if (word.startsWith("(")) {
                                addCompletion(new TokenCompletion(this, SQLEngineConstants.SELECT, e.tokenImage));
                                tableFielsdToo = true;
                        }

                        // SQL Statement not complete, auto-completion needed
                        doNormalCompletion(e, sql, tableFielsdToo);
                } catch (TokenMgrError e) {
                        // never happens, but still...
                }
        }

        @Override
        public void clear() {
                super.clear();
                idAdded = false;
        }

        /**
         * Adds to the CompletionProvider the field list of the current data source
         * @param sql SQL Statement up to where the field is needed
         */
        private void doFieldsCompletion(String sql) {
                if (sql.length() < 2) {
                        return;
                }

                // retrieve the source name
                int start = sql.length() - 2;
                while (start >= 0) {
                        char ch = sql.charAt(start);
                        start--;
                        if (ch == ' ' || ch == '.' || ch == ',' || ch == '(' || ch == ')') {
                                start++;
                                break;
                        }
                }
                String table = sql.substring(start + 1, sql.length() - 1);

                // get the return metadata without executing anything
                Metadata m = getMetadataForDataSource(table);
                if (m == null) {
                        // wrong source name, no completion
                        return;
                }
                try {
                        for (int i = 0; i < m.getFieldCount(); i++) {
                                SQLFieldCompletion complet = new SQLFieldCompletion(this, m.getFieldName(i), DefaultType.typesDescription.get(m.getFieldType(i).getTypeCode()));
                                complet.setDefinedIn("<b>" + table + "</b>");
                                addCompletion(complet);
                        }
                } catch (DriverException ex) {
                        // needed for m.getField*
                }
        }

        /**
         * Parses the ParseException to get the correct tokens and thus completions
         * @param e
         */
        private void doNormalCompletion(ParseException e, String content, boolean tableWithFields) {
                int tokenKind = e.currentToken.kind;
                // special case : nothing after TABLE
                if (tokenKind == SQLEngineConstants.TABLE) {
                        return;
                }

                // special case : only sources after FROM
                // may change in the future (functions...)
                if (tokenKind == SQLEngineConstants.FROM
                        || tokenKind == SQLEngineConstants.INTO) {
                        addCompletions(new ArrayList(getSourceNamesCompletion(false)));
                        return;
                }

                if (isAfterWhereStatement()) {
                        String toLowerCase = content.trim().toLowerCase();

                        if (toLowerCase.endsWith("or") || toLowerCase.endsWith("and")) {
                                addCompletions(new ArrayList(getSourceNamesCompletion(true)));
                                addCompletions(new ArrayList(getFunctionCompletions()));
                                addCompletion(new TokenCompletion(this, 0, imgBool));
                                addCompletion(new TokenCompletion(this, 1, imgBool));
                                return;
                        }
                        tableWithFields = true;
                        if ((tokenKind == SQLEngineConstants.ID || tokenKind == SQLEngineConstants.BOOLEAN_LITERAL
                                || tokenKind == SQLEngineConstants.CLOSEPAREN || tokenKind == SQLEngineConstants.FLOATING_POINT_LITERAL
                                || tokenKind == SQLEngineConstants.INTEGER_LITERAL || tokenKind == SQLEngineConstants.NULL
                                || tokenKind == SQLEngineConstants.STRING_LITERAL)) {
                                addCompletion(new TokenCompletion(this, SQLEngineConstants.AND, e.tokenImage));
                                addCompletion(new TokenCompletion(this, SQLEngineConstants.OR, e.tokenImage));
                        }
                }



                if (tokenKind == SQLEngineConstants.WHERE || tokenKind == SQLEngineConstants.SELECT) {
                        tableWithFields = true;
                }

                HashSet words = new HashSet();
                for (int i = 0; i < e.expectedTokenSequences.length; i++) {
                        if (e.expectedTokenSequences[i].length > 0) {
                                int token = e.expectedTokenSequences[i][e.expectedTokenSequences[i].length - 1];
                                words.addAll(getCompletions(token, e.tokenImage, tokenKind, tableWithFields));
                        }
                }
                addCompletions(new ArrayList(words));
        }

        /**
         * Finds all function completions
         * @return the corresponding collection of Completion items
         */
        private Collection getFunctionCompletions() {
                HashSet a = new HashSet();

                // retrieve all registered functions
                String[] functions = FunctionManager.getFunctionNames();

                for (int i = 0; i < functions.length; i++) {
                        Function function = FunctionManager.getFunction(functions[i]);

                        Arguments[] args = function.getFunctionArguments();
                        for (int j = 0; j < args.length; j++) {
                                ArrayList params = new ArrayList();

                                // will contain all argument types needed to call function.getType(...)
                                Type[] types = new Type[args[j].getArgumentCount()];

                                for (int l = 0; l < args[j].getArgumentCount(); l++) {
                                        Argument arg = args[j].getArgument(l);
                                        int typeCode = arg.getTypeCode();

                                        // no need to auto-complete a NULL argument
                                        if (typeCode != Type.NULL) {
                                                // adds a parameter with no name but a type
                                                params.add(new ParameterizedCompletion.Parameter(DefaultType.typesDescription.get(typeCode).replace("TYPE_", "").replace("ALL", "ANY"), null));
                                        }
                                        // builds the actual corresponding type
                                        types[l] = TypeFactory.createType(typeCode, DefaultType.typesDescription.get(typeCode));
                                }
                                String typeDesc = null;

                                // return type for this set of arguments
                                Type type = function.getType(types);
                                // and its description
                                typeDesc = DefaultType.typesDescription.get(type.getTypeCode()).replace("TYPE_", "").replace("ALL", "ANY");

                                SQLFunctionCompletion c = new SQLFunctionCompletion(this, function.getName(), typeDesc);
                                c.setShortDescription(function.getDescription() + "<br>Ex :<br><br>" + function.getSqlOrder());
                                c.setSummary(function.getDescription());
                                c.setParams(params);
                                a.add(c);
                        }
                }



                return a;
        }

        private ArrayList getSourceNamesCompletion(boolean addfields) {
                ArrayList a = new ArrayList();
                // adds the source names
                DataManager dataManager = Services.getService(DataManager.class);
                String[] s = dataManager.getSourceManager().getSourceNames();
                for (int i = 0; i < s.length; i++) {
                        if (!s[i].startsWith("gdms")) {
                                if (addfields) {
                                        doFieldsCompletion(s[i] + '.');
                                }

                                VariableCompletion c = new VariableCompletion(this, s[i], "TABLE");
                                StringBuilder str = new StringBuilder();
                                str.append("Fields :<br>");
                                Metadata m = getMetadataForDataSource(s[i]);
                                if (m == null) {
                                        // cannot mount the datasource
                                        continue;
                                }
                                try {
                                        for (int j = 0; j < m.getFieldCount(); j++) {
                                                str.append(m.getFieldName(j));
                                                str.append(" : ");
                                                str.append(TypeFactory.getTypeName(m.getFieldType(j).getTypeCode()).toUpperCase());
                                                str.append("<br>");
                                        }
                                } catch (DriverException e) {
                                }
                                c.setShortDescription(str.toString());
                                a.add(c);

                        }
                }
                return a;
        }

        /**
         * return completions for a specific (keywords or ID) token.
         * @param token the expected token
         * @param tokenImage reference to the list of token names
         * @return the Completion items to add
         */
        private ArrayList getCompletions(int token, String[] tokenImage, int currentToken, boolean tablesWithFields) {
                ArrayList a = new ArrayList();
                // secial (useless) tokens
                if (0 <= token && token <= 11) {
                        return a;
                }

                // special (useful) tokens
                if (69 <= token && token <= 76) {
                        switch (token) {
                                case SQLEngineConstants.BOOLEAN_LITERAL: {
                                        // adds TRUE and FALSE rather than BOOLEAN_LITERAL
                                        a.add(new TokenCompletion(this, 0, imgBool));
                                        a.add(new TokenCompletion(this, 1, imgBool));
                                        break;
                                }
                                case SQLEngineConstants.ID: {
                                        // hack around parser to prevent adding twice the IDs
                                        if (idAdded) {
                                                break;
                                        }
                                        idAdded = true;
                                        if (currentToken == SQLEngineConstants.EOF || currentToken == SQLEngineConstants.SEMICOLON) {
                                                break;
                                        }
                                        // adding function completions
                                        a.addAll(getFunctionCompletions());
                                        // adding sources completions
                                        a.addAll(getSourceNamesCompletion(tablesWithFields));
                                }
                        }
                } else {
                        // normal keyword token, easy
                        a.add(new TokenCompletion(this, token, tokenImage));
                }

                return a;
        }

        /**
         * Trims a string to get the last SQL Statement inside it.
         * @param str the string to trim
         * @return the actual SQL statement
         */
        private String getCurrentSQLStatement(String str) {
                // statement just finished
                if (str.endsWith(";")) {
                        return "";
                }
                // maybe there is several statements
                int pt = str.lastIndexOf(';');
                if (pt != -1) {
                        // keep only the last one
                        str = str.substring(pt + 1);
                }
                return str.replace('\n', ' ');
        }

        /**
         * retrieve <code>Metadata</code> for a specific data source.
         * @param sourceName the name of the source
         * @return the <code>Metadata</code> object containing the field names and types
         */
        private Metadata getMetadataForDataSource(String sourceName) {
                DataManager dataManager = Services.getService(DataManager.class);
                DataSourceFactory dsf = dataManager.getDataSourceFactory();
                SQLProcessor sqlProcessor = new SQLProcessor(dsf);

                // the query isn't really executed, do not worry
                String select = "SELECT * FROM " + sourceName + ";";
                try {
                        // lets just prepare the instruction
                        Instruction instruction = sqlProcessor.prepareInstruction(select);
                        return instruction.getResultMetadata();
                } catch (Exception ex) {
                        // bad, but who knows what is going on with this source
                        // let's just forget it in the completion list
                        return null;
                }
        }

        /**
         * Returns the current word before caret position, including preceding
         *  dot, ..., but not whitespace
         * @return the word
         */
        private String ReadCurrentWord(String content) {


                if (content.trim().endsWith(",")) {
                        return ",";
                }
                if (content.endsWith(" ")) {
                        return " ";
                }

                int start = content.length();
                while (start > 0) {
                        start--;
                        char ch = content.charAt(start);
                        if (ch == ' ' || ch == '.' || ch == ',' || ch == '(' || ch == ')' || ch == ';') {
                                break;
                        }
                }
                if (start == content.length()) {
                        return "";
                }
                return content.substring(start, content.length());
        }

        private String getTextContent() {
                try {
                        String content = textC.getDocument().getText(0, textC.getCaretPosition());

                        // removing multi-line comments
                        int sComm = content.indexOf("/*");
                        int eComm = content.indexOf("*/");
                        if (eComm < sComm) {
                                content = content.substring(sComm);
                                sComm = content.indexOf("/*");
                                eComm = content.indexOf("*/");
                        }
                        while (sComm != -1) {
                                if (eComm != - 1) {
                                        if (eComm < sComm) {
                                                content = content.substring(sComm);
                                                sComm = content.indexOf("/*");
                                                eComm = content.indexOf("*/");
                                        }
                                        content = content.substring(0, sComm)
                                                + content.substring(eComm + 2);
                                } else {
                                        content = content.substring(0, sComm);
                                }
                                sComm = content.indexOf("/*");
                                eComm = content.indexOf("*/");
                        }

                        // removing single-line comments
                        int comm = content.indexOf("--");
                        int ret = content.indexOf('\n');
                        while (comm != -1) {
                                if (ret != - 1) {
                                        content = content.substring(0, comm)
                                                + content.substring(ret);
                                } else {
                                        content = content.substring(0, comm);
                                }

                                comm = content.indexOf("--");
                                ret = content.indexOf('\n', comm + 2);
                        }

                        while (content.contains("\n\n")) {
                                content = content.replace("\n\n", "\n");
                        }
                        if (content.endsWith("\n")) {
                                content = content.substring(0, content.length() - 1);
                        }

                        if (rootText != null) {
                                content = rootText.trim() + ' ' + content;
                        }

                        return content;
                } catch (BadLocationException ex) {
                        return "";
                }
        }

        /**
         * @return the rootText
         */
        public String getRootText() {
                return rootText;
        }

        /**
         * @param rootText the rootText to set
         */
        public void setRootText(String rootText) {
                this.rootText = rootText;
        }

        /**
         * Check
         * @return
         */
        private boolean isAfterWhereStatement() {
                String content;

                // get all text to caret
                content = getTextContent().replace('\n', ' ');
                if (rootText != null) {
                        content = rootText.trim() + ' ' + content;
                }

                int wPos = content.toLowerCase().lastIndexOf("where");
                if (wPos == -1) {
                        return false;
                }
                int start = content.length();
                while (start > wPos + 5) {
                        start--;
                        char ch = content.charAt(start);
                        if (ch == ',' || ch == ';') {
                                return false;
                        }
                }
                return true;
        }

        private boolean isWithinComment() {
                try {
                        String content = textC.getDocument().getText(0, textC.getCaretPosition());
                        if (textC instanceof JTextArea) {
                                JTextArea area = (JTextArea) textC;
                                int comm = content.lastIndexOf("--");

                                int line = area.getLineOfOffset(comm);

                                return line == area.getLineOfOffset(area.getCaretPosition());

                        }
                } catch (BadLocationException ex) {
                }
                return false;
        }
}
