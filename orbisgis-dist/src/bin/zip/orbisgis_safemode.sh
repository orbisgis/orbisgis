#! /bin/bash
#
# OrbisGIS is a GIS application dedicated to scientific spatial simulation.
# This cross-platform GIS is developed at French IRSTV institute and is able to
# manipulate and create vector and raster spatial information.
#
# OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
# team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
#
# Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
#
# This file is part of OrbisGIS.
#
# OrbisGIS is free software: you can redistribute it and/or modify it under the
# terms of the GNU General Public License as published by the Free Software
# Foundation, either version 3 of the License, or (at your option) any later
# version.
#
# OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
# A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with
# OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
#
# For more information, please consult: <http://www.orbisgis.org/>
# or contact directly:
# info_at_ orbisgis.org
#

# Total memory in KB
totalMemKB=$(awk '/MemTotal:/ { print $2 }' /proc/meminfo)

# If unable to retrieve the memory, run orbisgis with 1024M
if [ -z "$totalMemKB" ]; then
	cd $(dirname "$0")
	java -Xmx1024m -jar orbisgis.jar $* --nofailmode
# Else, uses a percentage of the physical memory
else
	# Percentage of memory to use for Java heap
	usagePercent=30

	let heapKB=$totalMemKB*$usagePercent/100
	let heapMB=$heapKB/1024
	cd $(dirname "$0")
	java -Xmx${heapMB}m -jar orbisgis.jar $* --nofailmode
fi
