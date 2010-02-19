#! /bin/bash
# ======================================================================
# Thomas LEDUC - le 09/01/2008
# ======================================================================
PLUGIN_LIST="org.orbisgis.core-ui org.orbisgis.games org.orbisgis.processing org.urbsat";

# ======================================================================
if [ "$#" -ne "2" ] && [ "$#" -ne "1" ]; then
  echo "Usage: releaseVersion.sh http://geosysin.iict.ch/irstv-svn/platform [ official ]";
  exit 1;
fi

LZPACK_HOME="/home/bocher/applications/izpack";
BASE_DIRECTORY="/tmp";
DST_INSTALLER_DIRECTORY="${BASE_DIRECTORY}/orbisgis-installers";
DST_SVN_DIRECTORY="${BASE_DIRECTORY}/orbisgis-svn";
DATE=`date +%Y%m%d-%H%M`;
RELEASE_DIRECTORY="${BASE_DIRECTORY}/orbisgis-zip";
URL="$1";
INSTALLER_URL="http://geosysin.iict.ch/irstv-svn/platform-installers";

if [ "$2" == "official" ]; then
  OFFICIAL="official";
fi

if [ $OFFICIAL ]; then
echo "Please, create a change log (press intro when done)"
read foo
echo "Please, generate the OrbisGIS reference (run platform with -document) and commit on the svn (press intro when done)"
read foo
echo "Please, change the pom version numbers depending on the change log (press intro when done)"
read foo
echo "Please, change the OrbisGIS version number on the ApplicationInfo implementation in orbisgis-core (press intro when done)"
read foo
echo "It's done. Don't forget to publish change log and zip. A binary and a source package will be created (press intro to proceed)"
read foo
fi
echo "Please, enter the version number to appear in the zip file name"
read VERSION

MAIN_CLASS="org.orbisgis.pluginManager.Main";

RSYNC="rsync --archive --verbose --exclude=.svn";
MVN="mvn -Dmaven.test.skip=true";
# ======================================================================
checkLzPack() {
	if [ ! -d ${LZPACK_HOME} ]; then
		echo "Wrong lzPack home";
		exit 1;
	fi
}

svnCheckout() {
	rm -fr ${DST_SVN_DIRECTORY};
	mkdir -p ${DST_SVN_DIRECTORY};
	cd ${DST_SVN_DIRECTORY};
	svn checkout $URL platform;
}

createZipOfAllSrcAndJavadoc() {
	cd ${DST_SVN_DIRECTORY};
	rm -fr $(find . -type d -name .svn);

	cd ${DST_SVN_DIRECTORY}/platform;
	${MVN} install;
	${MVN} javadoc:javadoc;
	echo "Uploading javadoc (press intro)"
	read foo
	scp -r target/site/apidocs/* orbisgis.cerma.archi.fr:/home/web/orbisgis/javadoc/
	echo "Uploading reference (press intro)"
	read foo
	scp -r plugin-manager/docs/reference/* orbisgis.cerma.archi.fr:/home/web/orbisgis/orbisgis-reference
	cd ${DST_SVN_DIRECTORY}
	mv platform/target/site/apidocs apidocs;
	mv platform/plugin-manager/docs/reference reference;
	rm -fr $(find . -type d -name target);
	zip -r /tmp/orbisgis-${VERSION}-src.zip platform apidocs reference;
}

mvnPackage() {
	cd ${DST_SVN_DIRECTORY}/platform;
	if [ $OFFICIAL ]; then
      ${MVN} deploy;
    else
      ${MVN} install;
    fi
	${MVN} dependency:copy-dependencies;
}

createPluginListXml() {
	rm -fr "${RELEASE_DIRECTORY}";
	mkdir -p "${RELEASE_DIRECTORY}";
	echo "<plugins>" > "${RELEASE_DIRECTORY}/plugin-list.xml";
	for plugin in ${PLUGIN_LIST}; do
		echo "  <plugin dir=\"plugins/${plugin}\"/>" >> "${RELEASE_DIRECTORY}/plugin-list.xml";
	done
	echo "</plugins>" >> "${RELEASE_DIRECTORY}/plugin-list.xml";
}

copyAllJarFiles() {
	mkdir -p "${RELEASE_DIRECTORY}/lib";
	cd "${RELEASE_DIRECTORY}"

	for jar in $(find ${DST_SVN_DIRECTORY}/platform/dependencies -name \*.jar); do
		cp --archive ${jar} ${RELEASE_DIRECTORY}/lib/$(basename ${jar});
	done
}

copyDependenciesAndPluginXmlAndSchema() {
	cd ${DST_SVN_DIRECTORY}/platform;
	for plugin in ${PLUGIN_LIST}; do
		mkdir -p ${RELEASE_DIRECTORY}/plugins/${plugin};
		${RSYNC} ${plugin}/plugin.xml ${RELEASE_DIRECTORY}/plugins/${plugin};
		${RSYNC} ${plugin}/schema ${RELEASE_DIRECTORY}/plugins/${plugin};
	done
}

produceBatAndShellFiles() {
	cd ${RELEASE_DIRECTORY};

	for jar in $(find ${DST_SVN_DIRECTORY}/platform/plugin-manager -name \*.jar -printf "%f\n"); do
		UNX="lib/${jar}:${UNX}";
		WIN="lib\\${jar};${WIN}";
	done

# standalone linux package
	cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.sh;
java -Xmx512M -cp "${UNX}" ${MAIN_CLASS} \${@}
EOF
	chmod +x ${RELEASE_DIRECTORY}/orbisgis.sh;

# with jdk
	cat <<EOF > ${BASE_DIRECTORY}/orbisgis.sh;
../jdk/bin/java -Xmx512M -cp "${UNX}" ${MAIN_CLASS} \${@}
EOF

# standalone windows package
cat <<EOF > ${RELEASE_DIRECTORY}/orbisgis.bat;
start javaw -Xmx512M -cp "${WIN}" ${MAIN_CLASS} %1
EOF
	unix2dos ${RELEASE_DIRECTORY}/orbisgis.bat;

# with jdk
cat <<EOF > ${BASE_DIRECTORY}/orbisgis.bat;
start ..\jdk\bin\javaw -Xmx512M -cp "${WIN}" ${MAIN_CLASS} %1
EOF
	unix2dos ${BASE_DIRECTORY}/orbisgis.bat;
}

makeZip() {
	cp ${DST_SVN_DIRECTORY}/platform/plugin-manager/docs/license.txt ${RELEASE_DIRECTORY};
	cd $(dirname ${RELEASE_DIRECTORY}) && zip -r orbisgis-${VERSION}.zip $(basename ${RELEASE_DIRECTORY});
}

createInstallers() {
	rm -fr ${DST_INSTALLER_DIRECTORY};
	svn checkout $INSTALLER_URL ${DST_INSTALLER_DIRECTORY};
	cd ${DST_INSTALLER_DIRECTORY}/linux;
	tar -xjvf jdk.bz2;
	cd ${DST_INSTALLER_DIRECTORY}/windows;
	tar -xjvf jdk.bz2;
	cd ${DST_INSTALLER_DIRECTORY};
	cp -R ${RELEASE_DIRECTORY} bin
	cp ${BASE_DIRECTORY}/orbisgis.sh bin
	cp ${BASE_DIRECTORY}/orbisgis.bat bin
	${LZPACK_HOME}/bin/compile install-linux.xml -b ${DST_INSTALLER_DIRECTORY} -o orbisgis-linux-installer-${VERSION}.jar -h ${LZPACK_HOME}
	${LZPACK_HOME}/bin/compile install-windows.xml -b ${DST_INSTALLER_DIRECTORY} -o orbisgis-windows-installer-${VERSION}.jar -h ${LZPACK_HOME}}
}

# ======================================================================
if [ ${#} -eq 1 ]; then
	DATE_OF_RELEASE=${1};
else
	DATE_OF_RELEASE=$(date +%Y%m%d);
fi

checkLzPack;
svnCheckout ${DATE_OF_RELEASE};
if [ $OFFICIAL ]; then
 createZipOfAllSrcAndJavadoc;
fi
mvnPackage;
createPluginListXml;
copyAllJarFiles;
copyDependenciesAndPluginXmlAndSchema;
produceBatAndShellFiles;
makeZip;

createInstallers;
