# This scripts requires at least jaxb 2.1. If this version is ever included
# in the jvm we can move it to an Ant script to execute inside eclipse. Meanwhile
# jaxb 2.1.x can be downloaded here https://jaxb.dev.java.net/2.1.3/

EXEC="/home/gonzales/applications/jaxb-ri/bin/xjc.sh";

${EXEC} -target 2.0 -d src/main/java -p org.orbisgis.renderer.symbol.collection.persistence -episode target/symbol.episode src/main/resources/org/orbisgis/renderer/symbol/persistence.xsd
${EXEC} -target 2.0 -extension -d src/main/java -p org.orbisgis.renderer.legend.carto.persistence -b target/symbol.episode src/main/resources/org/orbisgis/renderer/legend/persistence.xsd