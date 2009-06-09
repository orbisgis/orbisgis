<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
	xmlns:xs='http://www.w3.org/2001/XMLSchema'>
	<xsl:template match="/extension-points">
		<html>
			<head>
				<title>
					OrbisGIS reference
				</title>
			</head>
			<body>
				<H1>OrbisGIS Reference</H1>
				<H2>1.- Extension-points</H2>
				<ul>
					<xsl:apply-templates select="extension-point"/>
				</ul>
				<H2><a href="services.html">2.- Services</a></H2>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="extension-point">
		<li>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="@href"/>
				</xsl:attribute>
				<xsl:value-of select="@id"/>
			</a>
		</li>
	</xsl:template>
</xsl:stylesheet>