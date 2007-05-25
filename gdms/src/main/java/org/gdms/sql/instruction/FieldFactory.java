package org.gdms.sql.instruction;

import org.gdms.data.InternalDataSource;
import org.gdms.driver.DriverException;



/**
 * Clase que se encarga de crear los objetos Field de las instrucciones Select
 * a  partir del nombre del campo
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class FieldFactory {
	/**
	 * Dada una lista de tablas y el nombre de un campo, devuelve el objeto
	 * Field conteniendo la informaci�n del �ndice de la tabla a la que
	 * pertenece el campo y el �ndice del campo dentro de dicha tabla
	 *
	 * @param tables Array de tablas donde se buscar� el campo
	 * @param fieldName Nombre del campo que se est� buscando
	 * @param source Fuente de datos para el campo que se crea. El campo
	 * 		  obtendr� sus valores de dicha fuente.
	 *
	 * @return Objeto Field
	 *
	 * @throws AmbiguousFieldNameException Si hay dos tablas que pueden tener
	 * 		   el campo
	 * @throws DriverException Si se produce un error accediendo a los campos
	 * @throws FieldNotFoundException Si el campo no se encuentra en ninguna de
	 * 		   las tablas
	 */
	public static Field createField(InternalDataSource[] tables, String fieldName,
		InternalDataSource source)
		throws AmbiguousFieldNameException, DriverException, 
			FieldNotFoundException {
		if (fieldName.indexOf(".") != -1) {
			return createWithTable(tables, fieldName, source);
		} else {
			return createWithoutTable(tables, fieldName, source);
		}
	}

	/**
	 * Crea un campo que viene especificado por el nombre de campo sin el
	 * nombre de la tabla a la que pertenece
	 *
	 * @param tables Array de tablas donde se buscar� el campo
	 * @param fieldName Nombre del campo que se est� buscando
	 * @param source Fuente de datos para el campo que se crea
	 *
	 * @return Objeto Field
	 *
	 * @throws FieldNotFoundException Si el campo no se encuentra en ninguna de
	 * 		   las tablas
	 * @throws AmbiguousFieldNameException Si hay dos tablas que pueden tener
	 * 		   el campo
	 * @throws DriverException Si se produce un error accediendo a los campos
	 */
	private static Field createWithoutTable(InternalDataSource[] tables,
		String fieldName, InternalDataSource source)
		throws FieldNotFoundException, AmbiguousFieldNameException, 
			DriverException {
		int retIndex = -1;
		int dataSource = -1;

		for (int i = 0; i < tables.length; i++) {
			int index = tables[i].getFieldIndexByName(fieldName);

			if (index != -1) {
				//Si ya se hab�a encontrado uno
				if (retIndex != -1) {
					throw new AmbiguousFieldNameException(fieldName);
				} else {
					retIndex = index;
					dataSource = i;
				}
			}
		}

		if (retIndex == -1) {
			throw new FieldNotFoundException(fieldName);
		}

		Field ret = new Field();
		ret.setDataSourceIndex(dataSource);
		ret.setFieldId(retIndex);
		ret.setTables(tables);
		ret.setDataSource(source);

		return ret;
	}

	/**
	 * Crea un campo que viene especificado por el nombre de la tabla seguido
	 * de "." y del nombre del campo de dicha tabla
	 *
	 * @param tables Array de tablas donde se buscar� el campo
	 * @param fieldName Nombre del campo que se est� buscando
	 * @param source Fuente de datos para el campo que se crea
	 *
	 * @return Objeto Field
	 *
	 * @throws FieldNotFoundException Si el campo no se encuentra en ninguna de
	 * 		   las tablas
	 * @throws AmbiguousFieldNameException Si hay dos tablas que pueden tener
	 * 		   el campo
	 * @throws DriverException Si se produce un error accediendo a los campos
	 */
	private static Field createWithTable(InternalDataSource[] tables, String fieldName,
		InternalDataSource source)
		throws FieldNotFoundException, AmbiguousFieldNameException, 
			DriverException {
		int retIndex = -1;
		int dataSource = -1;

		//Se obtiene el nombre de la tabla y del campo
		String[] nombres = fieldName.split("[.]");
		String tableName = nombres[0].trim();
		fieldName = nombres[1].trim();

		for (int i = 0; i < tables.length; i++) {
			if (tables[i].getName().equals(tableName)) {
				retIndex = tables[i].getFieldIndexByName(fieldName);
				dataSource = i;

				break;
			}
		}

		if (retIndex == -1) {
			throw new FieldNotFoundException(fieldName);
		}

		Field ret = new Field();
		ret.setDataSourceIndex(dataSource);
		ret.setFieldId(retIndex);
		ret.setTables(tables);
		ret.setDataSource(source);

		return ret;
	}
}
