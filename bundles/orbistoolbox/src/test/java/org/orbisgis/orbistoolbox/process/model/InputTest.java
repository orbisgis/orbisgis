/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.process.model;

import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.orbistoolbox.process.model.DataDescription;
import org.orbisgis.orbistoolbox.process.model.Format;
import org.orbisgis.orbistoolbox.process.model.Input;
import org.orbisgis.orbistoolbox.process.model.RawData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the Input class.
 *
 * @author Sylvain PALOMINOS
 */

public class InputTest {

    /**
     * Tests if the constructor with a null title returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullTitleConstructorTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        new Input(null, new URI("test"), new RawData(f));
    }

    /**
     * Tests if the constructor with a null identifier returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullURIConstructorTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        new Input("test", null, new RawData(f));
    }

    /**
     * Tests if the constructor with a null dataDescription returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullDataDescriptionConstructorTest() throws URISyntaxException {
        new Input("test", new URI("test"), (List<Input>)null);
    }

    /**
     * Tests if the constructor with a null input list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullInputConstructorTest() throws URISyntaxException {
        new Input("test", new URI("test"), (DataDescription)null);
    }

    /**
     * Tests if the constructor with an empty input list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void emptyInputConstructorTest() throws URISyntaxException {
        List<Input> emptyList = new ArrayList<>();
        new Input("test", new URI("test"), emptyList);
    }

    /**
     * Tests if the constructor with a input list containing a null input returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void containingNullInputConstructorTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        Input input = new Input("test", new URI("test"), new RawData(f));
        List<Input> nullList = new ArrayList<>();
        nullList.add(input);
        nullList.add(null);
        new Input("test", new URI("test"), nullList);
    }

    /**
     * Tests if setting the input list sets the data description to null.
     */
    @Test()
    public final void setInputTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        Input input1 = new Input("test", new URI("test"), new RawData(f));
        Input input2 = new Input("test", new URI("test"), new RawData(f));

        List<Input> list = new ArrayList<>();
        list.add(input2);

        input1.setInput(list);

        Assert.assertEquals("The data description should be null.", null, input1.getDataDescription());
        Assert.assertEquals("The input list is not the same as the one given.", list, input1.getInput());
    }

    /**
     * Tests if setting the data description sets the input list to null.
     */
    @Test()
    public final void setDataDescriptionTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        Input input = new Input("test", new URI("test"), new RawData(f));

        DataDescription dataDescription = new RawData(f);
        input.setDataDescription(dataDescription);

        Assert.assertEquals("The data description should be null.", null, input.getInput());
        Assert.assertEquals("The input list is not the same as the one given.", dataDescription, input.getDataDescription());
    }

    /**
     * Tests if setting false occurrence value do not break the Input.
     */
    @Test()
    public final void setOccurrenceTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        Input input = new Input("test", new URI("test"), new RawData(f));

        input.setMaxOccurs(5);
        input.setMinOccurs(0);

        input.setMinOccurs(6);
        Assert.assertFalse("minOccurs can not be higher than maxOccurs", input.getMaxOccurs() < input.getMinOccurs());

        input.setMinOccurs(3);
        input.setMaxOccurs(1);
        Assert.assertFalse("minOccurs can not be higher than maxOccurs", input.getMaxOccurs() < input.getMinOccurs());

        input.setMinOccurs(-2);
        Assert.assertFalse("minOccurs can not be negative", input.getMinOccurs() < 0);

    }
}
