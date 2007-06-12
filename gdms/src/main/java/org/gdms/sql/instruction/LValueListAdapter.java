package org.gdms.sql.instruction;

import org.gdms.data.values.Value;

/**
 * @author Fernando Gonz�lez Cort�s
 */
public class LValueListAdapter extends Adapter {

    public int getListLength() {
        return getChilds().length;
    }

    public Value getLValue(int index) throws EvaluationException{
        return ((LValueElementAdapter)getChilds()[index]).evaluate();
    }

}
