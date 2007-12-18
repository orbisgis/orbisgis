#! /bin/bash
# ======================================================================
# Thomas LEDUC - le 03/12/2007
# ======================================================================
PLUGINS_LIST="org.orbisgis.core org.orbisgis.geocatalog org.orbisgis.geocatalog.basicResources org.orbisgis.geoview org.orbisgis.geoview.basic org.urbsat";

# ======================================================================
DST_SVN_DIRECTORY="/tmp/orbisgis-${$}";
DST_SVN_DIRECTORY="/tmp/orbisgis-svn";
DATE=`date +%Y%m%d-%H%M`;
RELEASE_DIRECTORY="/tmp/orbisgis-${DATE}";
RELEASE_DIRECTORY="/tmp/orbisgis-zip";

MAIN_CLASS="org.orbisgis.pluginManager.Main";
# ======================================================================
svnCheckout() {
	if [ -d ${DST_SVN_DIRECTORY} ]; then
		cd ${DST_SVN_DIRECTORY};
		rm --force platform/pom.xml
		svn update platform;
	else
		mkdir -p ${DST_SVN_DIRECTORY};
		cd ${DST_SVN_DIRECTORY};
		svn checkout http://geosysin.iict.ch/irstv-svn/platform platform;
	fi
}

createDummyPlugin() {
	cd ${DST_SVN_DIRECTORY}/platform;
	mkdir -p dummy;
	cp plugin-manager/docs/deploy-pom.xml dummy/pom.xml;
}

modifyParentPomXml() {
	perl -pi -e 's#</modules>#\t<module>dummy</module>\n</modules>#' ${DST_SVN_DIRECTORY}/platform/pom.xml;
}

mvnPackage() {
	cd ${DST_SVN_DIRECTORY}/platform;
	# mvn -o -Dmaven.test.skip=true package;
	mvn -o -Dmaven.test.skip=true install;
	mvn -o -Dmaven.test.skip=true dependency:copy-dependencies;
}

createPluginListXml() {
	rm -fr ${RELEASE_DIRECTORY};
	mkdir -p ${RELEASE_DIRECTORY};
	echo "<plugins>" > ${RELEASE_DIRECTORY}/plugin-list.xml;
	for plugin in ${PLUGINS_LIST}; do
		echo "  <plugin dir=\"plugins/${plugin}\"/>" >> ${RELEASE_DIRECTORY}/plugin-list.xml;
	done
	echo "  <plugin dir=\"plugins/plugins-lib\"/>" >> ${RELEASE_DIRECTORY}/plugin-list.xml;
	echo "</plugins>" >> ${RELEASE_DIRECTORY}/plugin-list.xml;
}

createPluginManagerStartup() {
	mkdir -p ${RELEASE_DIRECTORY}/plugins/plugins-lib;
	echo "<plugin></plugin>" > ${RELEASE_DIRECTORY}/plugins/plugins-lib/plugin.xml;
	# copy the jars that are necessary to the plugins :
	rsync -av ${DST_SVN_DIRECTORY}/platform/dependencies/* ${RELEASE_DIRECTORY}/plugins/plugins-lib;

	# copy the jars that are necessary to the plugin-manager launcher :
	rsync -av ${DST_SVN_DIRECTORY}/platform/plugin-manager/dependencies/* ${RELEASE_DIRECTORY}/lib;

	# copy the jar that are necessary to the plugin-manager laucher :
	rsync -av plugin-manager/target/plugin-manager-*.jar ${RELEASE_DIRECTORY}/lib;
}

copyDependenciesAndPluginXmlAndSchema() {
	cd ${DST_SVN_DIRECTORY}/platform;
	for plugin in ${PLUGINS_LIST}; do
		mkdir -p ${RELEASE_DIRECTORY}/plugins/${plugin};
		rsync --archive --quiet ${plugin}/plugin.xml ${RELEASE_DIRECTORY}/plugins/${plugin};
		rsync --archive --quiet ${plugin}/schema ${RELEASE_DIRECTORY}/plugins/${plugin};
	done
}

produceBatAndShellFiles() {
	cd ${RELEASE_DIRECTORY};
	for jar in $(find lib -name \*.jar -print); do
		UNX="${jar}:${UNX}";
		WIN="${jar};${WIN}";
	done

	cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.sh;
#! /bin/sh
LIB=lib;
CLASSPATH="\${CLASSPATH}:${UNX}";
java -Xms256M -Xmx256M -cp \${CLASSPATH} ${MAIN_CLASS} \${@}
EOF
	chmod +x ${RELEASE_DIRECTORY}/orbisgis.sh;

cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.bat;
set LIB=lib
set CLASSPATH="%CLASSPATH%;${WIN}"
start javaw -Xms256M -Xmx256M -cp %CLASSPATH% ${MAIN_CLASS} %1
EOF
	unix2dos --quiet ${RELEASE_DIRECTORY}/orbisgis.bat;
}

removeRedundantJars() {
	# if some jar file already exists in lib, remove it from plugins-lib
	cd ${RELEASE_DIRECTORY}/lib;

	printf "%d jars files have been copied in plugins/plugins-lib/!\n" $(find ${RELEASE_DIRECTORY}/plugins/plugins-lib -name \*.jar -print | wc -l);

	for jar in $(find . -type f -name \*.jar -print); do
		[ -e ${RELEASE_DIRECTORY}/plugins/plugins-lib/${jar} ] && rm --force ${RELEASE_DIRECTORY}/plugins/plugins-lib/${jar};
	done

	printf "After removeRedundantJars(), %d jars files still remains !\n" $(find ${RELEASE_DIRECTORY}/plugins/plugins-lib -name \*.jar -print | wc -l);

	# remove .svn directories...
	find . -name .svn | xargs rm
}

makeZip() {
	cd $(dirname ${RELEASE_DIRECTORY}) && zip -r orbisgis-${DATE} $(basename ${RELEASE_DIRECTORY});
}
# ======================================================================
svnCheckout;
createDummyPlugin;
modifyParentPomXml;
mvnPackage;
createPluginListXml;
createPluginManagerStartup;
copyDependenciesAndPluginXmlAndSchema;
produceBatAndShellFiles;
removeRedundantJars;
makeZip;
