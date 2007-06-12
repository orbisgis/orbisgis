package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

/**
 * @author Fernando Gonz�lez Cort�s
 */
public class LValueElementAdapter extends Adapter {

    public Value evaluate() throws EvaluationException {
        if (getEntity().first_token.image.toLowerCase().equals("null")){
            return ValueFactory.createNullValue();
        }else{
            return ((Expression)getChilds()[0]).evaluate();
        }
    }
}
