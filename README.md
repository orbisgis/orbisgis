OrbisGIS
=====

OrbisGIS is a GIS application dedicated to scientific spatial simulation.
This cross-platform GIS is developed at French IRSTV institute and is able to
manipulate and create vector and raster spatial information. 

For general information, please consult http://www.orbisgis.org/.

For developer-related information, have a look at [OrbisGIS's GitHub Wiki](https://github.com/irstv/orbisgis/wiki).

Licenses
-----

OrbisGIS in under the GPLv3 license. See [Licenses/license-GPL.txt](https://github.com/irstv/orbisgis/blob/master/Licenses/license-GPL.txt) for more information.


Quick Build instruction
------

OrbisGIS uses the Maven build system. 

 * To launch a full build (including tests):

```
mvn clean install
```
 * To run OrbisGIS from Maven:

```
cd orbisgis-dist\
mvn exec:exec
```
