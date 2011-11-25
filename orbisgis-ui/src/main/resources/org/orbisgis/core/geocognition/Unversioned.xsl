<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
	xmlns:xs='http://www.w3.org/2001/XMLSchema'
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:ns3="org.orbisgis.symbol">
	<!--Add namespaces in element-->
	<xsl:template match="symbol-list | legend-container | map-context | map-context//*">
		<xsl:variable name="ns">
			<xsl:choose>
				<!-- layer model namespaces -->
				<xsl:when test="name()='map-context'">
					<xsl:value-of select="'ns4:'"/>
				</xsl:when>
				<xsl:when test="name()='selected-layer'">
					<xsl:value-of select="'ns4:'"/>
				</xsl:when>
				<xsl:when test="name()='simple-legend'">
					<xsl:value-of select="'ns4:'"/>
				</xsl:when>
				<xsl:when test="name()='legends'">
					<xsl:value-of select="'ns4:'"/>
				</xsl:when>
				<xsl:when test="name()='abstract-layer-type'">
					<xsl:value-of select="'ns4:'"/>
				</xsl:when>
				<xsl:when test="name()='layer-type'">
					<xsl:value-of select="'ns4:'"/>
				</xsl:when>
				<xsl:when test="name()='layer-collection-type'">
					<xsl:value-of select="'ns4:'"/>
				</xsl:when>
				<xsl:when test="name()='layer-collection'">
					<xsl:value-of select="'ns4:'"/>
				</xsl:when>
				
				<!-- legend namespaces -->
				<xsl:when test="name()='legend-container'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='legend-type'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='unique-symbol-legend-type'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='unique-symbol-legend'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='classified-legend-type'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='value-classification'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='unique-value-legend-type'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='unique-value-legend'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='interval-classification'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='interval-legend-type'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='interval-legend'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='proportional-legend-type'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='proportional-legend'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='label-legend-type'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='label-legend'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='raster-legend-type'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				<xsl:when test="name()='raster-legend'">
					<xsl:value-of select="'ns2:'"/>
				</xsl:when>
				
				<!-- symbol namespaces -->
				<xsl:when test="name()='symbol-list'">
					<xsl:value-of select="'ns3:'"/>
				</xsl:when>
				<xsl:when test="name()='symbol-type'">
					<xsl:value-of select="'ns3:'"/>
				</xsl:when>
				<xsl:when test="name()='simple-symbol-type'">
					<xsl:value-of select="'ns3:'"/>
				</xsl:when>
				<xsl:when test="name()='simple-symbol'">
					<xsl:value-of select="'ns3:'"/>
				</xsl:when>
				<xsl:when test="name()='symbol-composite-type'">
					<xsl:value-of select="'ns3:'"/>
				</xsl:when>
				<xsl:when test="name()='symbol-composite'">
					<xsl:value-of select="'ns3:'"/>
				</xsl:when>
				<xsl:when test="name()='ns3:property'">
					<xsl:value-of select="'ns3:'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="''"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$ns}{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<!-- Change xsi:type -->
	<xsl:template match="legend-description[@xsi:type]">
		<legend-description>
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="name()='xsi:type'">
						<xsl:attribute name="xsi:type">
							<xsl:value-of select="concat('ns2:',.)"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			<xsl:apply-templates/>
		</legend-description>
	</xsl:template>
	
	<!-- Change xsi:type and outdated symbols-->
	<xsl:template match="symbol">
		<symbol>
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="name()='xsi:type'">
						<xsl:attribute name="xsi:type">
							<xsl:value-of select="concat('ns3:',.)"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when
						test="(name()='symbol-type-id') and (.='org.orbisgis.symbol.point.Circle')">
						<xsl:attribute name="symbol-type-id">
							<xsl:value-of
								select="'org.orbisgis.symbol.polygon.centroid.Circle'"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when
						test="(name()='symbol-type-id') and (.='org.orbisgis.symbol.point.Square')">
						<xsl:attribute name="symbol-type-id">
							<xsl:value-of
								select="'org.orbisgis.symbol.polygon.centroid.Square'"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			<xsl:apply-templates/>
		</symbol>
	</xsl:template>
	
	<!-- copy all attributes 
	<xsl:template match="@*">
	<xsl:copy-of select="."/>
	</xsl:template-->
	
	<!-- Header -->
	<xsl:template match="/geocognition-node">
		<geocognition-node xmlns:ns4="org.orbisgis.mapContext"
			xmlns:ns2="org.orbisgis.legend" xmlns:ns3="org.orbisgis.symbol" id="">
			<xsl:apply-templates/>
		</geocognition-node>
	</xsl:template>
	
	<!-- COPY -->
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>