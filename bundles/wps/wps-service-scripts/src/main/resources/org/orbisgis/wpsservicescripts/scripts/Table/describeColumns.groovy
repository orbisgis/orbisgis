package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.attributes.Keyword
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/**
 * This process is used to describe the columns of a table
 * 
 *
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
        traducedTitles = [
                @LanguageString(value = "Describe columns", lang = "en"),
                @LanguageString(value = "Décrire les colonnes", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Extract the name, type and comment from all fields of a table.", lang = "en"),
                @LanguageString(value = "Extrait le nom, le type et le commentaire de chacun des champs d'un table.", lang = "fr")
        ],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Table", lang = "en"),
                        @LanguageString(value = "Table", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Describe", lang = "en"),
                        @LanguageString(value = "Descrition", lang = "fr")
                ])
        ],
        metadata = [
                @MetadataAttribute(title="h2gis", role ="DBMS", href = "http://www.h2gis.org/"),
                @MetadataAttribute(title="postgis", role ="DBMS", href = "http://postgis.net/")
        ])
def processing() {
    
    literalOutput = "No descriptions have been extracted."
    
    
    if(isH2){
        String query =  "CREATE TABLE " + outputTableName +" as SELECT COLUMN_NAME as col_name, TYPE_NAME as col_type,  REMARKS as col_comment from INFORMATION_SCHEMA.COLUMNS where table_name = '"+ tableName+"';"
        sql.execute(query);
        literalOutput = "The descriptions have been extracted."
    }
    else{
        String query =   "CREATE TABLE " + outputTableName +" as SELECT cols.column_name as col_name,cols.udt_name as col_type, pg_catalog.col_description(c.oid, cols.ordinal_position::int) as col_comment FROM pg_catalog.pg_class c, information_schema.columns cols WHERE cols.table_name = '"+tableName +"'AND cols.table_name = c.relname "
        sql.execute(query);
        literalOutput = "The descriptions have been extracted."
    } 
    
}

/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source table. */
@DataStoreInput(
        traducedTitles = [
                @LanguageString(value = "Table", lang = "en"),
                @LanguageString(value = "Table", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Extract name, type and comments from the selected table.", lang = "en"),
                @LanguageString(value = "Extrait les noms, les types et les commentaires de la table.", lang = "fr")
        ])
String tableName

@LiteralDataInput(
        traducedTitles = [
                @LanguageString(value = "Output table name", lang = "en"),
                @LanguageString(value = "Nom de la table de sortie", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Name of the table containing the descriptions.", lang = "en"),
                @LanguageString(value = "Nom de la table contenant les descriptions.", lang = "fr")
        ])
String outputTableName


/** Output message. */
@LiteralDataOutput(
        traducedTitles = [
                @LanguageString(value = "Output message", lang = "en"),
                @LanguageString(value = "Message de sortie", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The output message.", lang = "en"),
                @LanguageString(value = "Le message de sortie.", lang = "fr")
        ])
String literalOutput

