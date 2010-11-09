/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole.syntax;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 *
 * @author agourlay
 */
public class TokenCompletion extends AbstractCompletion {

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TokenCompletion other = (TokenCompletion) obj;
        if (this.token != other.token) {
            return false;
        }
//        if (token == -1) {
//            return this.tokenImage[0].equals(other.tokenImage[0]);
//        }
        if (this.tokenImage.hashCode() != other.tokenImage.hashCode()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return tokenImage.hashCode() + this.token;
    }
    private int token;
    private String[] tokenImage;

    public TokenCompletion(CompletionProvider provider, int token, String[] tokenImage) {
        super(provider);
        this.token = token;
        this.tokenImage = tokenImage;
    }

    public TokenCompletion(CompletionProvider provider, String text) {
        super(provider);
        this.token = -1;
        this.tokenImage = new String[]{text};
    }

    @Override
    public String getReplacementText() {
        if (token == -1)
            return tokenImage[0];
        String img = tokenImage[token].replaceAll("\"", "");
        return img.toUpperCase();
    }

    @Override
    public String getSummary() {
        return null;
    }
}
