/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.util.Locale;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;

/**
 *
 * @author alexis
 */
public class LocalizedTextComparatorTest  extends AbstractTest {

    private LocalizedText lt1;
    private LocalizedText lt2;
    private LocalizedText lt3;
    private LocalizedText lt4;
    private LocalizedText lt5;
    private LocalizedText lt6;
    
    @Before
    @Override
    public void setUp(){
        lt1 = new LocalizedText("youhou", new Locale("en"));
        lt2 = new LocalizedText("youhou", new Locale("fr"));
        lt3 = new LocalizedText("hello", new Locale("en"));
        lt4 = new LocalizedText("hello", new Locale("en"));
        lt5 = new LocalizedText("youhou", null);
    }

    @Test
    public void testLocaleAndTextComparator(){
        LocaleAndTextComparator lc = new LocaleAndTextComparator();
        assertTrue(lc.compare(lt1, lt2)==-1);
        assertTrue(lc.compare(lt1, lt3)== 1);
        assertTrue(lc.compare(lt2, lt1)== 1);
        assertTrue(lc.compare(lt2, lt3)== 1);
        assertTrue(lc.compare(lt5, lt3)==-1);
        assertTrue(lc.compare(lt5, lt1)==-1);
        assertTrue(lc.compare(lt1, lt5)== 1);
        assertTrue(lc.compare(lt2, lt5)== 1);
        assertTrue(lc.compare(lt3, lt4)== 0);
        assertTrue(lc.compare(lt3, lt1)==-1);
    }

}
