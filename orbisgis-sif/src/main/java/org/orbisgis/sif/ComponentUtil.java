package org.orbisgis.sif;

import java.awt.*;

/**
 * @author Alexis Guéganno
 */
public class ComponentUtil {

    private ComponentUtil(){}

    /**
     * Recursively enables or disables all the components contained in the
     * containers of {@code comps}.
     * @param enable Tell if the underlying components should be active or not
     * @param comp The root component.
     */
    public static void setFieldState(boolean enable, Component comp){
        comp.setEnabled(enable);
        if(comp instanceof Container){
            Component[] comps = ((Container)comp).getComponents();
            for(Component c: comps){
                setFieldState(enable, c);
            }
        }
    }
}
