# This scripts requires at least jaxb 2.1. If this version is ever included
# in the jvm we can move it to an Ant script to execute inside eclipse. Meanwhile
# jaxb 2.1.7 can be downloaded here https://jaxb.dev.java.net/servlets/ProjectDocumentList

EXEC="/home/fergonco/applications/jaxb-ri/bin/xjc.sh";

${EXEC} -target 2.0 -d src/main/java -p org.orbisgis.renderer.symbol.collection.persistence -episode src/main/resources/org/orbisgis/renderer/symbol/symbol.episode src/main/resources/org/orbisgis/renderer/symbol/persistence.xsd
${EXEC} -target 2.0 -extension -d src/main/java -p org.orbisgis.renderer.legend.carto.persistence -episode src/main/resources/org/orbisgis/renderer/legend/legend.episode -b src/main/resources/org/orbisgis/renderer/symbol/symbol.episode src/main/resources/org/orbisgis/renderer/legend/persistence.xsd
${EXEC} -target 2.0 -extension -d src/main/java -p org.orbisgis.layerModel.persistence -b src/main/resources/org/orbisgis/renderer/symbol/symbol.episode -b src/main/resources/org/orbisgis/renderer/legend/legend.episode src/main/resources/org/orbisgis/layerModel/persistence.xsd
