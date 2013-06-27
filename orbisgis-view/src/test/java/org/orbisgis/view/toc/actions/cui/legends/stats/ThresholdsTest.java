package org.orbisgis.view.toc.actions.cui.legends.stats;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import java.util.Iterator;
import java.util.SortedSet;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexis Guéganno
 */
public class ThresholdsTest {

    @Test
    public void testQuantiles() throws Exception {
        Double[] vals = new Double[]{1.0,2.0,3.,4.,5.,6.,7.,8.,9.,10.,11.,12.};
        DescriptiveStatistics ds = new DescriptiveStatistics();
        for(Double d : vals){
            ds.addValue(d);
        }
        Thresholds th = new Thresholds(ds,"name");
        SortedSet<Double> quantiles = th.getQuantiles(3);
        //Classes :
        //1 2 3 4
        //5 6 7 8
        //9 10 11 12
        Iterator<Double> it = quantiles.iterator();
        assertTrue(it.next().equals(1.0));
        Double d = it.next();
        assertTrue(d > 4.0 && d <=5.0);
        d = it.next();
        assertTrue(d > 8.0 && d <=9.0);

    }


}
