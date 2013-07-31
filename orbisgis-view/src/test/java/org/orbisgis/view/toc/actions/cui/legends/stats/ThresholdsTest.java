package org.orbisgis.view.toc.actions.cui.legends.stats;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexis Guéganno
 */
public class ThresholdsTest {


    private Double[] sample = new Double[]{1.0,2.0,3.,4.,5.,6.,7.,8.,9.,10.,11.,12.};

    @Test
    public void testQuantiles() throws Exception {
        DescriptiveStatistics ds = new DescriptiveStatistics();
        Double[] vals = Arrays.copyOf(sample, sample.length);
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

    @Test
    public void testBoxedMeans() throws Exception{
        DescriptiveStatistics ds = new DescriptiveStatistics();
        Double[] vals = Arrays.copyOf(sample, sample.length);
        for(Double d : vals){
            ds.addValue(d);
        }
        Thresholds th = new Thresholds(ds,"name");
        SortedSet<Double> boxedMeans = th.getBoxedMeans(8);
        // Thresholds:
        // 1.0 2.0 3.5 5.0 6.5 8.0 9.5 11 12.0
        //
        //
        Iterator<Double> it = boxedMeans.iterator();
        assertTrue(it.next().equals(1.0));
        assertTrue(it.next().equals(2.0));
        assertTrue(it.next().equals(3.5));
        assertTrue(it.next().equals(5.0));
        assertTrue(it.next().equals(6.5));
        assertTrue(it.next().equals(8.0));
        assertTrue(it.next().equals(9.5));
        assertTrue(it.next().equals(11.0));
    }

    @Test
    public void testBoxedMeansIntervalNumber() throws Exception {
        DescriptiveStatistics ds = new DescriptiveStatistics();
        Double[] vals = Arrays.copyOf(sample, sample.length);
        for(Double d : vals){
            ds.addValue(d);
        }
        Thresholds th = new Thresholds(ds,"name");
        SortedSet<Double> boxedMeans = th.getBoxedMeans(9);
        assertTrue(boxedMeans.size()==8);

    }

}
