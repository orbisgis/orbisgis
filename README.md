# OrbisGIS [![Build Status](https://travis-ci.org/orbisgis/orbisgis.png?branch=master)](https://travis-ci.org/orbisgis/orbisgis)


[OrbisGIS](http://orbisgis.org/) is a cross-platform open-source Geographic Information System ([GIS](https://en.wikipedia.org/wiki/Geographic_information_system)) created by research and for research. It is leaded by [CNRS](http://www.cnrs.fr/) within the French [Lab-STICC](http://www.lab-sticc.fr/) laboratory ([DECIDE](http://www.labsticc.fr/le-pole-cid/decision-aid-and-knowledge-discovery-decide/) team of Vannes) and licensed under [GPLv3](https://github.com/orbisgis/orbisgis/blob/master/Licenses/license-GPL.txt). OrbisGIS proposes new methods and techniques to model, represent, process and share spatial data, making it easy to monitor geographical territories and manage their evolution.
In a world ever-increasingly aware of its ecological footprint and the relevance of sustainable development, a systematic approach to evaluating public policies is of paramount importance. Such an approach must take into account relevant environmental, social and economic factors to facilitate efficient decision making and planning. As an integrated modeling platform containing analytical tools for computing various indicators at different spatial and temporal scales, OrbisGIS is already an indispensable instrument for many. Come see what all the buzz is about!


For general information, visit our [website](http://www.orbisgis.org). Feel free to [contact us](http://www.orbisgis.org/#contact) or use the [mailing list](http://orbisgis.3871844.n2.nabble.com/).

Developers, check out our [GitHub Wiki](https://github.com/orbisgis/orbisgis/wiki).

Users, please consult the on-line documentation on [doc.orbisgis.org](http://doc.orbisgis.org/).

### Quick build instructions

OrbisGIS uses Maven. To launch a full build (including tests), run the following command:
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
