package org.orbisgis.wpsservice.controller.utils;

import net.opengis.ows._2.CodeType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows._2.MetadataType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import net.opengis.wps._2_0.DescriptionType;
import org.orbisgis.wpsgroovyapi.attributes.TranslatableString;
import org.orbisgis.wpsgroovyapi.attributes.LanguageString;
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This test class perform three test on the DescriptionType annotation parsing.
 * The test are done on converting a @DescriptionTypeAttribute annotation into the model java object.
 * Each test are done on different annotation complexity : a full annotation, a simple and a minimal one.
 *
 * @author Sylvain PALOMINOS
 */
public class DescriptionTypeConvertTest {

    /*************************
     * FULL DESCRIPTION TYPE *
     *************************/

    /** Field containing the full DescriptionTypeAttribute annotation. */
    @DescriptionTypeAttribute(
            title = "DescriptionType attribute title",
            translatedTitles = {
                    @LanguageString(value = "DescriptionType attribute title", lang = "en"),
                    @LanguageString(value = "Titre de l'attribut DescriptionType", lang = "fr")
            },
            resume = "DescriptionType attribute resume",
            translatedResumes = {
                    @LanguageString(value = "DescriptionType attribute resume", lang = "en"),
                    @LanguageString(value = "Description de l'attribut DescriptionType", lang = "fr")
            },
            keywords = {"DescriptionType","Attribute"},
            translatedKeywords = {
                    @TranslatableString(translatableStrings = {
                            @LanguageString(value = "Attribute en", lang = "en"),
                            @LanguageString(value = "Attribute fr", lang = "fr")
                    }),
                    @TranslatableString(translatableStrings = {
                            @LanguageString(value = "DescriptionType en", lang = "en"),
                            @LanguageString(value = "DescriptionType fr", lang = "fr")
                    })
            },
            identifier = "test:descriptionTypeAttribute",
            metadata = {
                    @MetadataAttribute(title = "metadata1", linkType = "simple", role = "role1", href = "href1"),
                    @MetadataAttribute(title = "metadata2", linkType = "simple", role = "role2", href = "href2")
            }
    )
    public Object fullDescriptionTypeAttribute;
    /** Name of the field containing the fullDescriptionTypeAttribute annotation. */
    private static final String FULL_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME = "fullDescriptionTypeAttribute";

    /**
     * Test if the decoding and convert of the full DescriptionTypeAttribute annotation into its java object is valid.
     */
    @Test
    public void testFullDescriptionTypeAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the DescriptionType object
            DescriptionType descriptionType = new DescriptionType();
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field descriptionTypeField = this.getClass().getDeclaredField(FULL_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME);
            for(Annotation annotation : descriptionTypeField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof DescriptionTypeAttribute){
                    annotationFound = true;
                    DescriptionTypeAttribute descriptionTypeAnnotation = (DescriptionTypeAttribute) annotation;
                    ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, descriptionType);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound){
                Assert.fail("Unable to get the annotation '@DescriptionTypeAttribute' from the field '" +
                        FULL_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME +"'.");
            }

            ///////////////////////////////////////
            // Build the DescriptionType to test //
            ///////////////////////////////////////

            DescriptionType toTest = new DescriptionType();

            //Build the title
            List<LanguageStringType> titleList = new ArrayList<>();
            LanguageStringType titleEN = new LanguageStringType();
            titleEN.setValue("DescriptionType attribute title");
            titleEN.setLang("en");
            titleList.add(titleEN);
            LanguageStringType titleFR = new LanguageStringType();
            titleFR.setValue("Titre de l'attribut DescriptionType");
            titleFR.setLang("fr");
            titleList.add(titleFR);

            toTest.getTitle().clear();
            toTest.getTitle().addAll(titleList);

            //Build the resume
            List<LanguageStringType> resumeList = new ArrayList<>();
            LanguageStringType resumeEN = new LanguageStringType();
            resumeEN.setValue("DescriptionType attribute resume");
            resumeEN.setLang("en");
            resumeList.add(resumeEN);
            LanguageStringType resumeFR = new LanguageStringType();
            resumeFR.setValue("Description de l'attribut DescriptionType");
            resumeFR.setLang("fr");
            resumeList.add(resumeFR);

            toTest.getAbstract().clear();
            toTest.getAbstract().addAll(resumeList);

            //Build the keywords
            List<KeywordsType> keywordsTypeList = new ArrayList<>();

            KeywordsType descriptionTypeKeyword = new KeywordsType();
            List<LanguageStringType> descriptionTypeList = new ArrayList<>();
            LanguageStringType descriptionTypeEN = new LanguageStringType();
            descriptionTypeEN.setValue("DescriptionType en");
            descriptionTypeEN.setLang("en");
            descriptionTypeList.add(descriptionTypeEN);
            LanguageStringType descriptionTypeFR = new LanguageStringType();
            descriptionTypeFR.setValue("DescriptionType fr");
            descriptionTypeFR.setLang("fr");
            descriptionTypeList.add(descriptionTypeFR);
            descriptionTypeKeyword.getKeyword().clear();
            descriptionTypeKeyword.getKeyword().addAll(descriptionTypeList);
            keywordsTypeList.add(descriptionTypeKeyword);

            KeywordsType attributeKeyword = new KeywordsType();
            List<LanguageStringType> attributeList = new ArrayList<>();
            LanguageStringType attributeEN = new LanguageStringType();
            attributeEN.setValue("Attribute en");
            attributeEN.setLang("en");
            attributeList.add(attributeEN);
            LanguageStringType attributeFR = new LanguageStringType();
            attributeFR.setValue("Attribute fr");
            attributeFR.setLang("fr");
            attributeList.add(attributeFR);
            attributeKeyword.getKeyword().clear();
            attributeKeyword.getKeyword().addAll(attributeList);
            keywordsTypeList.add(attributeKeyword);

            toTest.getKeywords().clear();
            toTest.getKeywords().addAll(keywordsTypeList);

            //Build the identifier
            CodeType identifier = new CodeType();
            identifier.setValue("test:descriptionTypeAttribute");

            toTest.setIdentifier(identifier);

            //Build the metadata
            List<MetadataType> metadataList = new ArrayList<>();
            MetadataType metadata1 = new MetadataType();
            metadata1.setTitle("metadata1");
            metadata1.setHref("href1");
            metadata1.setRole("role1");
            metadataList.add(metadata1);
            MetadataType metadata2 = new MetadataType();
            metadata2.setTitle("metadata2");
            metadata2.setHref("href2");
            metadata2.setRole("role2");
            metadataList.add(metadata2);

            toTest.getMetadata().clear();
            toTest.getMetadata().addAll(metadataList);

            ///////////////////////////////
            // Tests the DescriptionType //
            ///////////////////////////////

            //Test the title
            String messageTitleNumber = "Number of titles is not the one expected ("+descriptionType.getTitle().size()+
                    " instead of "+toTest.getTitle().size();
            boolean sameSizeTitle = descriptionType.getTitle().size() != toTest.getTitle().size();
            Assert.assertFalse(messageTitleNumber, sameSizeTitle);

            boolean areAllTitlePresent = true;
            for(LanguageStringType title1 : toTest.getTitle()){
                boolean isTitlePresent = false;
                for(LanguageStringType title2 : descriptionType.getTitle()){
                    if(title1.getLang().equals(title2.getLang()) && title1.getValue().equals(title2.getValue())){
                        isTitlePresent = true;
                    }
                }
                if(!isTitlePresent){
                    areAllTitlePresent = false;
                }
            }
            String messageTitleElements = "The title list doesn't contain the same elements.";
            Assert.assertTrue(messageTitleElements, areAllTitlePresent);

            //Test the resume
            String messageResumeNumber = "Number of resumes is not the one expected ("+
                    descriptionType.getAbstract().size()+ " instead of "+toTest.getAbstract().size();
            boolean sameSizeResume = descriptionType.getAbstract().size() == toTest.getAbstract().size();
            Assert.assertTrue(messageResumeNumber, sameSizeResume);

            boolean areAllResumePresent = true;
            for(LanguageStringType resume1 : toTest.getAbstract()){
                boolean isResumePresent = false;
                for(LanguageStringType resume2 : descriptionType.getAbstract()){
                    if(resume1.getLang().equals(resume2.getLang()) && resume1.getValue().equals(resume2.getValue())){
                        isResumePresent = true;
                    }
                }
                if(!isResumePresent){
                    areAllResumePresent = false;
                }
            }
            String messageElements = "The abstract list doesn't contain the same elements.";
            Assert.assertTrue(messageElements, areAllResumePresent);

            //test the keywords
            String messageKeywordsNumber = "Number of keywords is not the one expected ("+
                    descriptionType.getKeywords().size()+ " instead of "+toTest.getKeywords().size();
            boolean sameSizeKeywords = descriptionType.getKeywords().size() == toTest.getKeywords().size();
            Assert.assertTrue(messageKeywordsNumber, sameSizeKeywords);

            boolean condition = true;
            for(KeywordsType keyword1 : descriptionType.getKeywords()){
                boolean sameKeywords = false;
                for(KeywordsType keyword2 : toTest.getKeywords()){
                    boolean validTranslation = (keyword1.getKeyword().size() == keyword2.getKeyword().size());
                    for(LanguageStringType language1 : keyword1.getKeyword()){
                        boolean sameTranslation = false;
                        for(LanguageStringType language2 : keyword2.getKeyword()){
                            if(language1.getLang().equals(language2.getLang()) &&
                                    language1.getValue().equals(language2.getValue())){
                                sameTranslation = true;
                            }
                        }
                        validTranslation &= sameTranslation;
                    }
                    if(validTranslation){
                        sameKeywords = true;
                    }
                }
                condition &= sameKeywords;
            }
            String messageKeywordsElement = "The keyword list doesn't contain the same elements.";
            Assert.assertTrue(messageKeywordsElement, condition);

            //test the identifier
            boolean identifierCondition = descriptionType.getIdentifier().getValue().equals(toTest.getIdentifier().getValue());
            String messageIdentifier = "The identifier is not the one expected ("+descriptionType.getIdentifier().getValue()+
                    " instead of "+toTest.getIdentifier().getValue()+").";
            Assert.assertTrue(messageIdentifier, identifierCondition);

            //Test the metadata
            String messageMetadataNumber = "Number of metadata is not the one expected ("+
                    descriptionType.getMetadata().size()+ " instead of "+toTest.getMetadata().size();
            boolean sameSizeMetadata = descriptionType.getMetadata().size() == toTest.getMetadata().size();
            Assert.assertTrue(messageMetadataNumber, sameSizeMetadata);

            boolean areAllMetadataPresent = true;
            for(MetadataType meta1 : toTest.getMetadata()){
                boolean isMetadataPresent = false;
                for(MetadataType meta2 : descriptionType.getMetadata()){
                    if(meta1.getHref().equals(meta2.getHref()) &&
                            meta1.getRole().equals(meta2.getRole()) &&
                            meta1.getTitle().equals(meta2.getTitle())){
                        isMetadataPresent = true;
                    }
                }
                if(!isMetadataPresent){
                    areAllMetadataPresent = false;
                }
            }
            String messageMetadata = "The metadata list doesn't contain the same elements.";
            Assert.assertTrue(messageMetadata, areAllMetadataPresent);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '"+ FULL_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME +"' from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }

    /***************************
     * SIMPLE DESCRIPTION TYPE *
     ***************************/

    /** Field containing the simple DescriptionTypeAttribute annotation. */
    @DescriptionTypeAttribute(
            title = "DescriptionType attribute title",
            resume = "DescriptionType attribute resume",
            keywords = {"DescriptionType","Attribute"}
    )
    public Object simpleDescriptionTypeAttribute;
    /** Name of the field containing the simpleDescriptionTypeAttribute annotation. */
    private static final String SIMPLE_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME = "simpleDescriptionTypeAttribute";

    /**
     * Test if the decoding and convert of the simple DescriptionTypeAttribute annotation into its java object is valid.
     */
    @Test
    public void testSimpleDescriptionTypeAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the DescriptionType object
            DescriptionType descriptionType = new DescriptionType();
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field descriptionTypeField = this.getClass().getDeclaredField(SIMPLE_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME);
            for(Annotation annotation : descriptionTypeField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof DescriptionTypeAttribute){
                    annotationFound = true;
                    DescriptionTypeAttribute descriptionTypeAnnotation = (DescriptionTypeAttribute) annotation;
                    ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, descriptionType);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound){
                Assert.fail("Unable to get the annotation '@DescriptionTypeAttribute' from the field '" +
                        SIMPLE_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME +"'.");
            }

            ///////////////////////////////////////
            // Build the DescriptionType to test //
            ///////////////////////////////////////

            DescriptionType toTest = new DescriptionType();

            //Build the title
            List<LanguageStringType> titleList = new ArrayList<>();
            LanguageStringType title = new LanguageStringType();
            title.setValue("DescriptionType attribute title");
            titleList.add(title);

            toTest.getTitle().clear();
            toTest.getTitle().addAll(titleList);

            //Build the resume
            List<LanguageStringType> resumeList = new ArrayList<>();
            LanguageStringType resume = new LanguageStringType();
            resume.setValue("DescriptionType attribute resume");
            resumeList.add(resume);

            toTest.getAbstract().clear();
            toTest.getAbstract().addAll(resumeList);

            //Build the keywords
            List<KeywordsType> keywordsTypeList = new ArrayList<>();

            KeywordsType descriptionTypeKeyword = new KeywordsType();
            List<LanguageStringType> descriptionTypeList = new ArrayList<>();
            LanguageStringType descriptionTypeKeyword1 = new LanguageStringType();
            descriptionTypeKeyword1.setValue("DescriptionType");
            descriptionTypeList.add(descriptionTypeKeyword1);
            descriptionTypeKeyword.getKeyword().clear();
            descriptionTypeKeyword.getKeyword().addAll(descriptionTypeList);
            keywordsTypeList.add(descriptionTypeKeyword);

            descriptionTypeKeyword = new KeywordsType();
            descriptionTypeList = new ArrayList<>();
            LanguageStringType descriptionTypeKeyword2 = new LanguageStringType();
            descriptionTypeKeyword2.setValue("Attribute");
            descriptionTypeList.add(descriptionTypeKeyword2);
            descriptionTypeKeyword.getKeyword().clear();
            descriptionTypeKeyword.getKeyword().addAll(descriptionTypeList);
            keywordsTypeList.add(descriptionTypeKeyword);

            toTest.getKeywords().clear();
            toTest.getKeywords().addAll(keywordsTypeList);

            //Build the identifier
            toTest.setIdentifier(null);

            //Build the metadata
            toTest.getMetadata().clear();

            ///////////////////////////////
            // Tests the DescriptionType //
            ///////////////////////////////

            //Test the title
            String messageTitleNumber = "Number of titles is not the one expected ("+descriptionType.getTitle().size()+
                    " instead of "+toTest.getTitle().size();
            boolean sameSizeTitle = descriptionType.getTitle().size() != toTest.getTitle().size();
            Assert.assertFalse(messageTitleNumber, sameSizeTitle);

            boolean areAllTitlePresent = true;
            for(LanguageStringType title1 : toTest.getTitle()){
                boolean isTitlePresent = false;
                for(LanguageStringType title2 : descriptionType.getTitle()){
                    if(title1.getLang() == null && title2.getLang() == null &&
                            title1.getValue().equals(title2.getValue())){
                        isTitlePresent = true;
                    }
                }
                if(!isTitlePresent){
                    areAllTitlePresent = false;
                }
            }
            String messageTitleElements = "The title list doesn't contain the same elements.";
            Assert.assertTrue(messageTitleElements, areAllTitlePresent);

            //Test the resume
            String messageResumeNumber = "Number of resumes is not the one expected ("+
                    descriptionType.getAbstract().size()+ " instead of "+toTest.getAbstract().size();
            boolean sameSizeResume = descriptionType.getAbstract().size() == toTest.getAbstract().size();
            Assert.assertTrue(messageResumeNumber, sameSizeResume);

            boolean areAllResumePresent = true;
            for(LanguageStringType resume1 : toTest.getAbstract()){
                boolean isResumePresent = false;
                for(LanguageStringType resume2 : descriptionType.getAbstract()){
                    if(resume1.getLang() == null && resume2.getLang() == null &&
                            resume1.getValue().equals(resume2.getValue())){
                        isResumePresent = true;
                    }
                }
                if(!isResumePresent){
                    areAllResumePresent = false;
                }
            }
            String messageElements = "The abstract list doesn't contain the same elements.";
            Assert.assertTrue(messageElements, areAllResumePresent);

            //test the keywords
            String messageKeywordsNumber = "Number of keywords is not the one expected ("+
                    descriptionType.getKeywords().size()+ " instead of "+toTest.getKeywords().size()+").";
            boolean sameSizeKeywords = descriptionType.getKeywords().size() == toTest.getKeywords().size();
            Assert.assertTrue(messageKeywordsNumber, sameSizeKeywords);

            boolean condition = true;
            for(KeywordsType keyword1 : descriptionType.getKeywords()){
                boolean sameKeywords = false;
                for(KeywordsType keyword2 : toTest.getKeywords()){
                    boolean validTranslation = (keyword1.getKeyword().size() == keyword2.getKeyword().size());
                    for(LanguageStringType language1 : keyword1.getKeyword()){
                        boolean sameTranslation = false;
                        for(LanguageStringType language2 : keyword2.getKeyword()){
                            if(language1.getLang() == null && language2.getLang() == null &&
                                    language1.getValue().equals(language2.getValue())){
                                sameTranslation = true;
                            }
                        }
                        validTranslation &= sameTranslation;
                    }
                    if(validTranslation){
                        sameKeywords = true;
                    }
                }
                condition &= sameKeywords;
            }
            String messageKeywordsElement = "The keyword list doesn't contain the same elements.";
            Assert.assertTrue(messageKeywordsElement, condition);

            //test the identifier
            boolean identifierCondition = descriptionType.getIdentifier() == null;
            String messageIdentifier = "The identifier is not the one expected ("+descriptionType.getIdentifier()+
                    " instead of "+toTest.getIdentifier()+").";
            Assert.assertTrue(messageIdentifier, identifierCondition);

            //Test the metadata
            String messageMetadataNumber = "Number of metadata is not the one expected ("+
                    descriptionType.getMetadata().size()+ " instead of "+toTest.getMetadata().size();
            boolean sameSizeMetadata = descriptionType.getMetadata().size() == toTest.getMetadata().size();
            Assert.assertTrue(messageMetadataNumber, sameSizeMetadata);

            boolean areAllMetadataPresent = true;
            for(MetadataType meta1 : toTest.getMetadata()){
                boolean isMetadataPresent = false;
                for(MetadataType meta2 : descriptionType.getMetadata()){
                    if(meta1.getHref().equals(meta2.getHref()) &&
                            meta1.getRole().equals(meta2.getRole()) &&
                            meta1.getTitle().equals(meta2.getTitle())){
                        isMetadataPresent = true;
                    }
                }
                if(!isMetadataPresent){
                    areAllMetadataPresent = false;
                }
            }
            String messageMetadata = "The metadata list doesn't contain the same elements.";
            Assert.assertTrue(messageMetadata, areAllMetadataPresent);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '"+ SIMPLE_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME +"' from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }


    /*************************
     * MINI DESCRIPTION TYPE *
     *************************/

    /** Field containing the minimal DescriptionTypeAttribute annotation. */
    @DescriptionTypeAttribute(
            title = "DescriptionType attribute title"
    )
    public Object miniDescriptionTypeAttribute;
    /** Name of the field containing the miniDescriptionTypeAttribute annotation. */
    private static final String  MINI_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME = "miniDescriptionTypeAttribute";

    /**
     * Test if the decoding and convert of the minimal DescriptionTypeAttribute annotation into its java object is valid.
     */
    @Test
    public void testMinimalDescriptionTypeAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the DescriptionType object
            DescriptionType descriptionType = new DescriptionType();
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field descriptionTypeField = this.getClass().getDeclaredField(MINI_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME);
            for(Annotation annotation : descriptionTypeField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof DescriptionTypeAttribute){
                    annotationFound = true;
                    DescriptionTypeAttribute descriptionTypeAnnotation = (DescriptionTypeAttribute) annotation;
                    ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, descriptionType);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound){
                Assert.fail("Unable to get the annotation '@DescriptionTypeAttribute' from the field '" +
                        SIMPLE_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME +"'.");
            }

            ///////////////////////////////////////
            // Build the DescriptionType to test //
            ///////////////////////////////////////

            DescriptionType toTest = new DescriptionType();

            //Build the title
            List<LanguageStringType> titleList = new ArrayList<>();
            LanguageStringType title = new LanguageStringType();
            title.setValue("DescriptionType attribute title");
            titleList.add(title);

            toTest.getTitle().clear();
            toTest.getTitle().addAll(titleList);

            //Build the resume
            toTest.getAbstract().clear();

            //Build the keywords
            toTest.getKeywords().clear();

            //Build the identifier
            toTest.setIdentifier(null);

            //Build the metadata
            toTest.getMetadata().clear();

            ///////////////////////////////
            // Tests the DescriptionType //
            ///////////////////////////////

            //Test the title
            String messageTitleNumber = "Number of titles is not the one expected ("+descriptionType.getTitle().size()+
                    " instead of "+toTest.getTitle().size();
            boolean sameSizeTitle = descriptionType.getTitle().size() != toTest.getTitle().size();
            Assert.assertFalse(messageTitleNumber, sameSizeTitle);

            boolean areAllTitlePresent = true;
            for(LanguageStringType title1 : toTest.getTitle()){
                boolean isTitlePresent = false;
                for(LanguageStringType title2 : descriptionType.getTitle()){
                    if(title1.getLang() == null && title2.getLang() == null &&
                            title1.getValue().equals(title2.getValue())){
                        isTitlePresent = true;
                    }
                }
                if(!isTitlePresent){
                    areAllTitlePresent = false;
                }
            }
            String messageTitleElements = "The title list doesn't contain the same elements.";
            Assert.assertTrue(messageTitleElements, areAllTitlePresent);

            //Test the resume
            String messageResumeNumber = "Number of resumes is not the one expected ("+
                    descriptionType.getAbstract().size()+ " instead of "+toTest.getAbstract().size();
            boolean sameSizeResume = descriptionType.getAbstract().size() == toTest.getAbstract().size();
            Assert.assertTrue(messageResumeNumber, sameSizeResume);

            boolean areAllResumePresent = true;
            for(LanguageStringType resume1 : toTest.getAbstract()){
                boolean isResumePresent = false;
                for(LanguageStringType resume2 : descriptionType.getAbstract()){
                    if(resume1.getLang() == null && resume2.getLang() == null &&
                            resume1.getValue().equals(resume2.getValue())){
                        isResumePresent = true;
                    }
                }
                if(!isResumePresent){
                    areAllResumePresent = false;
                }
            }
            String messageElements = "The abstract list doesn't contain the same elements.";
            Assert.assertTrue(messageElements, areAllResumePresent);

            //test the keywords
            String messageKeywordsNumber = "Number of keywords is not the one expected ("+
                    descriptionType.getKeywords().size()+ " instead of "+toTest.getKeywords().size()+").";
            boolean sameSizeKeywords = descriptionType.getKeywords().size() == toTest.getKeywords().size();
            Assert.assertTrue(messageKeywordsNumber, sameSizeKeywords);

            boolean condition = true;
            for(KeywordsType keyword1 : descriptionType.getKeywords()){
                boolean sameKeywords = false;
                for(KeywordsType keyword2 : toTest.getKeywords()){
                    boolean validTranslation = (keyword1.getKeyword().size() == keyword2.getKeyword().size());
                    for(LanguageStringType language1 : keyword1.getKeyword()){
                        boolean sameTranslation = false;
                        for(LanguageStringType language2 : keyword2.getKeyword()){
                            if(language1.getLang() == null && language2.getLang() == null &&
                                    language1.getValue().equals(language2.getValue())){
                                sameTranslation = true;
                            }
                        }
                        validTranslation &= sameTranslation;
                    }
                    if(validTranslation){
                        sameKeywords = true;
                    }
                }
                condition &= sameKeywords;
            }
            String messageKeywordsElement = "The keyword list doesn't contain the same elements.";
            Assert.assertTrue(messageKeywordsElement, condition);

            //test the identifier
            boolean identifierCondition = descriptionType.getIdentifier() == null;
            String messageIdentifier = "The identifier is not the one expected ("+descriptionType.getIdentifier()+
                    " instead of "+toTest.getIdentifier()+").";
            Assert.assertTrue(messageIdentifier, identifierCondition);

            //Test the metadata
            String messageMetadataNumber = "Number of metadata is not the one expected ("+
                    descriptionType.getMetadata().size()+ " instead of "+toTest.getMetadata().size();
            boolean sameSizeMetadata = descriptionType.getMetadata().size() == toTest.getMetadata().size();
            Assert.assertTrue(messageMetadataNumber, sameSizeMetadata);

            boolean areAllMetadataPresent = true;
            for(MetadataType meta1 : toTest.getMetadata()){
                boolean isMetadataPresent = false;
                for(MetadataType meta2 : descriptionType.getMetadata()){
                    if(meta1.getHref().equals(meta2.getHref()) &&
                            meta1.getRole().equals(meta2.getRole()) &&
                            meta1.getTitle().equals(meta2.getTitle())){
                        isMetadataPresent = true;
                    }
                }
                if(!isMetadataPresent){
                    areAllMetadataPresent = false;
                }
            }
            String messageMetadata = "The metadata list doesn't contain the same elements.";
            Assert.assertTrue(messageMetadata, areAllMetadataPresent);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '"+ MINI_DESCRIPTION_TYPE_ATTRIBUTE_FIELD_NAME +"' from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }
}
