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

import java.sql.CallableStatement
import java.sql.ResultSet
import java.sql.Types

import static org.orbisgis.datastore.jdbcutils.JDBCDataStoreUtils.out
/**
 * Test class dedicated to {@link org.orbisgis.datastore.jdbcutils.JDBCDataStoreUtils}.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class JDBCDataStoreUtilsTest {

    private static JDBCDataStore h2gis
    private static JDBCDataStore postgis

    @BeforeAll
    static void beforeAll() {
        def dataStore = DataStoreFinder.getDataStore([dbtype: "h2gis", database: "./target/database_${UUID.randomUUID()}"])
        assert dataStore in JDBCDataStore
        h2gis = (JDBCDataStore) dataStore
        h2gis.connection.execute("""
            CREATE TABLE elements (
                id int,
                name varchar(255),
                number int
            );
            INSERT INTO elements (id, name, number) VALUES (1, 'Simple Name', 2846);
            INSERT INTO elements (id, name, number) VALUES (2, 'Maybe a complex Name', 7455);
            INSERT INTO elements (id, name, number) VALUES (3, 'S N', 9272);
        """)

        dataStore = DataStoreFinder.getDataStore([dbtype: "postgis", host: "localhost", user: "orbisgis",
                                                  passwd: "orbisgis", database: "orbisgis_db", port: 5432])
        assert dataStore in JDBCDataStore
        postgis = (JDBCDataStore) dataStore
    }

    @Test
    void queryTest() {
        def str = "";
        h2gis.query("SELECT * FROM elements WHERE id > 1")
                {while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        h2gis.query("SELECT * FROM elements WHERE id > ?", [1])
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        h2gis.query("SELECT * FROM elements WHERE id > :id", [id:1])
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        h2gis.query([id:1], "SELECT * FROM elements WHERE id > :id")
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        def id = 1
        h2gis.query("SELECT * FROM elements WHERE id > $id")
                { while(it.next()) {
                    str+=it.name+" "+it.number+" "
                }}
        assert "Maybe a complex Name 7455 S N 9272 " == str
    }

    @Test
    void eachRowTest() {
        def str = "";
        h2gis.eachRow("SELECT * FROM elements WHERE id > 1")
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        h2gis.eachRow("SELECT * FROM elements", 2, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > 1")
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements" ,
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 2, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > ?" , [1],
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > :id" , [id:1],
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow([id:1], "SELECT * FROM elements WHERE id > :id" ,
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > ?" , [1])
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > :id" , [id:1])
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        h2gis.eachRow([id:1], "SELECT * FROM elements WHERE id > :id" )
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > ?" , [1])
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > :id" , [id:1])
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        h2gis.eachRow([id:1], "SELECT * FROM elements WHERE id > :id" )
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > ?" , [1], 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > :id" , [id:1], 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow([id:1], "SELECT * FROM elements WHERE id > :id" , 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > ${1}")
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 S N 9272 " == str

        str = "";
        h2gis.eachRow("SELECT * FROM elements WHERE id > ${1}", 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "Maybe a complex Name 7455 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > ${1}")
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 S N 9272 " == str
        str = "";

        h2gis.eachRow("SELECT * FROM elements WHERE id > ${1}" ,
                { str+=it.getColumnName(2) + " " + it.getColumnName(3) + " "}, 1, 1)
                { str+=it.name+" "+it.number+" " }
        assert "NAME NUMBER Maybe a complex Name 7455 " == str
        str = "";
    }

    @Test
    void rowsTest() {
        def rows = h2gis.rows("SELECT * FROM elements")
        assert 3 == rows.size()
        assert "{ID=1, NAME=Simple Name, NUMBER=2846}" == rows[0].toString()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[1].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[2].toString()

        rows = h2gis.rows("SELECT * FROM elements", 2, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        def str = ""
        rows = h2gis.rows("SELECT * FROM elements")
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 3 == rows.size()
        assert "{ID=1, NAME=Simple Name, NUMBER=2846}" == rows[0].toString()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[1].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[2].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = h2gis.rows("SELECT * FROM elements", 2, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str

        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ?", [1])
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()

        rows = h2gis.rows([id:1],"SELECT * FROM elements WHERE ID > :id")
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()

        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ?", [1], 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        rows = h2gis.rows("SELECT * FROM elements WHERE ID > :id", [id:1], 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        rows = h2gis.rows([id:1], "SELECT * FROM elements WHERE ID > :id", 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ?", [1] as Object[])
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()

        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ?", [1] as Object[], 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        str = ""
        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ?", [1])
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = h2gis.rows("SELECT * FROM elements WHERE ID > :id", [id:1])
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = h2gis.rows([id:1],"SELECT * FROM elements WHERE ID > :id")
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ?", [1], 1, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = h2gis.rows("SELECT * FROM elements WHERE ID > :id", [id:1], 1, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = h2gis.rows([id:1],"SELECT * FROM elements WHERE ID > :id", 1, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str

        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ${1}")
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()

        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ${1}", 1, 1)
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()

        str = ""
        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ${1}")
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 2 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "{ID=3, NAME=S N, NUMBER=9272}" == rows[1].toString()
        assert "ID NAME NUMBER" == str

        str = ""
        rows = h2gis.rows("SELECT * FROM elements WHERE ID > ${1}", 1, 1)
                { str+=it.getColumnName(1)+" "+it.getColumnName(2)+" "+it.getColumnName(3)}
        assert 1 == rows.size()
        assert "{ID=2, NAME=Maybe a complex Name, NUMBER=7455}" == rows[0].toString()
        assert "ID NAME NUMBER" == str
    }

    @Test
    void firstRowTest(){
        def row = h2gis.firstRow("SELECT * FROM elements")
        assert row.containsKey("ID")
        assert 1 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Simple Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 2846 == row.get("NUMBER")

        row = h2gis.firstRow("SELECT * FROM elements WHERE ID > ${1}")
        assert row.containsKey("ID")
        assert 2 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Maybe a complex Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 7455 == row.get("NUMBER")

        row = h2gis.firstRow("SELECT * FROM elements WHERE ID > ?", [1])
        assert row.containsKey("ID")
        assert 2 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Maybe a complex Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 7455 == row.get("NUMBER")

        row = h2gis.firstRow([id:1], "SELECT * FROM elements WHERE ID > :id")
        assert row.containsKey("ID")
        assert 2 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Maybe a complex Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 7455 == row.get("NUMBER")

        row = h2gis.firstRow("SELECT * FROM elements WHERE ID > :id", [id:1])
        assert row.containsKey("ID")
        assert 2 == row.get("ID")
        assert row.containsKey("NAME")
        assert "Maybe a complex Name" == row.get("NAME")
        assert row.containsKey("NUMBER")
        assert 7455 == row.get("NUMBER")
    }

    @Test
    void executeTest() {
        assert h2gis.execute("SELECT * FROM elements")

        h2gis.execute("SELECT * FROM elements")
                { isResultSet, result ->
                    assert isResultSet
                    assert 3 == result.size()
                }

        assert h2gis.execute("SELECT * FROM elements WHERE ID > ?", [1])

        h2gis.execute("SELECT * FROM elements WHERE ID > ?", [1])
                { isResultSet, result ->
                    assert isResultSet
                    assert 2 == result.size()
                }

        assert h2gis.execute([id:1], "SELECT * FROM elements WHERE ID > :id")

        h2gis.execute([id:1], "SELECT * FROM elements WHERE ID > :id")
                { isResultSet, result ->
                    assert isResultSet
                    assert 2 == result.size()
                }

        assert h2gis.execute("SELECT * FROM elements WHERE ID > ?", [1] as Object[])

        h2gis.execute("SELECT * FROM elements WHERE ID > ?", [1] as Object[])
                { isResultSet, result ->
                    assert isResultSet
                    assert 2 == result.size()
                }

        assert h2gis.execute("SELECT * FROM elements WHERE ID > ${1}")

        h2gis.execute("SELECT * FROM elements WHERE ID > ${1}")
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

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (id, name, number) VALUES (1, 'Simple Name', 2846);")
        def row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (id, name, number) VALUES (?, ?, ?);", [1, 'Simple Name', 2846])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (name, number) VALUES (?, ?);", ['Simple Name', 2846],
                ["id"])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert([id: 1, name: 'Simple Name', number: 2846],
                "INSERT INTO insertions (id, name, number) VALUES (:id, :name, 2846);")
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert([name: 'Simple Name', number: 2846],
                "INSERT INTO insertions (name, number) VALUES (:name, :number);", ["id"])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (id, name, number) VALUES (?, ?, ?);",
                [1, 'Simple Name', 2846] as Object[])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (name, number) VALUES ('Simple Name', 2846);", ["ID"] as String[])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (name, number) VALUES ('Simple Name', 2846);", ["ID"] as String[])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (name, number) VALUES (?, ?);", ["ID"] as String[],
                ['Simple Name', 2846] as Object[])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (id, name, number) VALUES (${1}, ${'Simple Name'}, ${2846});")
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        assert !h2gis.firstRow("SELECT * FROM insertions")
        h2gis.executeInsert("INSERT INTO insertions (name, number) VALUES (${'Simple Name'}, ${2846});", ["id"])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")
    }

    @Test
    void executeUpdateTest() {
        def drop = "DROP TABLE IF EXISTS insertions"
        def create = "CREATE TABLE insertions (\n" +
                "                id serial,\n" +
                "                name varchar(255),\n" +
                "                number int\n" +
                "            );"
        def insert = "INSERT INTO insertions(id, name, number) VALUES (1, 'Name', 5432);"

        h2gis.execute(drop)
        h2gis.execute(create)
        h2gis.execute(insert)
        def row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Name' == row.get("NAME")
        assert 5432 == row.get("NUMBER")
        h2gis.executeUpdate("UPDATE insertions SET id = 2, name = 'Simple Name', number = 2846;")
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 2 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        h2gis.execute(insert)
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Name' == row.get("NAME")
        assert 5432 == row.get("NUMBER")
        h2gis.executeUpdate("UPDATE insertions SET id = ?, name = ?, number = ?;", [2, 'Simple Name', 2846])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 2 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        h2gis.execute(insert)
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Name' == row.get("NAME")
        assert 5432 == row.get("NUMBER")
        h2gis.executeUpdate("UPDATE insertions SET id = :id, name = :name, number = :number;",
                [id:2, name:'Simple Name', number:2846])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 2 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        h2gis.execute(insert)
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Name' == row.get("NAME")
        assert 5432 == row.get("NUMBER")
        h2gis.executeUpdate([id:2, name:'Simple Name', number:2846], "UPDATE insertions SET id = :id, name = :name, number = :number;")
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 2 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        h2gis.execute(insert)
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Name' == row.get("NAME")
        assert 5432 == row.get("NUMBER")
        h2gis.executeUpdate("UPDATE insertions SET id = ?, name = ?, number = ?;", [2, 'Simple Name', 2846])
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 2 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")

        h2gis.execute(drop)
        h2gis.execute(create)
        h2gis.execute(insert)
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 1 == row.get("ID")
        assert 'Name' == row.get("NAME")
        assert 5432 == row.get("NUMBER")
        h2gis.executeUpdate("UPDATE insertions SET id = ${2}, name = ${'Simple Name'}, number = ${2846};")
        row = h2gis.firstRow("SELECT * FROM insertions")
        assert 2 == row.get("ID")
        assert 'Simple Name' == row.get("NAME")
        assert 2846 == row.get("NUMBER")
    }

    @Test
    void callTest() {
        postgis.execute """
            CREATE OR REPLACE FUNCTION HouseSwap(_first1 VARCHAR(50), _first2 VARCHAR(50))
            RETURNS VARCHAR
            LANGUAGE plpgsql
            AS \$\$
            DECLARE _loc1 INT;
            DECLARE _loc2 INT;
            DECLARE _loc INT;
            BEGIN
                SELECT location_id into _loc1 FROM PERSON where firstname = _first1;
                SELECT location_id into _loc2 FROM PERSON where firstname = _first2;
                UPDATE PERSON
                set location_id = case firstname
                    when _first1 then _loc2
                    when _first2 then _loc1
                end
                where (firstname = _first1 OR firstname = _first2);
                SELECT location_id into _loc FROM PERSON where firstname = _first1;
                CASE _loc
                    WHEN _loc1 THEN
                        RETURN 'Nothing to do';
                    ELSE
                        RETURN 'Swap done';
                END CASE;
            END; \$\$
        """

        def drop = "DROP TABLE IF EXISTS PERSON"
        def create = "CREATE TABLE PERSON (\n" +
                "                location_id int,\n" +
                "                firstname varchar(255)\n" +
                "            );"
        def insert = "INSERT INTO PERSON(location_id, firstname) VALUES (1, 'Guillaume');" +
                "INSERT INTO PERSON(location_id, firstname) VALUES (2, 'Paul');"

        postgis.execute(drop)
        postgis.execute(create)
        postgis.execute(insert)
        def row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Guillaume' == row.get("firstname")
        postgis.call("{CALL HouseSwap('Guillaume', 'Paul')}")
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Paul' == row.get("firstname")

        postgis.execute(drop)
        postgis.execute(create)
        postgis.execute(insert)
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Guillaume' == row.get("firstname")
        postgis.call("{CALL HouseSwap(${"Guillaume"}, ${"Paul"})}")
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Paul' == row.get("firstname")

        postgis.execute(drop)
        postgis.execute(create)
        postgis.execute(insert)
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Guillaume' == row.get("firstname")
        postgis.call("{CALL HouseSwap(?, ?)}", ["Guillaume", "Paul"])
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Paul' == row.get("firstname")

        postgis.execute(drop)
        postgis.execute(create)
        postgis.execute(insert)
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Guillaume' == row.get("firstname")
        postgis.call("{CALL HouseSwap(?, ?)}", ["Guillaume", "Paul"] as Object[])
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Paul' == row.get("firstname")

        postgis.execute(drop)
        postgis.execute(create)
        postgis.execute(insert)
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Guillaume' == row.get("firstname")
        postgis.call("{? = CALL HouseSwap(?, ?)}", [out.VARCHAR, "Guillaume", "Paul"]) { assert "Swap done" == it }
        row = postgis.firstRow("SELECT * FROM PERSON WHERE location_id=1")
        assert 'Paul' == row.get("firstname")
    }
}