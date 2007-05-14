package org.gdms.spatial;

import java.util.HashMap;

import org.gdms.data.values.Value;


/**
 * 
 */
public class PTTypes {
    public static final int GEOMETRY = 30000;
    public static final String STR_GEOMETRY = "GEOMETRY";
    public static HashMap<Integer, String> typesDescription = 
    new HashMap<Integer, String> ();

    static {
        java.lang.reflect.Field[] fields = Value.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                PTTypes.typesDescription.put((Integer)fields[i].get(null),
                        fields[i].getName());
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        typesDescription.put((Integer) GEOMETRY, STR_GEOMETRY);
    }
}
