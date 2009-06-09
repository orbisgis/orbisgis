<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:m="http://maven.apache.org/POM/4.0.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://maven.apache.org/POM/4.0.0">
	
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:namespace-alias stylesheet-prefix="m" result-prefix="#default"/>
	
	<xsl:variable name="parent-version">2.0.0-SNAPSHOT</xsl:variable>
	<xsl:variable name="commons-version">1.0.3-SNAPSHOT</xsl:variable>
	<xsl:variable name="gdms-version">1.3.0-SNAPSHOT</xsl:variable>
	<xsl:variable name="grap-version">1.1.3-SNAPSHOT</xsl:variable>
	<xsl:variable name="h2-version">1.0.1</xsl:variable>
	<xsl:variable name="core-version">1.2.0-SNAPSHOT</xsl:variable>
	<xsl:variable name="core-ui-version">1.3.0-SNAPSHOT</xsl:variable>
	<xsl:variable name="core-full-ui-version">1.0.0-SNAPSHOT</xsl:variable>
	<xsl:variable name="games-version">1.0.0-SNAPSHOT</xsl:variable>
	<xsl:variable name="processing-plugin-version">1.0.3-SNAPSHOT</xsl:variable>
	<xsl:variable name="urbsat-version">1.0.4-SNAPSHOT</xsl:variable>
	<xsl:variable name="plugin-manager-version">1.1.3-SNAPSHOT</xsl:variable>
	<xsl:variable name="processing-version">1.1.4-SNAPSHOT</xsl:variable>
	<xsl:variable name="sif-version">1.0.4-SNAPSHOT</xsl:variable>
	
	<!-- COMMONS -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='commons']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>commons</m:artifactId>
			<m:version>
				<xsl:value-of select="$commons-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='commons']">
		<m:version>
			<xsl:value-of select="$commons-version"/>
		</m:version>
	</xsl:template>
	
	<!-- GDMS -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='gdms']">
		<m:dependency>
			<m:groupId>org.gdms</m:groupId>
			<m:artifactId>gdms</m:artifactId>
			<m:version>
				<xsl:value-of select="$gdms-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='gdms']">
		<m:version>
			<xsl:value-of select="$gdms-version"/>
		</m:version>
	</xsl:template>
	
	<!-- GRAP -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='grap']">
		<m:dependency>
			<m:groupId>org.grap</m:groupId>
			<m:artifactId>grap</m:artifactId>
			<m:version>
				<xsl:value-of select="$grap-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='grap']">
		<m:version>
			<xsl:value-of select="$grap-version"/>
		</m:version>
	</xsl:template>
	
	<!-- H2 -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='h2spatial']">
		<m:dependency>
			<m:groupId>h2spatial</m:groupId>
			<m:artifactId>h2spatial</m:artifactId>
			<m:version>
				<xsl:value-of select="$h2-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='h2spatial']">
		<m:version>
			<xsl:value-of select="$h2-version"/>
		</m:version>
	</xsl:template>
	
	<!-- CORE -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='orbisgis-core']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>orbisgis-core</m:artifactId>
			<m:version>
				<xsl:value-of select="$core-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='orbisgis-core']">
		<m:version>
			<xsl:value-of select="$core-version"/>
		</m:version>
	</xsl:template>
	
	<!-- CORE-UI -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='org.orbisgis.core-ui']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>org.orbisgis.core-ui</m:artifactId>
			<m:version>
				<xsl:value-of select="$core-ui-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template
		match="/m:project/m:version[../m:artifactId='org.orbisgis.core-ui']">
		<m:version>
			<xsl:value-of select="$core-ui-version"/>
		</m:version>
	</xsl:template>
	
	<!-- FULL-UI -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='org.orbisgis.full-ui']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>org.orbisgis.full-ui</m:artifactId>
			<m:version>
				<xsl:value-of select="$core-full-ui-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template
		match="/m:project/m:version[../m:artifactId='org.orbisgis.full-ui']">
		<m:version>
			<xsl:value-of select="$core-full-ui-version"/>
		</m:version>
	</xsl:template>
	
	<!-- GAMES -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='org.orbisgis.games']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>org.orbisgis.games</m:artifactId>
			<m:version>
				<xsl:value-of select="$games-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template
		match="/m:project/m:version[../m:artifactId='org.orbisgis.games']">
		<m:version>
			<xsl:value-of select="$games-version"/>
		</m:version>
	</xsl:template>
	
	<!-- PROCESSING-PLUGIN -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='org.orbisgis.processing']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>org.orbisgis.processing</m:artifactId>
			<m:version>
				<xsl:value-of select="$processing-plugin-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template
		match="/m:project/m:version[../m:artifactId='org.orbisgis.processing']">
		<m:version>
			<xsl:value-of select="$processing-plugin-version"/>
		</m:version>
	</xsl:template>
	
	<!-- URBSAT -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='org.urbsat']">
		<m:dependency>
			<m:groupId>org.urbsat</m:groupId>
			<m:artifactId>org.urbsat</m:artifactId>
			<m:version>
				<xsl:value-of select="$urbsat-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='org.urbsat']">
		<m:version>
			<xsl:value-of select="$urbsat-version"/>
		</m:version>
	</xsl:template>
	
	<!-- PLUGIN MANAGER -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='plugin-manager']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>plugin-manager</m:artifactId>
			<m:version>
				<xsl:value-of select="$plugin-manager-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='plugin-manager']">
		<m:version>
			<xsl:value-of select="$plugin-manager-version"/>
		</m:version>
	</xsl:template>
	
	<!-- PROCESSING -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='processing']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>processing</m:artifactId>
			<m:version>
				<xsl:value-of select="$processing-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='processing']">
		<m:version>
			<xsl:value-of select="$processing-version"/>
		</m:version>
	</xsl:template>
	
	<!-- SIF -->
	<xsl:template
		match="/m:project/m:dependencies/m:dependency[m:artifactId='sif']">
		<m:dependency>
			<m:groupId>org.orbisgis</m:groupId>
			<m:artifactId>sif</m:artifactId>
			<m:version>
				<xsl:value-of select="$sif-version"/>
			</m:version>
		</m:dependency>
	</xsl:template>
	
	<xsl:template match="/m:project/m:version[../m:artifactId='sif']">
		<m:version>
			<xsl:value-of select="$sif-version"/>
		</m:version>
	</xsl:template>
	
	<!-- PARENT -->
	<xsl:template
		match="/m:project/m:parent">
		<m:parent>
			<m:artifactId>platform</m:artifactId>
			<m:groupId>org.orbisgis</m:groupId>
			<m:version>
				<xsl:value-of select="$parent-version"/>
			</m:version>
		</m:parent>
	</xsl:template>
	
	<!-- COPY -->
	<xsl:template match="*">
		
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
		
	</xsl:template>
</xsl:stylesheet>
