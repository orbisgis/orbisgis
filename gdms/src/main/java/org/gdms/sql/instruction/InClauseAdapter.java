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
public class InClauseAdapter extends Adapter {
    
    public boolean isNegated(){
        return getEntity().first_token.image.toLowerCase().equals("not");
    }
    
    public int getListLength() {
        return ((LValueListAdapter)getChilds()[0]).getListLength();
    }
    
    public Value getLValue(int index, long rowIndex) throws EvaluationException{
        return ((LValueListAdapter)getChilds()[0]).getLValue(index, rowIndex);        
    }
}