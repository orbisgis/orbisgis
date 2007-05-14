package org.gdms.data.edition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Field {
    private String name;
    private int type;
    private int originalIndex;
    private String driverType;
    private HashMap<String, String> params;
    
    public Field(int originalIndex, String name, String driverType, int type, String[] paramNames, String[] paramValues) {
        super();
        this.originalIndex = originalIndex;
        this.name = name;
        this.driverType = driverType;
        this.type = type;
        this.params = new HashMap<String, String>();
        for (int i = 0; i < paramValues.length; i++) {
            this.params.put(paramNames[i], paramValues[i]);
        }
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

    public void setOriginalIndex(int originalIndex) {
        this.originalIndex = originalIndex;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String[] getParamNames() {
        Iterator<String> i = params.keySet().iterator();
        ArrayList<String> ret = new ArrayList<String>();
        while (i.hasNext()) {
            ret.add(i.next());
        }
        
        return ret.toArray(new String[0]);
    }

    public String[] getParamValues() {
        Iterator<String> i = params.values().iterator();
        ArrayList<String> ret = new ArrayList<String>();
        while (i.hasNext()) {
            ret.add(i.next());
        }
        
        return ret.toArray(new String[0]);
    }
}