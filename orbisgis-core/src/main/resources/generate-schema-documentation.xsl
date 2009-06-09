<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
	xmlns:xs='http://www.w3.org/2001/XMLSchema'>
	<xsl:template match="/xs:schema">
		<html>
			<head>
				<title>
					<xsl:value-of select="xs:annotation/xs:appinfo"/>
				</title>
			</head>
			<body>
				<xsl:apply-templates select="xs:element"/>

				<pre><xsl:text>
































				</xsl:text>
				</pre>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="xs:element">
		<xsl:if test="not(@ref)">
			<h1>
				<a>
					<xsl:attribute name="name">
						<xsl:value-of select="@name"/>
					</xsl:attribute>
					<xsl:if test="@name='extension'">
						<xsl:value-of select="../xs:annotation/xs:appinfo"/>
					</xsl:if>
					<xsl:if test="@name!='extension'">
						<xsl:value-of select="@name"/>
					</xsl:if>
				</a>
			</h1>
			<xsl:if test="@name='extension'">
				<h2>Description:</h2>
				<xsl:value-of select="xs:annotation/xs:documentation/text()"/>
				<h2>Example:</h2>
				<xsl:apply-templates select="../xs:annotation/xs:documentation/*"/>
			</xsl:if>
			<xsl:if test="@name!='extension'">
				<xsl:value-of select="xs:annotation/xs:documentation"/>
			</xsl:if>
			<xsl:if test="xs:complexType/xs:sequence/xs:element">
				<h2>Content:</h2>
				<table border="1">
					<th>Element name</th>
					<th>minOccurs</th>
					<th>maxOccurs</th>
					<xsl:apply-templates
						select="xs:complexType/xs:sequence/xs:element"/>
				</table>
			</xsl:if>
			<h2>Attributes:</h2>

			<xsl:if test="xs:complexType/xs:attribute">
				<table border="1">
					<th>Attribute name</th>
					<th>Required</th>
					<th>Description</th>
					<xsl:apply-templates select="xs:complexType/xs:attribute"/>
				</table>
			</xsl:if>
		</xsl:if>
		<xsl:if test="@ref">
			<tr>
				<td> <a>
					<xsl:attribute name="href"> #
						<xsl:value-of select="@ref"/> </xsl:attribute>
					<xsl:value-of select="@ref"/> </a>
				</td>
				<td>
					<xsl:value-of select="@minOccurs"/>
				</td>
				<td>
					<xsl:value-of select="@maxOccurs"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="xs:sequence">
		<xsl:apply-templates select="xs:element"/>
	</xsl:template>

	<xsl:template match="xs:attribute">
		<tr>
			<td>
				<xsl:value-of select="@name"/>
			</td>
			<td>
				<xsl:if test="@use='required'"> required </xsl:if>
				<xsl:if test="not(@use='required')"> optional </xsl:if>
			</td>
			<td>
				<xsl:value-of select="xs:annotation/xs:documentation"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="*">
		<ul>
			&lt;
			<xsl:value-of select="name()"/>
			<xsl:for-each select="@*">
				<xsl:text>
				</xsl:text>
				<xsl:value-of select="name()"/>="
				<xsl:value-of select="."/>" </xsl:for-each>
			<xsl:if test="child::node()">
				&gt;
				<ul>
					<xsl:apply-templates select="*"/>
				</ul>&lt;/
				<xsl:value-of select="name()"/>&gt; </xsl:if>
			<xsl:if test="not(child::node())"> /&gt;</xsl:if>
		</ul>
	</xsl:template>
</xsl:stylesheet>