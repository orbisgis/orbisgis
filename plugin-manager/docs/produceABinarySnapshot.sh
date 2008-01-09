#! /bin/bash
# ======================================================================
# Thomas LEDUC - le 09/01/2008
# ======================================================================
PLUGINS_LIST="org.orbisgis.core org.orbisgis.geocatalog org.orbisgis.geoview org.urbsat";

# ======================================================================
BASE_DIRECTORY="/tmp";
# BASE_DIRECTORY="/import/tmp-3jours";
# DST_SVN_DIRECTORY="${BASE_DIRECTORY}/orbisgis-${$}";
DST_SVN_DIRECTORY="${BASE_DIRECTORY}/orbisgis-svn";
DATE=`date +%Y%m%d-%H%M`;
# RELEASE_DIRECTORY="${BASE_DIRECTORY}/orbisgis-${DATE}";
RELEASE_DIRECTORY="${BASE_DIRECTORY}/orbisgis-zip";

MAIN_CLASS="org.orbisgis.pluginManager.Main";

RSYNC="rsync --archive --verbose --exclude=.svn";
MVN="mvn -Dmaven.test.skip=true";
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
		# svn checkout http://geosysin.iict.ch/irstv-svn/platform-stable platform;
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
	# ${MVN} package;
	${MVN} install;
	${MVN} dependency:copy-dependencies;
}

createPluginListXml() {
	rm -fr "${RELEASE_DIRECTORY}";
	mkdir -p "${RELEASE_DIRECTORY}";
	echo "<plugins>" > "${RELEASE_DIRECTORY}/plugin-list.xml";
	for plugin in ${PLUGINS_LIST}; do
		echo "  <plugin dir=\"plugins/${plugin}\"/>" >> "${RELEASE_DIRECTORY}/plugin-list.xml";
	done
	echo "  <plugin dir=\"lib\"/>" >> "${RELEASE_DIRECTORY}/plugin-list.xml";
	echo "</plugins>" >> "${RELEASE_DIRECTORY}/plugin-list.xml";
}

createUniqFileName() {
	dstDir="${1}";
	fileName="${2}";
	
	if [ -e "${dstDir}/${fileName}" ]; then
		while [ -e "${dstDir}/${fileName}" ]; do
			fileName="_${fileName}";
		done
	fi
	echo "${dstDir}/${fileName}";
}

copyAllJarFiles() {
	mkdir -p "${RELEASE_DIRECTORY}/lib";
	cd "${RELEASE_DIRECTORY}"

	for jar in $(find ${DST_SVN_DIRECTORY}/platform/dependencies -name \*.jar); do
		UNIQ_JAR_NAME=$(createUniqFileName lib $(basename ${jar}));
		cp --archive ${jar} ${UNIQ_JAR_NAME};
	done
}

copyDependenciesAndPluginXmlAndSchema() {
	cd ${DST_SVN_DIRECTORY}/platform;
	for plugin in ${PLUGINS_LIST}; do
		mkdir -p ${RELEASE_DIRECTORY}/plugins/${plugin};
		${RSYNC} ${plugin}/plugin.xml ${RELEASE_DIRECTORY}/plugins/${plugin};
		${RSYNC} ${plugin}/schema ${RELEASE_DIRECTORY}/plugins/${plugin};
	done
}

produceBatAndShellFiles() {
	cd ${RELEASE_DIRECTORY};
	CLASSPATH="lib/$(find ${DST_SVN_DIRECTORY}/platform/dependencies/org/orbisgis/plugin-manager -name \*.jar -printf '%f')";

	cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.sh;
#! /bin/sh
LIB=lib;
# PATH="/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Commands/java:\${PATH}";
MEMORY=512M;
java -Xmx\${MEMORY} -cp ${CLASSPATH} ${MAIN_CLASS} \${@}
EOF
	chmod +x ${RELEASE_DIRECTORY}/orbisgis.sh;

cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.bat;
set LIB=lib
set MEMORY=512M
start javaw -Xmx%MEMORY% -cp ${CLASSPATH} ${MAIN_CLASS} %1
EOF
	unix2dos --quiet ${RELEASE_DIRECTORY}/orbisgis.bat;
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
copyAllJarFiles;
copyDependenciesAndPluginXmlAndSchema;
produceBatAndShellFiles;
makeZip;

cat <<EOF

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * TAKE CARE TO THE dummy/pom.xml FILE !!!
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF
