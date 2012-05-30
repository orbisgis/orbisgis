/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.util.Comparator;
import java.util.Locale;

/**
 * This comparator intends to compare instances of {@code LocalizedText}. It
 * will perform the comparison using first the {@code Locale} definition, and
 * then, if they are equal (according to the same conditions than described
 * in {@code LocaleComparator}), using the inner {@code String} value.
 * @author alexis
 */
public class LocaleAndTextComparator  implements Comparator<LocalizedText> {

    @Override
    public int compare(LocalizedText o1, LocalizedText o2) {
        int comp = compareLocale(o1, o2);
        if(comp == 0){
            comp = o1.getValue().compareTo(o2.getValue());
            if (comp<0) {
                comp = -1;
            } else if(comp >0){
                comp = 1;
            }
        }
        return comp;
    }

    private int compareLocale(LocalizedText l1, LocalizedText l2) {
        Locale o1 = l1.getLocale();
        Locale o2 = l2.getLocale();
        if(o1 == null && o2 == null){
            return 0;
        } else if(o1 == null) {
            //o2 is not null.
            return -1;
        } else if(o2 == null){
            //o1 is not null.
            return 1;
        } else {
            return o1.toString().compareTo(o2.toString());
        }
    }

}
