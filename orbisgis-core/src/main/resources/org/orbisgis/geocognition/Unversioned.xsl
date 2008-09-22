<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
	xmlns:xs='http://www.w3.org/2001/XMLSchema'
	xmlns:ns4='org.orbisgis.MapContext'>
	<xsl:template match="map-context | map-context//*">
		<xsl:element name="ns4:{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template
		match="*[@symbol-type-id='org.orbisgis.symbol.point.Circle'] | *[@symbol-type-id='org.orbisgis.symbol.point.Square']">
		<xsl:element name="ns4:{name()}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="@symbol-type-id[.='org.orbisgis.symbol.point.Circle']">
		<xsl:attribute name="symbol-type-id">
			<xsl:value-of select="'org.orbisgis.symbol.polygon.centroid.Circle'"/>
		</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="@symbol-type-id[.='org.orbisgis.symbol.point.Square']">
		<xsl:attribute name="symbol-type-id">
			<xsl:value-of select="'org.orbisgis.symbol.polygon.centroid.Square'"/>
		</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="@*">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<!-- COPY -->
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>