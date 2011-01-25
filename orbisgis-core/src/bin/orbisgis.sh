#!/bin/sh
CP=""
HOMEDIR="/usr/local/OrbisGIS/"
PATHBASE="./lib/"
PATTERN="*jar"
SEARCH=${PATHBASE}${PATTERN}

#We move into the right folder
cd ${HOMEDIR}

#We fill the classpath CP
for i in `find . -path ${SEARCH}`
do
	CP=${CP}:${i}
done

#We add the orbisgis-core jar to the CP
CP=${CP}":./orbisgis-core-3.0.jar:"

#And we launch the software
java -Xmx512m -Xms64m -cp ".:${CP}" org.orbisgis.core.Main
