package org.orbisgis.wpsservice.utils;

import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import net.opengis.wps._2_0.ProcessDescriptionType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains methods which does the translation of a process.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessTranslator {


    /**
     * Return the process with the given language translation.
     * If the asked translation doesn't exists, use the english one. If it doesn't exists too, uses one of the others.
     * @param process Process to traduce.
     * @param language Language asked.
     * @return The traduced process.
     */
    public static final ProcessDescriptionType getTranslatedProcess(ProcessDescriptionType process, String language){
        ProcessDescriptionType translatedProcess = new ProcessDescriptionType();
        translatedProcess.setLang(language);
        List<InputDescriptionType> inputList = new ArrayList<>();
        for(InputDescriptionType input : process.getInput()){
            InputDescriptionType translatedInput = new InputDescriptionType();
            translatedInput.setDataDescription(input.getDataDescription());
            translatedInput.setMaxOccurs(input.getMaxOccurs());
            translatedInput.setMinOccurs(input.getMinOccurs());
            translateDescriptionType(translatedInput, input, language);
            inputList.add(translatedInput);
        }
        translatedProcess.getInput().clear();
        translatedProcess.getInput().addAll(inputList);
        List<OutputDescriptionType> outputList = new ArrayList<>();
        for(OutputDescriptionType output : process.getOutput()){
            OutputDescriptionType translatedOutput = new OutputDescriptionType();
            translatedOutput.setDataDescription(output.getDataDescription());
            translateDescriptionType(translatedOutput, output, language);
            outputList.add(translatedOutput);
        }
        translatedProcess.getOutput().clear();
        translatedProcess.getOutput().addAll(outputList);
        translateDescriptionType(translatedProcess, process, language);
        return translatedProcess;
    }

    /**
     * Sets the given translatedDescriptionType with the traduced elements of the source descriptionType.
     * If the asked translation doesn't exists, use the english one. If it doesn't exists too, uses one of the others.
     *
     * @param translatedDescriptionType Translated DescriptionType.
     * @param descriptionType Source DescriptionType.
     * @param language Language asked.
     */
    public static final void translateDescriptionType(DescriptionType translatedDescriptionType,
                                                      DescriptionType descriptionType,
                                                      String language){
        String enLanguage = "en";
        translatedDescriptionType.setIdentifier(descriptionType.getIdentifier());
        translatedDescriptionType.getMetadata().clear();
        translatedDescriptionType.getMetadata().addAll(descriptionType.getMetadata());
        //Find the good abstract
        LanguageStringType translatedAbstract = new LanguageStringType();
        boolean defaultAbstrFound = false;
        for(LanguageStringType abstr : descriptionType.getAbstract()){
            if(abstr.getLang() != null && abstr.getLang().equals(language)){
                translatedAbstract = abstr;
                break;
            }
            else if(abstr.getLang() != null && abstr.getLang().equals(enLanguage)){
                translatedAbstract = abstr;
                defaultAbstrFound = true;
            }
            else if(!defaultAbstrFound){
                translatedAbstract = abstr;
            }
        }
        List<LanguageStringType> abstrList = new ArrayList<>();
        abstrList.add(translatedAbstract);
        translatedDescriptionType.getAbstract().clear();
        translatedDescriptionType.getAbstract().addAll(abstrList);
        //Find the good title
        LanguageStringType translatedTitle = new LanguageStringType();
        boolean defaultTitleFound = false;
        for(LanguageStringType title : descriptionType.getTitle()){
            if(title.getLang() != null && title.getLang().equals(language)){
                translatedTitle = title;
                break;
            }
            else if(title.getLang() != null && title.getLang().equals(enLanguage)){
                translatedTitle = title;
                defaultTitleFound = true;
            }
            else if(!defaultTitleFound){
                translatedTitle = title;
            }
        }
        List<LanguageStringType> titleList = new ArrayList<>();
        titleList.add(translatedTitle);
        translatedDescriptionType.getTitle().clear();
        translatedDescriptionType.getTitle().addAll(titleList);
        //Find the good keywords
        List<KeywordsType> keywordsList = new ArrayList<>();
        KeywordsType translatedKeywords = new KeywordsType();
        List<LanguageStringType> keywordList = new ArrayList<>();
        for(KeywordsType keywords : descriptionType.getKeywords()) {
            LanguageStringType translatedKeyword = new LanguageStringType();
            boolean defaultKeywordFound = false;
            for (LanguageStringType keyword : keywords.getKeyword()) {
                if (keyword.getLang() != null && keyword.getLang().equals(language)) {
                    translatedKeyword = keyword;
                    break;
                } else if (keyword.getLang() != null && keyword.getLang().equals(enLanguage)) {
                    translatedKeyword = keyword;
                    defaultKeywordFound = true;
                } else if (!defaultKeywordFound) {
                    translatedKeyword = keyword;
                }
            }
            keywordList.add(translatedKeyword);
        }
        translatedKeywords.getKeyword().clear();
        translatedKeywords.getKeyword().addAll(keywordList);
        keywordsList.add(translatedKeywords);
        translatedDescriptionType.getKeywords().clear();
        translatedDescriptionType.getKeywords().addAll(keywordsList);
    }
}
