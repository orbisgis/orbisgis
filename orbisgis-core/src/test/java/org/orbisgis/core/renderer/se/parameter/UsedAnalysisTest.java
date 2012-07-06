package org.orbisgis.core.renderer.se.parameter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.renderer.se.parameter.real.*;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;

/**
 *
 * @author alexis
 */


public class UsedAnalysisTest {

    @Test
    public void testInitialization(){
        UsedAnalysis ua = new UsedAnalysis();
        assertFalse(ua.isCategorizeUsed());
        assertFalse(ua.isRecodeUsed());
        assertFalse(ua.isInterpolateUsed());
    }

    @Test
    public void testCategorize(){
        UsedAnalysis ua = new UsedAnalysis();
        Categorize2Real c2c = new Categorize2Real(new RealLiteral(), new RealLiteral(), new RealAttribute("youhou"));
        Categorize2Real c2cb = new Categorize2Real(new RealLiteral(), new RealLiteral(), new RealAttribute("youhou"));
        ua.include(c2c);
        assertTrue(ua.isCategorizeUsed());
        ua.include(c2cb);
        assertTrue(ua.isCategorizeUsed());
        assertFalse(ua.isRecodeUsed());
        assertFalse(ua.isInterpolateUsed());
    }

    @Test
    public void testRecode(){
        UsedAnalysis ua = new UsedAnalysis();
        Recode2Real c2c = new Recode2Real(new RealLiteral(), new StringAttribute("youhou"));
        Recode2Real c2cb = new Recode2Real(new RealLiteral(), new StringAttribute("youhou"));
        ua.include(c2c);
        assertTrue(ua.isRecodeUsed());
        ua.include(c2cb);
        assertTrue(ua.isRecodeUsed());
        assertFalse(ua.isCategorizeUsed());
        assertFalse(ua.isInterpolateUsed());
    }

    @Test
    public void testInterpolate(){
        UsedAnalysis ua = new UsedAnalysis();
        Interpolate2Real c2c = new Interpolate2Real(new RealLiteral());
        Interpolate2Real c2cb = new Interpolate2Real(new RealLiteral());
        ua.include(c2c);
        assertTrue(ua.isInterpolateUsed());
        ua.include(c2cb);
        assertTrue(ua.isInterpolateUsed());
        assertFalse(ua.isCategorizeUsed());
        assertFalse(ua.isRecodeUsed());
    }
}
