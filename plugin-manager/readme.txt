The generate-schema-documentation.xsl takes the documentation embeded in the schema files and generates a HTML file.

the process does the following:
- Takes the name of the extension-point from the "schema/annotation/appinfo" element
- Takes an xml example directly from "schema/annotation/documentation"
- All other "annotation/documentation" elements are treated as description of the element they are in
