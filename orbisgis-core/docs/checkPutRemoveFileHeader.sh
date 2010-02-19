#! /bin/bash
# ======================================================================
# Thomas LEDUC - le 11/01/2008
# ======================================================================
usage() {
	cat <<EOF >&2
Usage: ${0} [ -check | -put | -remove ] <project path> <fileHeader>
  exemples :
	${0} -check ~/dev/eclipse-java/platform fileHeader_orbisgis.txt

	${0} -remove ~/dev/eclipse-java/platform fileHeader_orbisgis.txt

	${0} -put ~/dev/eclipse-java/platform fileHeader_orbisgis.txt
EOF
	exit 1;
}

checkFileHeaderSum() {
	NBLIGNES=${1};
	src=${2};
	echo $(head -${NBLIGNES} ${src} | md5sum | cut --fields=1 --delimiter=' ');
}
# ======================================================================
[ 3 -ne ${#} ] && usage;
([ "-check" = ${1} ] || [ "-put" = ${1} ] || [ "-remove" = ${1} ]) || usage;

CMD=${1};
PROJECT=${2};
FILEHEADER=${3};

NBLIGNES=$(wc -l ${FILEHEADER} | cut --fields=1 --delimiter=' ');
REFSUM=$(md5sum ${FILEHEADER} | cut --fields=1 --delimiter=' ');
let CPT=1;

find ${PROJECT} -name \*.java | while read src; do
	CHECK=$(checkFileHeaderSum ${NBLIGNES} ${src});

	if [ "-check" = ${CMD} ] && [ ${REFSUM} != ${CHECK} ]; then
		echo "[${CPT}] no header in ${src}";
		let CPT=${CPT}+1;
	fi

	if [ "-put" = ${CMD} ] && [ ${REFSUM} != ${CHECK} ]; then
		echo "[${CPT}] put a header in ${src}";
		cat ${FILEHEADER} ${src} > ${src}.DEL;
		mv ${src}.DEL ${src};
		let CPT=${CPT}+1;
	fi

	if [ "-remove" = ${CMD} ] && [ ${REFSUM} = ${CHECK} ]; then
		echo "[${CPT}] remove header in ${src}";
		sed --expression="1,${NBLIGNES}d" --in-place ${src};
		# sed --expression="1,${NBLIGNES}d" --in-place=.DEL ${src};
		let CPT=${CPT}+1;
	fi
done
