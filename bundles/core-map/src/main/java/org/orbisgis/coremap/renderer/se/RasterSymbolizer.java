/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se;

import org.locationtech.jts.geom.Geometry;
import java.awt.Graphics2D;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.raster.RasterSymbolizerType;


import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.coremap.renderer.se.parameter.color.Interpolate2Color;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.coremap.renderer.se.raster.Channel;
import org.orbisgis.coremap.renderer.se.raster.ContrastEnhancement;
import org.orbisgis.coremap.renderer.se.raster.OverlapBehavior;

/**
 * @ todo implements almost all...
 *
 * @author Maxence Laurent, Erwan Bocher
 */
public class RasterSymbolizer extends Symbolizer implements UomNode {

    private RealParameter opacity;
    private Channel redChannel;
    private Channel greenChannel;
    private Channel blueChannel;
    private Channel grayChannel;
    private boolean isColored; // true => use red, green and blue channels; false => use gray
    private OverlapBehavior overlapBehavior;
    private Interpolate2Color interpolatedColorMap;
    private Categorize2Color categorizedColorMap;
    private boolean useInterpolationForColorMap; // true => interpolatedColorMap, False => CategorizedColorMap
    private ContrastEnhancement contrastEnhancement;
    private double gamma;
    private boolean shadedReliefOnlyBrightness;
    private double shadedReliefFactor;
    private Uom uom;
    private AreaSymbolizer outline;

    /**
     * Create a rastersymbolizer with a default AreaSymbolizer
     *
     * SE request either a LineSymbolizer or an AreaSymbolizer Since a line
     * symbolizer is an area one without the fill element, we only provide the
     * latter
     */
    public RasterSymbolizer() {
        outline = new AreaSymbolizer();
        setUom(Uom.PX);
        name = "Raster symbolizer";
    }

    @Override
    public void draw(Graphics2D g2, ResultSet rs, long fid,
            boolean selected, MapTransform mt, Geometry the_geom)
            throws ParameterException, IOException, SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Channel getBlueChannel() {
        return blueChannel;
    }

    public void setBlueChannel(Channel blueChannel) {
        this.blueChannel = blueChannel;
    }

    public Categorize2Color getCategorizedColorMap() {
        return categorizedColorMap;
    }

    public void setCategorizedColorMap(Categorize2Color categorizedColorMap) {
        this.categorizedColorMap = categorizedColorMap;
    }

    public ContrastEnhancement getContrastEnhancement() {
        return contrastEnhancement;
    }

    public void setContrastEnhancement(ContrastEnhancement contrastEnhancement) {
        this.contrastEnhancement = contrastEnhancement;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public Channel getGrayChannel() {
        return grayChannel;
    }

    public void setGrayChannel(Channel grayChannel) {
        this.grayChannel = grayChannel;
    }

    public Channel getGreenChannel() {
        return greenChannel;
    }

    public void setGreenChannel(Channel greenChannel) {
        this.greenChannel = greenChannel;
    }

    public Interpolate2Color getInterpolatedColorMap() {
        return interpolatedColorMap;
    }

    public void setInterpolatedColorMap(Interpolate2Color interpolatedColorMap) {
        this.interpolatedColorMap = interpolatedColorMap;
    }

    public boolean isIsColored() {
        return isColored;
    }

    public void setIsColored(boolean isColored) {
        this.isColored = isColored;
    }

    public RealParameter getOpacity() {
        return opacity;
    }

    public void setOpacity(RealParameter opacity) {
        this.opacity = opacity;
        if (this.opacity != null) {
            this.opacity.setContext(RealParameterContext.PERCENTAGE_CONTEXT);
        }
    }

    public AreaSymbolizer getOutline() {
        return outline;
    }

    public void setOutline(AreaSymbolizer outline) {
        this.outline = outline;
    }

    public OverlapBehavior getOverlapBehavior() {
        return overlapBehavior;
    }

    public void setOverlapBehavior(OverlapBehavior overlapBehavior) {
        this.overlapBehavior = overlapBehavior;
    }

    public Channel getRedChannel() {
        return redChannel;
    }

    public void setRedChannel(Channel redChannel) {
        this.redChannel = redChannel;
    }

    public double getShadedReliefFactor() {
        return shadedReliefFactor;
    }

    public void setShadedReliefFactor(double shadedReliefFactor) {
        this.shadedReliefFactor = shadedReliefFactor;
    }

    public boolean isShadedReliefOnlyBrightness() {
        return shadedReliefOnlyBrightness;
    }

    public void setShadedReliefOnlyBrightness(boolean shadedReliefOnlyBrightness) {
        this.shadedReliefOnlyBrightness = shadedReliefOnlyBrightness;
    }

    public boolean isUseInterpolationForColorMap() {
        return useInterpolationForColorMap;
    }

    public void setUseInterpolationForColorMap(boolean useInterpolationForColorMap) {
        this.useInterpolationForColorMap = useInterpolationForColorMap;
    }

    @Override
    public final void setUom(Uom uom) {
        if (uom != null) {
            this.uom = uom;
        } else {
            this.uom = Uom.PX;
        }
    }

    @Override
    public JAXBElement<RasterSymbolizerType> getJAXBElement() {
        return null;
    }

    public RasterSymbolizer(JAXBElement<RasterSymbolizerType> st) throws InvalidStyle {
        super(st);
        RasterSymbolizerType lst = st.getValue();
        System.out.println("RasterSymb");

        System.out.println("  Name: " + lst.getName());
        System.out.println("  UoM: " + lst.getUom());
        System.out.println("  Version: " + lst.getVersion());
        System.out.println("  Desc: " + lst.getDescription());
        System.out.println("  Geom: " + lst.getGeometry());
    }

    @Override
    public List<SymbolizerNode> getChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }

    @Override
    public final Uom getUom() {
        return uom == null ? Uom.PX : uom;
    }
}
