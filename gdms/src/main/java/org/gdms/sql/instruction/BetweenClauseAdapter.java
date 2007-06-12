/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.values.Value;

/**
 * Adaptador
 *
 */
public class BetweenClauseAdapter extends Adapter {

    public Value getInfValue() throws EvaluationException {
        return ((Expression)getChilds()[0]).evaluate();
    }

    public Value getSupValue() throws EvaluationException {
        return ((Expression)getChilds()[1]).evaluate();
    }

    public boolean isNegated() {
        return (getEntity().first_token.image.toLowerCase().equals("not"));
    }

}
