package org.gdms.data.edition;

import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.ObjectReadWriteDriver;

public class ReadAndWriteDriver extends ReadDriver implements
		FileReadWriteDriver, DBReadWriteDriver, ObjectReadWriteDriver {
}