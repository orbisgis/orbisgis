package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * A JPanel containing a combo that can be used to choose an unit of measure
 * @author Alexis Guéganno
 */
public class UomCombo extends JPanel {
    private static final I18n I18N = I18nFactory.getI18n(UomCombo.class);
    private ContainerItemProperties[] units;
    private JComboBox jcc;

    /**
     * Creates a JPanel containing
     * @param orig The unit of measure that must be displayed in the inner JComboBox after its creation.
     * @param units The known units of measure
     * @param label The label we want to display along the JComboBox.
     */
    public UomCombo(Uom orig, ContainerItemProperties[] units, String label){
        super();
        this.units = units;
        JComboBox jcb = getPointUomCombo(orig);
        this.add(new JLabel(label));
        this.add(jcb);
    }

    /**
     * ComboBox to configure the unit of measure used to draw th stroke.
     * @return A JComboBox the user can use to set the unit of measure of the symbol's dimensions.
     */
    private JComboBox getPointUomCombo(Uom uom){
        String[] values = new String[units.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = I18N.tr(units[i].toString());
        }
        jcc = new JComboBox(values);
        jcc.setSelectedItem(uom.toString().toUpperCase());
        return jcc;
    }

    public void addActionListener(ActionListener al){
        jcc.addActionListener(al);
    }
}
