<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
	xmlns:xs='http://www.w3.org/2001/XMLSchema'>
	<xsl:output cdata-section-elements="body"/>
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="category">
		<category>
			<xsl:attribute name="id">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:attribute name="label">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:apply-templates select="category"/>
			<xsl:apply-templates select="menuItem"/>
		</category>
	</xsl:template>

	<xsl:template match="menuItem[className]">
		<sql-instruction>
			<xsl:attribute name="class">
				<xsl:value-of select="className/@value"/>
			</xsl:attribute>
		</sql-instruction>
	</xsl:template>

	<xsl:template match="menuItem[sqlBlock]">
		<sql-script>
			<xsl:attribute name="id">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:attribute name="resource">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:apply-templates select="sqlBlock/comment"/>
			<xsl:apply-templates select="sqlBlock/sqlInstr"/>
		</sql-script>
	</xsl:template>

	<xsl:template match="comment"> /*
		<xsl:value-of select="@value"/>*/ #return# </xsl:template>

	<xsl:template match="sqlInstr">
		<xsl:value-of select="@value"/> #return# </xsl:template>

</xsl:stylesheet>