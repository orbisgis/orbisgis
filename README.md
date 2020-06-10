### ALERT

After several years of development, it is time to reorganize the OrbisGIS architecture to offer to the users and the developers a robust libraries and framework, a better documentation and a more user-friendly GUI. Because the OrbisGIS team is at the heart of open source communities, because it supports collective work and the sharing of tools and methods rather than reinventing the wheel, we decided to split OrbisGIS in a two main libraries : 

- OrbisData : https://github.com/orbisgis/orbisdata -> to manage, process data
- OrbisMap : https://github.com/orbisgis/orbismap -> to renderer geospatial data

These libraries take profit of the FOSS ecosystem and the next OrbisGIS user interface will be developped on top of the Eclipse RCP framework and the [dbeaver](https://dbeaver.io/) tool. Many dbeaver features are in line with the needs of the OrbisGIS users and its functions (SQL editor, tree database explorer, table viewer...). We are therefore thinking of adding OrbisGIS features to dbeaver and use it in replacement to the current OrbisGIS (based on DockingFrames). 
OrbisRCP aggregates all OrbisGIS plugins available for DBeaver. See : https://github.com/orbisgis/orbisrcp

Note that the [H2GIS](http://www.h2gis.org/) extension and the [CTS](https://github.com/orbisgis/cts) library are still being actively developed by the OrbisGIS team.




# OrbisGIS [![Build Status](https://travis-ci.org/orbisgis/orbisgis.png?branch=master)](https://travis-ci.org/orbisgis/orbisgis)


[OrbisGIS](http://orbisgis.org/) is a cross-platform open-source Geographic Information System ([GIS](https://en.wikipedia.org/wiki/Geographic_information_system)) created by research and for research. It is leaded by [CNRS](http://www.cnrs.fr/) within the French [Lab-STICC](http://www.lab-sticc.fr/) laboratory ([DECIDE](http://www.lab-sticc.fr/en/teams/m-570-decide.htm) team of Vannes) and licensed under [GPLv3](https://github.com/orbisgis/orbisgis/blob/master/Licenses/license-GPL.txt). OrbisGIS proposes new methods and techniques to model, represent, process and share spatial data, making it easy to monitor geographical territories and manage their evolution.
In a world ever-increasingly aware of its ecological footprint and the relevance of sustainable development, a systematic approach to evaluating public policies is of paramount importance. Such an approach must take into account relevant environmental, social and economic factors to facilitate efficient decision making and planning. As an integrated modeling platform containing analytical tools for computing various indicators at different spatial and temporal scales, OrbisGIS is already an indispensable instrument for many. Come see what all the buzz is about!


For general information, visit our [website](http://www.orbisgis.org). Feel free to [contact us](http://www.orbisgis.org/#contact) or use the [mailing list](http://orbisgis.3871844.n2.nabble.com/).

Developers, check out our [GitHub Wiki](https://github.com/orbisgis/orbisgis/wiki).

Users, please consult the on-line documentation on [doc.orbisgis.org](http://doc.orbisgis.org/).

### Quick build instructions

OrbisGIS uses Maven. To launch a full build (including the tests), run the following command:
```bash
$ mvn clean install
```
To run OrbisGIS using Maven:
```bash
$ cd orbisgis-dist
$ mvn exec:exec
```

To build a release as a standalone zip file:
```bash
cd orbisgis-dist
mvn package assembly:single
```
