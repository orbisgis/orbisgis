import sbtantlr.SbtAntlrPlugin

name := "gdms"

version := "2.0-SNAPSHOT"

scalaVersion := "2.9.2"

externalPom()

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += "IRSTV" at "http://repo.orbisgis.org"

classDirectory in Compile <<= baseDirectory(f => new File(f, "target/classes"))

classDirectory in Test <<= baseDirectory(f => new File(f, "target/test-classes"))

seq(antlrSettings: _*)
