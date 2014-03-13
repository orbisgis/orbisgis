package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;

import javax.swing.*;
import java.text.NumberFormat;

/**
 * Dedicated key editor for interval classifications. It provides
 * a formatted field rather than a simple JTextField in order to
 * parse inputs efficiently
 * @author Alexis Guéganno
 */
public abstract class KeyEditorCategorized<U extends LineParameters> extends KeyEditorMappedLegend<Double, U> {

    private JFormattedTextField numberField;

    /**
     * Default constructor - instanciates the inned JFormattedTextField.
     */
    public KeyEditorCategorized(){
        numberField =new JFormattedTextField(NumberFormat.getNumberInstance());
        numberField.setActionCommand(EDIT);
        numberField.addActionListener(this);
    }

    @Override
    protected Double getNotUsedKey(Double prev){
        try{
            Double d = Double.valueOf(getField().getText());
            return d.equals(prev) ? prev :((AbstractCategorizedLegend)getLegend()).getNotUsedKey(d);
        } catch (NumberFormatException nfe){
            return prev;
        }
    }

    /**
     * Gets the associated JFormattedTextField
     * @return The associated JFormattedTextField
     */
    @Override
    public JFormattedTextField getField(){
        return numberField;
    }

}