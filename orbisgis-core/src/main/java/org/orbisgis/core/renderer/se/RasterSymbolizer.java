/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.HashSet;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.raster.RasterSymbolizerType;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.Interpolate2Color;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.raster.Channel;
import org.orbisgis.core.renderer.se.raster.ContrastEnhancement;
import org.orbisgis.core.renderer.se.raster.OverlapBehavior;


/**
 * @ todo implements almost all...
 * @author Maxence Laurent
 */
public class RasterSymbolizer extends Symbolizer {


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

    /*
     * SE request either a LineSymbolizer or an AreaSymbolizer
     * Since a line symbolizer is an area one witout the fill element, we only provide the latter
     */
    private AreaSymbolizer outline;
    @Override
    public void draw(Graphics2D g2, DataSet sds, long fid,
            boolean selected, MapTransform mt, Geometry the_geom, RenderContext perm)
            throws ParameterException, IOException, DriverException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Uom getUom() {
        return Uom.MM;
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
		if (this.opacity != null){
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
    public HashSet<String> dependsOnFeature() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

        @Override
        public UsedAnalysis getUsedAnalysis() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

}
