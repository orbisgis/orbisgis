package org.gdms.manual;

import java.io.ByteArrayInputStream;

import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;

public class TestExpression {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		
		String sql = "select * from table1 where gid =2;";
		SQLEngine eng = new SQLEngine(new ByteArrayInputStream(sql.getBytes()));
		eng.SQLStatement();
		
		

	}

}
