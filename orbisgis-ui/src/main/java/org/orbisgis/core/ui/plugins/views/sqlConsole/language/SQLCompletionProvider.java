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
package org.orbisgis.core.ui.plugins.views.sqlConsole.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.rsyntaxtextarea.modes.SQLTokenMaker;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.DefaultType;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.plugins.views.sqlConsole.language.matcher.SQLMatcher;

/**
 *
 * @author antoine
 */
public class SQLCompletionProvider extends DefaultCompletionProvider implements CaretListener, SQLMetadataListener {

    // other useful classes
    private SQLMetadataManager metManager;
    private DataManager dataManager;
    // text field
    private JTextComponent textC;
    // auto completion
    private AutoCompletion auto;
    // init text
    private String rootText;
    // keyword
    // caching
    private final Map<String, Completion> cachedCompletions = Collections.synchronizedMap(new TreeMap<String, Completion>());
    // common tokens
    private SQLMatcher matcher;

    /**
     * Default constructor
     * @param textC the JTextComponent that needs auto-completion.
     * @param metManager
     */
    public SQLCompletionProvider(JTextComponent textC, SQLMetadataManager metManager) {
        this(textC, metManager, null);
    }

    /**
     * This constructor allows the use of a fixed string like
     * "SELECT * FROM toto WHERE" to filter the completion inside the
     * JTextComponent. The content of the JTextComponent is added to rootText
     * and then processed by the completion parser.
     * @param textC the JTextComponent that needs auto-completion.
     * @param metManager 
     * @param rootText a fixed string to be append before the completion starts
     */
    public SQLCompletionProvider(JTextComponent textC, SQLMetadataManager metManager, String rootText) {
        this.metManager = metManager;
        this.textC = textC;
        this.rootText = rootText;
    }

    /**
     * Installs and enables auto-completion.
     * @return the AutoCompletion object linked to the JTextComponent.
     */
    public AutoCompletion install() {
        // listen to the MetadataManager
        metManager.registerMetadataListener(this);

        // listen to the caret
        textC.addCaretListener(this);

        // for autocomplete to work
        this.setParameterizedCompletionParams('(', ", ", ')');
        auto = new AutoCompletion(this);
        auto.setAutoCompleteSingleChoices(true);
        auto.setShowDescWindow(true);
        auto.install(textC);

        dataManager = Services.getService(DataManager.class);
        
        matcher = new SQLMatcher(this);

        return auto;
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        String content = getTextContent();

        // take care of rootText
        if (rootText != null) {
            content = rootText + content;
        }

        clear();
        matcher.match(content);
    }

    public void addSourceNamesCompletion(boolean addfields) {
        ArrayList<Completion> a = new ArrayList<Completion>();
        HashMap<String, SQLFieldCompletion> nn = new HashMap<String, SQLFieldCompletion>();

        // adds the source names
        String[] s = metManager.getSourceNames();
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

                    Metadata m = metManager.getMetadataFromCache(name);
                    if (m == null) {
                        m = metManager.getMetadata(name);
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
        addCompletions(a);
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
        Metadata m = metManager.getMetadata(table);
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

    @Override
    public void metadataAdded(String name, Metadata m) {
    }

    @Override
    public void metadataRemoved(String name, Metadata m) {
    }

    /**
     * Frees all external resources linking to this Provider
     *
     * This method MUST be called when unloading the JComponent associated with
     * the provider.
     * If it is not called the provider will never be garbage-collected.
     */
    public void freeExternalResources() {
        cachedCompletions.clear();
    }

    private String getTextContent() {
        String content;
        try {
            content = textC.getDocument().getText(0, textC.getCaretPosition());

        } catch (BadLocationException ex) {
            return "";
        }

        // some cleaning up
        content = removeMultilineComments(content);
        content = removeSinglelineComments(content);
        content = getLastSQLStatement(content);

        return content;
    }

    /**
     * Trims a string to get the last SQL Statement inside it.
     * @param str the string to trim
     * @return the actual SQL statement
     */
    private String getLastSQLStatement(String str) {
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

    private String removeSinglelineComments(String content) {
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
    }

    private String removeMultilineComments(String content) {
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
        return content;
    }

    public void setRootText(String rootText) {
        this.rootText = rootText;
    }
}
