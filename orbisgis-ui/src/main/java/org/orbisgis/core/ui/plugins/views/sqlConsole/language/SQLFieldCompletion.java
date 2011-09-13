/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.ui.plugins.views.sqlConsole.language;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

/**
 * Completion class dedicated to fields
 * @author Antoine Gourlay
 */
public class SQLFieldCompletion extends VariableCompletion {

    /**
     * Returns the field with the following format : "name : type".
     * @return formatted field name and type
     */
    @Override
    public String toString() {
        return super.toString() + " : " + getType();
    }

    public SQLFieldCompletion(CompletionProvider provider, String name, String type) {
        super(provider,name,type);
    }
}
