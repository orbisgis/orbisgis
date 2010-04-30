package org.orbisgis.core.renderer.se.parameter;

public class MapItem<Type>  {

    public MapItem(Type value, String key){
        this.value = value;
        this.key = key;
    }

    public Type getValue(){
        return value;
    }

    public void setValue(Type value){
        this.value = value;
    }

    public String getKey(){
        return key;
    }

    public void setData(String key){
        this.key = key;
    }


    @Override
    public boolean equals(Object o){
        MapItem<Type> item = (MapItem<Type>) o;

        // TODO Check type

        return item.key.equals(this.key);
    }


    public boolean equals(MapItem item){
        return item.key.equals(this.key);
    }

    public boolean equals(String key){
        return key.equals(this.key);
    }

    private String key = null;
    private Type value = null;
}
