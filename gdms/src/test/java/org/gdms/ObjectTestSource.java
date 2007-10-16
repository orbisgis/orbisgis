/**
 * 
 */
package org.gdms;

import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.driver.ObjectDriver;

class ObjectTestSource extends TestSource {

	private ObjectDriver driver;

	public ObjectTestSource(String name, ObjectDriver driver) {
		super(name);
		this.driver = driver;
	}

	@Override
	public void backup() throws Exception {
		SourceTest.dsf.registerDataSource(name, new ObjectSourceDefinition(driver));
	}

}