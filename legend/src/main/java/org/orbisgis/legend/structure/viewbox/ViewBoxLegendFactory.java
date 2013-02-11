package org.orbisgis.legend.structure.viewbox;

import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.structure.interpolation.SqrtInterpolationLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;

/**
 * Created with IntelliJ IDEA.
 * User: alexis
 * Date: 11/02/13
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public final class ViewBoxLegendFactory {

    private ViewBoxLegendFactory(){}

    /**
     * This utility method is used to create {@code ConstantViewBox} instances quickly. It does not test the given
     * {@link org.orbisgis.core.renderer.se.graphic.ViewBox} much, so it's up to the caller to check it can be used
     * to build a {@code ConstantViewbox}.
     * @param vb
     * @return
     * @throws ClassCastException If some argument of the ViewBox are not compatible with a {@code ConstantViewBox}.
     * @throws IllegalArgumentException if both height and width are null in the ViewBox.
     */
    public static ConstantViewBox createConstantViewBox(ViewBox vb) {
        RealLiteral height = (RealLiteral) vb.getHeight();
        RealLiteral width = (RealLiteral) vb.getWidth();
        if(height == null && width == null){
            throw new IllegalArgumentException("you're not supposed to set both height and width of a viewbox to null.");
        } else if(height == null){
            return new ConstantViewBox(new RealLiteralLegend(width),false,vb);
        } else if(width == null){
            return new ConstantViewBox(new RealLiteralLegend(height),true,vb);
        } else {
            return new ConstantViewBox(new RealLiteralLegend(height),new RealLiteralLegend(width),vb);
        }
    }

    /**
     * This utility method is used to create {@code MonovariateProportionalViewBox} instances quickly. It does not test the given
     * {@link org.orbisgis.core.renderer.se.graphic.ViewBox} much, so it's up to the caller to check it can be used
     * to build a {@code MonovariateProportionalViewBox}.
     * @param vb
     * @return
     * @throws ClassCastException If some argument of the ViewBox are not compatible with a {@code MonovariateProportionalViewBox}.
     * @throws IllegalArgumentException if both height and width are null or both are not null in the ViewBox.
     */
    public static MonovariateProportionalViewBox createMonovariateProportionalViewBox(ViewBox vb){
        RealParameter height = vb.getHeight();
        RealParameter width = vb.getWidth();
        if(height != null && width!=null){
            throw new IllegalArgumentException("One of the dimension must be null and the other one an interpolation on" +
                        "the square root of a field");
        }
        if(height == null && width==null){
            throw new IllegalArgumentException("One of the dimension must be null and the other one an interpolation on" +
                        "the square root of a field");
        }
        if (height == null){
            return new MonovariateProportionalViewBox(new SqrtInterpolationLegend((Interpolate2Real)width),false,vb);
        } else {
            return new MonovariateProportionalViewBox(new SqrtInterpolationLegend((Interpolate2Real)height),true,vb);
        }
    }
}
