package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryParameter;


/**
 * Entry point for all kind of symbolizer
 * This abstract class contains only the name, the geom and a description of the symbolizer
 * @todo Add a general draw method that fit well for vectors and raster; implement fetch default geometry
 * @author maxence
 */
public abstract class Symbolizer implements SymbolizerNode {

    public Symbolizer(){
        name = "Symbolizer name";
        desc = "";
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return desc;
    }

    public void setDescription(String description){
        desc = description;
    }

    public GeometryParameter getGeometry() {
        return the_geom;
    }

    public void setGeometry(GeometryParameter the_geom) {
        this.the_geom = the_geom;
    }

    public Geometry getTheGeom(SpatialDataSourceDecorator sds, long fid) throws DriverException{
        if (the_geom != null){
            return the_geom.getTheGeom(sds, fid);
        }
        else{
            return sds.getGeometry(fid);
        }
    }

    @Override
    public SymbolizerNode getParent(){
        return null;
    }

    @Override
    public void setParent(SymbolizerNode node){
       // TODO Throw symbolizer root
    }
   

    private String name;
    private String desc;
    private GeometryParameter the_geom;
}
