EXEC="/home/gonzales/applications/jaxb-ri/bin/xjc.sh";

${EXEC} -target 2.0 -d src/main/java -p org.orbisgis.renderer.symbol.collection.persistence -episode target/symbol.episode src/main/resources/org/orbisgis/renderer/symbol/persistence.xsd
${EXEC} -target 2.0 -extension -d src/main/java -p org.orbisgis.renderer.legend.carto.persistence -b target/symbol.episode src/main/resources/org/orbisgis/renderer/legend/persistence.xsd