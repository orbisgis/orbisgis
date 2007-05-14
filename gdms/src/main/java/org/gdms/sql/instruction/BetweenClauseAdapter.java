/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.values.Value;

/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class BetweenClauseAdapter extends Adapter {
 
    public Value getInfValue(long row) throws EvaluationException {
        return ((Expression)getChilds()[0]).evaluate(row);
    }

    public Value getSupValue(long row) throws EvaluationException {
        return ((Expression)getChilds()[1]).evaluate(row);
    }
    
    public boolean isNegated() {
        return (getEntity().first_token.image.toLowerCase().equals("not"));
    }

}
