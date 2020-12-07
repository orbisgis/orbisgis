/*
 * Bundle datastore/utils is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OSM is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OSM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OSM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * OSM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.datastore.jdbcutils

import org.geotools.data.DataStoreFinder
import org.geotools.jdbc.JDBCDataStore
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.sql.ResultSet

/**
 * Test class dedicated to {@link org.orbisgis.datastore.jdbcutils.JDBCDataStoreUtils}.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class JDBCDataStoreUtilsTest {

    private static JDBCDataStore ds

    @BeforeAll
    static void beforeAll() {
        def dataStore = DataStoreFinder.getDataStore([dbtype: "h2gis", database: "./target/database_${UUID.randomUUID()}"])
        assert dataStore in JDBCDataStore
        ds = (JDBCDataStore) dataStore
        ds.connection.execute("""
            CREATE TABLE elements (
                id int,
                name varchar(255),
                number int
            );
            INSERT INTO elements (id, name, number) VALUES (1, 'Simple Name', 2846);
            INSERT INTO elements (id, name, number) VALUES (2, 'Maybe a complex Name', 7455);
            INSERT INTO elements (id, name, number) VALUES (3, 'S N', 9272);
        """)
    }

    @Test
    void queryTest() {
        def str = "";
        ds.query("SELECT * FROM elements WHERE id > 1")
                {while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        ds.query("SELECT * FROM elements WHERE id > ?", [1])
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        ds.query("SELECT * FROM elements WHERE id > :id", [id:1])
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        ds.query([id:1], "SELECT * FROM elements WHERE id > :id")
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        def id = 1
        ds.query("SELECT * FROM elements WHERE id > $id")
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str
    }

    @Test
    void eachRowTest() {
        def str = "";
        ds.eachRow("SELECT * FROM elements WHERE id > 1")
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        ds.eachRow("SELECT * FROM elements", 2, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > 1")
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements" ,
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 2, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > ?" , [1],
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > :id" , [id:1],
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow([id:1], "SELECT * FROM elements WHERE id > :id" ,
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > ?" , [1])
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > :id" , [id:1])
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        ds.eachRow([id:1], "SELECT * FROM elements WHERE id > :id" )
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > ?" , [1])
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > :id" , [id:1])
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        ds.eachRow([id:1], "SELECT * FROM elements WHERE id > :id" )
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > ?" , [1], 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > :id" , [id:1], 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow([id:1], "SELECT * FROM elements WHERE id > :id" , 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > ${1}")
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        ds.eachRow("SELECT * FROM elements WHERE id > ${1}", 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > ${1}")
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        ds.eachRow("SELECT * FROM elements WHERE id > ${1}" ,
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";
    }

    @Test
    void rowsTest() {
        def rows = ds.rows("SELECT * FROM elements")
        assert 3 == rows.size()
        assert "{ID=1, NAME=Simple Name, NUMBER=2846}" == rows[0].toString()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[1].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[2].toString()

        rows = ds.rows("SELECT * FROM elements", 2, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        def str = ""
        rows = ds.rows("SELECT * FROM elements")
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 3 == rows.size()
        assert "{ID=1, NAME=Simple Name, NUMBER=2846}" == rows[0].toString()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[1].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[2].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = ds.rows("SELECT * FROM elements", 2, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str

        rows = ds.rows("SELECT * FROM elements WHERE ID > ?", [1])
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()

        rows = ds.rows([id:1],"SELECT * FROM elements WHERE ID > :id")
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()

        rows = ds.rows("SELECT * FROM elements WHERE ID > ?", [1], 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        rows = ds.rows("SELECT * FROM elements WHERE ID > :id", [id:1], 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        rows = ds.rows([id:1], "SELECT * FROM elements WHERE ID > :id", 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        rows = ds.rows("SELECT * FROM elements WHERE ID > ?", [1] as Object[])
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()

        rows = ds.rows("SELECT * FROM elements WHERE ID > ?", [1] as Object[], 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        str = ""
        rows = ds.rows("SELECT * FROM elements WHERE ID > ?", [1])
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = ds.rows("SELECT * FROM elements WHERE ID > :id", [id:1])
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = ds.rows([id:1],"SELECT * FROM elements WHERE ID > :id")
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = ds.rows("SELECT * FROM elements WHERE ID > ?", [1], 1, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = ds.rows("SELECT * FROM elements WHERE ID > :id", [id:1], 1, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = ds.rows([id:1],"SELECT * FROM elements WHERE ID > :id", 1, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str

        rows = ds.rows("SELECT * FROM elements WHERE ID > ${1}")
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()

        rows = ds.rows("SELECT * FROM elements WHERE ID > ${1}", 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        str = ""
        rows = ds.rows("SELECT * FROM elements WHERE ID > ${1}")
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = ds.rows("SELECT * FROM elements WHERE ID > ${1}", 1, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str
    }

    @Test
    void firstRowTest(){
        def row = ds.firstRow("SELECT * FROM elements")
        assert row.containsKey("ID")
        assert 1 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Simple Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 2846 == row.get("NUMBER")

        row = ds.firstRow("SELECT * FROM elements WHERE ID > ${1}")
        assert row.containsKey("ID")
        assert 2 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Maybe a complex Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 7455 == row.get("NUMBER")

        row = ds.firstRow("SELECT * FROM elements WHERE ID > ?", [1])
        assert row.containsKey("ID")
        assert 2 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Maybe a complex Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 7455 == row.get("NUMBER")

        row = ds.firstRow([id:1], "SELECT * FROM elements WHERE ID > :id")
        assert row.containsKey("ID")
        assert 2 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Maybe a complex Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 7455 == row.get("NUMBER")

        row = ds.firstRow("SELECT * FROM elements WHERE ID > :id", [id:1])
        assert row.containsKey("ID")
        assert 2 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Maybe a complex Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 7455 == row.get("NUMBER")
    }

    @Test
    void executeTest() {
        assert ds.execute("SELECT * FROM elements")

        ds.execute("SELECT * FROM elements")
                { isResultSet, result ->
                    assert isResultSet
                    assert 3 == result.size()
                }

        assert ds.execute("SELECT * FROM elements WHERE ID > ?", [1])

        ds.execute("SELECT * FROM elements WHERE ID > ?", [1])
                { isResultSet, result ->
                    assert isResultSet
                    assert 2 == result.size()
                }

        assert ds.execute([id:1], "SELECT * FROM elements WHERE ID > :id")

        ds.execute([id:1], "SELECT * FROM elements WHERE ID > :id")
                { isResultSet, result ->
                    assert isResultSet
                    assert 2 == result.size()
                }

        assert ds.execute("SELECT * FROM elements WHERE ID > ?", [1] as Object[])

        ds.execute("SELECT * FROM elements WHERE ID > ?", [1] as Object[])
                { isResultSet, result ->
                    assert isResultSet
                    assert 2 == result.size()
                }

        assert ds.execute("SELECT * FROM elements WHERE ID > ${1}")

        ds.execute("SELECT * FROM elements WHERE ID > ${1}")
                { isResultSet, result ->
                    assert isResultSet
                    assert 2 == result.size()
                }
    }

    @Test
    void executeInsertTest() {
        def drop = "DROP TABLE IF EXISTS insertions"
        def create = "CREATE TABLE insertions (\n" +
                "                id serial,\n" +
                "                name varchar(255),\n" +
                "                number int\n" +
                "            );"

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (id, name, number) VALUES (1, 'Simple Name', 2846);")
        def row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (id, name, number) VALUES (?, ?, ?);", [1, 'Simple Name', 2846])
        row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (name, number) VALUES (?, ?);", ['Simple Name', 2846],
                ["id"])
        row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (id, name, number) VALUES (?, ?, ?);",
                [1, 'Simple Name', 2846] as Object[])
        row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (name, number) VALUES ('Simple Name', 2846);", ["ID"] as String[])
        row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (name, number) VALUES ('Simple Name', 2846);", ["ID"] as String[])
        row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (name, number) VALUES (?, ?);", ["ID"] as String[],
                ['Simple Name', 2846] as Object[])
        row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (id, name, number) VALUES (${1}, ${'Simple Name'}, ${2846});")
        row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        ds.execute(drop)
        ds.execute(create)
        assert !ds.firstRow("SELECT * FROM insertions")
        ds.executeInsert("INSERT INTO insertions (name, number) VALUES (${'Simple Name'}, ${2846});", ["id"])
        row = ds.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")
    }
}
