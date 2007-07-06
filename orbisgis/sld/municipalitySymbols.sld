<StyledLayerDescriptor version="1.0.0"
	xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
	xmlns="http://www.opengis.net/sld"
	xmlns:ogc="http://www.opengis.net/ogc"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<NamedLayer>
	
		<UserStyle>
			
			<FeatureTypeStyle>
			<Rule>
				<Name>Rule 1</Name>
		        <PolygonSymbolizer>
                    <Fill>
                    	<CssParameter name="fill">#ffffaa</CssParameter>
                    </Fill>
                	<Stroke>
                    	<CssParameter name="stroke">#000000</CssParameter>
                    	<CssParameter name="stroke-width">1</CssParameter>
                    </Stroke>

                </PolygonSymbolizer>
            </Rule>
			<Rule>
			<Name>Rule 2</Name>
	            <PointSymbolizer>
	            	<Graphic>
	            		<Mark>
	            			<WellKnownName>circle</WellKnownName>
	            			<Fill>
                               <CssParameter name="fill">#6688aa</CssParameter>
                            </Fill>
							<Stroke>
								<CssParameter name="stroke">#000000</CssParameter>
								<CssParameter name="stroke-width">1</CssParameter>
							</Stroke>                            
	            		</Mark>
	            		<Size>
	            			<ogc:Div>
		            			<ogc:PropertyName>PNUM99</ogc:PropertyName>
		            			<ogc:Literal>3</ogc:Literal>
	            			</ogc:Div>
	            		</Size>
	            	</Graphic>
	            </PointSymbolizer>
            </Rule>			
			</FeatureTypeStyle>
		</UserStyle>
	</NamedLayer>
</StyledLayerDescriptor>