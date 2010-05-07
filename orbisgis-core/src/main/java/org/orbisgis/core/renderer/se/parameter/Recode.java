package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import org.gdms.data.DataSource;

import org.orbisgis.core.renderer.se.parameter.string.*;

public abstract class Recode<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter {

    public Recode(FallbackType fallbackValue, StringParameter lookupValue){
        this.fallbackValue = fallbackValue;
        this.lookupValue = lookupValue;
        mapItems = new ArrayList<MapItem<ToType>>();
    }

    
    @Override
    public final boolean dependsOnFeature(){
        if (this.getLookupValue().dependsOnFeature())
            return true;

        int i;
        for (i=0;i<this.getNumMapItem();i++){
           if (this.getMapItemValue(i).dependsOnFeature())
               return true;
        }

        return false;
    }

    public void setFallbackValue(FallbackType fallbackValue){
        this.fallbackValue = fallbackValue;
    }

    public FallbackType getFallbackValue(){
        return fallbackValue;
    }

    // TODO  On doit pouvoir récuperer des string ou des couleurs
    public void setLookupValue(StringParameter lookupValue){
        this.lookupValue = lookupValue;
    }

    public StringParameter getLookupValue(){
        return lookupValue;
    }

    /**
     * Return the number of unique value defined within the function.
     *  @return number of unique value
     */
    public int getNumMapItem(){
        return mapItems.size();
    }

    /**
     * Add a new map item
     * @param key
     * @param value
     */
    public void addMapItem(String key, ToType value){
        MapItem<ToType> item = new MapItem<ToType>(value, key);

        if (mapItems.contains(item)){
            //TODO  throw break unique value rules
        }
        else{
            mapItems.add(item);
        }
    }

    public ToType getMapItemValue(String key){
        MapItem<ToType> item = new MapItem<ToType>(null, key);
        return mapItems.get(mapItems.indexOf(item)).getValue();
    }

    public ToType getMapItemValue(int i){
        return mapItems.get(i).getValue();
    }
    
    public String getMapItemKey(int i){
        return mapItems.get(i).getKey();
    }

    public void removeMapItem(String key){
        MapItem<ToType> item = new MapItem<ToType>(null, key);
        mapItems.remove(item);
    }

    public ToType getParameter(DataSource ds, long fid){
        try{
            String key = lookupValue.getValue(ds, fid);
            return getMapItemValue(key);
        }
        catch(Exception e){
            return fallbackValue;
        }
    }

    protected FallbackType fallbackValue;
    protected StringParameter lookupValue;

    protected ArrayList<MapItem<ToType>> mapItems; // TODO switch to hash table <k: String, v: ToType>
}
