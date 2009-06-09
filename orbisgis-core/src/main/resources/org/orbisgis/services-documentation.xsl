<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
	xmlns:xs='http://www.w3.org/2001/XMLSchema'>
	<xsl:template match="/services">
		<html>
			<head>
				<title>
					OrbisGIS services
				</title>
			</head>
			<body>
				<H1>Services in OrbisGIS</H1>
				<p>This is the description of the available services in OrbisGIS. An
					OrbisGIS service is just a java instance that is accessible by a name and
					that implements a specified interface. From a practical point of view,
					services will be the entry point to access the different
					functionalities in OrbisGIS</p>

				<table border="1">
					<th>Service name</th>
					<th>Service interface</th>
					<th>Description</th>
					<xsl:apply-templates select="service"/>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="service">
			<tr>
				<td><xsl:value-of select="@name"/>
				</td>
				<td><xsl:value-of select="@interface"/>
				</td>
				<td><xsl:value-of select="@description"/>
				</td>
			</tr>
	</xsl:template>

</xsl:stylesheet>