Thomas LEDUC - 30/11/2007

HowTo produce "src/main/resources/org/urbsat/plugin/ui/urbsat.xml" :

  1/ cd platform/org.urbsat/xml
  2/ edit the XML file you want to modify
  3/ produce a single urbsat.xml file :
    xmllint --format --xinclude urbsatMain.xml | sed 's# xmlns:xi="http://www.w3.org/2001/XInclude"##' > ../src/main/resources/org/urbsat/plugin/ui/urbsat.xml 
  4/ validate the modifications using xmllint in command line :
    xmllint --noout --schema ../schema/urbsat.xsd ../src/main/resources/org/urbsat/plugin/ui/urbsat.xml
   
In case of ../schema/urbsat.xsd modifications, you have to produce new
JAXB classes using xjc JAXB Binding Compiler :
     xjc -d ../src/main/java/org/urbsat/plugin/ui/jaxb -p org.urbsat.plugin.ui.jaxb ../schema/urbsat.xsd
     
a better way is to produce it using MAVEN !
    <build>
            <plugin>
                <groupId>com.sun.tools.xjc.maven2</groupId>
                <artifactId>maven-jaxb-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <generatePackage>
                        org.urbsat.plugin.ui.jaxb</generatePackage>
                    <generateDirectory>src/main/java</generateDirectory>
                    <includeSchemas>
                        <includeSchema>**/*.xsd</includeSchema>
                    </includeSchemas>
                    <strict>true</strict>
                    <verbose>true</verbose>
                    <extension>yes</extension>
                </configuration>
            </plugin>
    </build>