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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.VariableCompletion;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.DefaultType;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceManager;
import org.gdms.source.SourceRemovalEvent;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.TokenMgrError;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.background.UniqueJobID;
import org.orbisgis.progress.IProgressMonitor;

/**
 * This class provides auto-completion for a JTextComponent.
 *
 * @author Antoine Gourlay
 */
public class SQLCompletionProvider extends DefaultCompletionProvider implements CaretListener, SourceListener {

        private JTextComponent textC;
        private AutoCompletion auto;
        private static final String[] imgBool = {"TRUE", "FALSE"};
        // Workaround to fix parser inconsistency
        private boolean idAdded;
        private String rootText;
        // caching
        private final Map<String, Metadata> cachedMetadatas = Collections.synchronizedMap(new TreeMap<String, Metadata>());
        private final Map<String, Completion> cachedCompletions = Collections.synchronizedMap(new TreeMap<String, Completion>());
        private final BlockingDeque<String> sourcesToLoad = new LinkedBlockingDeque<String>();
        // source loading & thread synchronisation
        private final Object lock = new Object();
        private volatile boolean isLoadingSources = false;
        private UniqueJobID jobID;
        // dsf
        DataManager dataManager;

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

                // listen to SourceManager
                dataManager = Services.getService(DataManager.class);
                dataManager.getSourceManager().addSourceListener(this);

                // queue all currently available sources
                Collections.addAll(sourcesToLoad, dataManager.getSourceManager().getSourceNames());

                jobID = new UniqueJobID();
        }

        /**
         * Frees all external resources linking to this Provider
         *
         * This method MUST be called when unloading the JComponent associated with
         * the provider.
         * If it is not called the provider will never be garbage-collected.
         */
        public void freeExternalResources() {
                // unlisten to SourceManager
                dataManager.getSourceManager().removeSourceListener(this);
        }

        private boolean checkSourcesToLoad() {
                // maybe we are already loading -> exit
                synchronized (lock) {
                        if (isLoadingSources) {
                                return true;
                        }
                }

                // maybe there is no need to load
                if (sourcesToLoad.isEmpty()) {
                        return false;
                }

                // loading
                synchronized (lock) {
                        isLoadingSources = true;
                }

                // to prevent the user from triggering a completion refresh
                // while the sources are loading
                textC.removeCaretListener(this);
                auto.uninstall();

                BackgroundManager bm = Services.getService(BackgroundManager.class);

                bm.nonBlockingBackgroundOperation(jobID, new BackgroundJob() {

                        @Override
                        public void run(IProgressMonitor pm) {
                                String source;
                                while (true) {
                                        source = sourcesToLoad.poll();

                                        if (source != null) {
                                                if (pm.isCancelled()) {
                                                        return;
                                                }
                                                // caching the source
                                                getMetadataForDataSource(source);
                                        } else {
                                                // there is no sources anymore -> exit
                                                break;
                                        }
                                }

                                // to resume the refreshing of the completions
                                textC.addCaretListener(SQLCompletionProvider.this);
                                auto.install(textC);

                                // finished loading
                                synchronized (lock) {
                                        isLoadingSources = false;
                                }

                                // trigger an update to compensate the call to this method
                                // from caretUpdate
                                SwingUtilities.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                                caretUpdate(null);
                                        }
                                });
                        }

                        @Override
                        public String getTaskName() {
                                return "Caching SQL Completions";
                        }
                });

                return true;
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

                // loading & caching sources Metadata if necessary
                if (checkSourcesToLoad()) {
                        return;
                }

                doCompletion(content);
        }

        /**
         * Core completion method.
         * @param content text on which completion is done
         */
        private void doCompletion(String content) {

                // get the correct part of the text
                String sql = getCurrentSQLStatement(content);

                // getting the current 'word'
                String word = ReadCurrentWord(sql);

                // we are in a list, either fields or tables
                if (word.startsWith(",")) {
                        doListCompetion(sql);
                        return;
                }


                // special case : no completion after ) or ;
                if (word.equals(")") || word.equals(";")) {
                        clear();
                        return;
                }
                // special case : field completion after .
                if (word.startsWith(".")) {
                        clear();
                        addCompletions(new ArrayList(getFieldsCompletion(sql.substring(0, sql.lastIndexOf(word) + 1))));
                        return;

                        // hack to go around parser limitation
                } else if (word.startsWith("(")) {
                        sql = sql.substring(0, sql.lastIndexOf(word)) + '(';
                } else if (!word.isEmpty() && !word.equals(" ") && !word.startsWith(";")) {
                        sql = sql.substring(0, sql.lastIndexOf(word)) + ' ';
                }

                // hack to go around parser limitation (bis)
                int par = sql.lastIndexOf('(');
                int par2 = sql.lastIndexOf(')');
                if ((par > par2) && sql.startsWith("(SELECT", par)) {
                        sql = sql.substring(par + 1);
                }

                // hack to go around parser limitation (again) with inner-queries
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

                // yet another hack
                // we are inside a list, either fields of tables
                if (ReadCurrentWord(sql).startsWith(",")) {
                        doListCompetion(sql);
                        return;
                }

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

        private void doListCompetion(String sql) {
                clear();
                if (sql.toLowerCase().lastIndexOf("from") > sql.toLowerCase().lastIndexOf("select")) {
                        // FROM clause
                        addCompletions(getSourceNamesCompletion(false));
                } else {
                        // SELECT clause
                        addCompletions(getSourceNamesCompletion(true));
                        addCompletions(new ArrayList(getFunctionCompletions()));
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
        private List<Completion> getFieldsCompletion(String sql) {
                List<Completion> a = new ArrayList<Completion>();
                if (sql.length() < 2) {
                        return a;
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
                        return a;
                }
                try {
                        for (int i = 0; i < m.getFieldCount(); i++) {
                                final String fieldName = m.getFieldName(i);
                                // trying in the cache
                                Completion c = cachedCompletions.get(fieldName);
                                if (c != null) {
                                        a.add(c);
                                        continue;
                                }
                                // else we build it

                                SQLFieldCompletion complet = new SQLFieldCompletion(this, fieldName, DefaultType.typesDescription.get(m.getFieldType(i).getTypeCode()));
                                complet.setDefinedIn("<b>" + table + "</b>");
                                // and add it to the cache
                                cachedCompletions.put(fieldName, c);
                                a.add(complet);
                        }
                } catch (DriverException ex) {
                        // needed for m.getField*
                }
                return a;
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

                if (isAfterWhereStatement(content)) {
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

                        // trying to get it from the cache
                        Completion compl = cachedCompletions.get(function.getName());
                        if (compl != null) {
                                a.add(compl);
                                continue;
                        }
                        // else we build it

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
                                // and we cache it for reuse
                                cachedCompletions.put(function.getName(), c);
                                a.add(c);
                        }
                }



                return a;
        }

        private ArrayList getSourceNamesCompletion(boolean addfields) {
                ArrayList<Completion> a = new ArrayList<Completion>();
                HashMap<String, SQLFieldCompletion> nn = new HashMap<String, SQLFieldCompletion>();

                // no need to load any sources anymore, this method will load
                // them all necessarily
                // Note that it should already be empty anyway, but still...
                sourcesToLoad.clear();

                // adds the source names
                String[] s = dataManager.getSourceManager().getSourceNames();
                for (int i = 0; i < s.length; i++) {
                        ArrayList<String> ss = new ArrayList<String>();
                        final String name = s[i];
                        ss.add(name);
                        try {
                                Collections.addAll(ss, dataManager.getSourceManager().getAllNames(name));
                        } catch (NoSuchTableException ex) {
                        }
                        for (int k = 0; k < ss.size(); k++) {
                                final String alias = ss.get(k);
                                if (!alias.startsWith("gdms")) {
                                        // check for an existing completion
                                        Completion compl = cachedCompletions.get(alias);
                                        if (compl != null) {
                                                a.add(compl);
                                                if (addfields) {
                                                        List<Completion> cp = getFieldsCompletion(name + '.');
                                                        for (int j = 0; j < cp.size(); j++) {
                                                                final SQLFieldCompletion localCompl = (SQLFieldCompletion) cp.get(j);
                                                                final String currCompl = localCompl.getName();
                                                                if (nn.containsKey(currCompl)) {
                                                                        String def = nn.get(currCompl).getDefinedIn();
                                                                        def = def.replace("</b>", ", " + alias + "</b>");
                                                                        nn.get(currCompl).setDefinedIn(def);
                                                                } else {
                                                                        nn.put(currCompl, localCompl);
                                                                }
                                                        }
                                                }
                                                continue;
                                        }

                                        // no existing completion, let's built it

                                        // adding fields
                                        if (addfields) {
                                                List<Completion> cp = getFieldsCompletion(name + '.');
                                                for (int j = 0; j < cp.size(); j++) {
                                                        final SQLFieldCompletion localCompl = (SQLFieldCompletion) cp.get(j);
                                                        final String currCompl = localCompl.getName();
                                                        if (nn.containsKey(currCompl)) {
                                                                String def = nn.get(currCompl).getDefinedIn();
                                                                def = def.replace("</b>", ", " + alias + "</b>");
                                                                nn.get(currCompl).setDefinedIn(def);
                                                        } else {
                                                                nn.put(currCompl, localCompl);
                                                        }
                                                }
                                        }

                                        Metadata m = cachedMetadatas.get(name);
                                        if (m == null) {
                                                m = getMetadataForDataSource(name);
                                                if (m == null) {
                                                        // cannot mount the datasource
                                                        continue;
                                                }
                                        }

                                        VariableCompletion c = new VariableCompletion(this, alias, "TABLE");
                                        StringBuilder str = new StringBuilder();
                                        if (!name.equals(alias) && !name.startsWith("gdms")) {
                                                str.append("<b>alias</b> for <i>");
                                                str.append(name);
                                                str.append("</i>.<br><br>");
                                        }
                                        str.append("Fields :<br>");
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
                                        // caching for reuse
                                        cachedCompletions.put(alias, c);
                                        a.add(c);

                                }
                        }
                }

                // adding fields if needed
                a.addAll(nn.values());
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
                } // maybe there is several statements
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

                // checking the cache
                Metadata m = cachedMetadatas.get(sourceName);
                if (m != null) {
                        return m;
                }
                // else we have to retrieve it from a DataSource
                // this is an expensive operation that should only be done once
                try {
                        DataSource ds = dataManager.getDataSourceFactory().getDataSource(sourceName);
                        ReadOnlyDriver d = ds.getDriver();
                        if ((d.getType() & SourceManager.RASTER) == SourceManager.RASTER) {
                                // this is a raster : easy !
                                m = d.getMetadata();
                        } else {
                                ds.open();
                                m = d.getMetadata();
                                ds.close();
                        }
                        // then we cache it
                        cachedMetadatas.put(sourceName, m);
                        return m;
                } catch (DriverLoadException ex) {
                        return null;
                } catch (NoSuchTableException ex) {
                        return null;
                } catch (DataSourceCreationException ex) {
                        return null;
                } catch (DriverException ex) {
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

                        } // removing single-line comments
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

        private boolean isAfterWhereStatement(String content) {

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

        @Override
        public void sourceAdded(final SourceEvent e) {
                if (!e.isWellKnownName()) {
                        return;
                }
                sourcesToLoad.add(e.getName());
        }

        @Override
        public void sourceRemoved(SourceRemovalEvent e) {

                Metadata m = cachedMetadatas.remove(e.getName());
                cachedCompletions.remove(e.getName());

                // we also remove the completion items of all the fields
                // the will be rebuild without this source referenced
                if (m != null) {
                        try {
                                for (int i = 0; i < m.getFieldCount(); i++) {
                                        cachedCompletions.remove(m.getFieldName(i));
                                }
                        } catch (DriverException ex) {
                                // too bad...
                                // we already used it that way, so it won't fail
                        }
                } else {
                        // maybe it was scheduled for loading
                        sourcesToLoad.remove(e.getName());
                }
        }

        @Override
        public void sourceNameChanged(SourceEvent e) {
                Metadata m = cachedMetadatas.remove(e.getName());
                if (m != null) {
                        cachedMetadatas.put(e.getNewName(), m);
                } else {
                        // this source wasn't cached
                        // maybe scheduled for loading
                        sourcesToLoad.remove(e.getName());
                        sourcesToLoad.add(e.getNewName());
                }
        }
}
