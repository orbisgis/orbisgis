/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.toolboxeditor.utils;

import net.opengis.ows._1.ExceptionReport;
import net.opengis.ows._1.ExceptionType;
import net.opengis.wps._1_0_0.ProcessBriefType;
import net.opengis.wps._1_0_0.ProcessDescriptionType;
import net.opengis.wps._1_0_0.ProcessDescriptions;
import net.opengis.wps._1_0_0.WPSCapabilitiesType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import net.opengis.wps._2_0.ProcessOffering;
import net.opengis.wps._2_0.ProcessOfferings;
import org.orbisgis.orbiswps.service.operations.Converter;
import org.orbisgis.toolboxeditor.ToolBoxPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Performs the WPS 1.0.0 request and convert the output into usable object.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS 2018)
 */
public class Wps1_0_0Request {

    private static final Logger LOGGER = LoggerFactory.getLogger(Wps1_0_0Request.class);
    private static final I18n I18N = I18nFactory.getI18n(Wps1_0_0Request.class);

    private ToolBoxPanel toolBoxPanel;

    private List<URI> processUriList = new ArrayList<>();

    public Wps1_0_0Request(ToolBoxPanel toolBoxPanel){
        this.toolBoxPanel = toolBoxPanel;
    }

    public void getCapabilities(URI target){

        for(URI uri : processUriList) {
            toolBoxPanel.remove(uri);
        }

        //Perform the request as a GET (mandatory for a WPS 1.0.0 service)
        URL obj;
        try {
            obj = new URL(target + "?service=WPS&Request=GetCapabilities&AcceptVersions=1.0.0");
        } catch (MalformedURLException e) {
            LOGGER.error(I18N.tr("The uri {1} is not a valid URL\n"+e, target.toString()));
            return;
        }

        HttpURLConnection con;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            LOGGER.error(I18N.tr("IO exception occurs.\n"+e.getLocalizedMessage()));
            return;
        }

        // optional default is GET
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            LOGGER.error(I18N.tr("Protocol exception occurs.\n"+e.getLocalizedMessage()));
            return;
        }

        int responseCode;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            LOGGER.error(I18N.tr("No valid response.\n"+e.getMessage()));
            return;
        }

        if(responseCode != 200){
            LOGGER.error(I18N.tr("No valid response."));
            return;
        }

        Unmarshaller unmarshaller;
        try {
            unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            LOGGER.error(I18N.tr("Unable to get an unmarshaller.\n"+e.getLocalizedMessage()));
            return;
        }

        Object o;
        try {
            o = unmarshaller.unmarshal(con.getInputStream());
        } catch (JAXBException|IOException e) {
            LOGGER.error(I18N.tr("Error while unmarshalling response.\n"+e.getLocalizedMessage()));
            return;
        }

        if(! (o instanceof JAXBElement)){
            return;
        }

        o = ((JAXBElement)o).getValue();

        if(o instanceof ExceptionReport){
            ExceptionReport exceptionReport = (ExceptionReport)o;
            for(ExceptionType exceptionType : exceptionReport.getException()){
                StringBuilder stringBuilder = new StringBuilder(I18N.tr("Exception code '"));
                stringBuilder.append(exceptionType.getExceptionCode());
                stringBuilder.append("'");
                if(exceptionType.isSetLocator()) {
                    stringBuilder.append(I18N.tr(" with code '"));
                    stringBuilder.append(exceptionType.getExceptionCode());
                    stringBuilder.append("'");
                }
                if(exceptionType.isSetExceptionText()) {
                    stringBuilder.append(I18N.tr(" with text "));
                    for(String txt : exceptionType.getExceptionText()){
                        stringBuilder.append("'");
                        stringBuilder.append(txt);
                        stringBuilder.append("'");
                    }
                }
                LOGGER.error(stringBuilder.toString());
            }
        }
        else if(o instanceof WPSCapabilitiesType){
            WPSCapabilitiesType wpsCapabilitiesType = (WPSCapabilitiesType)o;
            for(ProcessBriefType pbt : wpsCapabilitiesType.getProcessOfferings().getProcess()){
                URI uri = URI.create(pbt.getIdentifier().getValue());
                processUriList.add(uri);
                String title = pbt.isSetTitle() ? pbt.getTitle().getValue() : null;
                List<String> keywords = new ArrayList<>();
                HashMap<String, Object> metaMap = new HashMap<>();
                toolBoxPanel.addProcess(target, null, title, uri.toString(), keywords, metaMap);
            }
        }
        return;
    }

    public ProcessOfferings describeProcess(URI target, URI identifier){
        URL obj;
        try {
            obj = new URL(target + "?service=WPS&Request=DescribeProcess&Version=1.0.0&Identifier=" +
                    identifier);
        } catch (MalformedURLException e) {
            LOGGER.error(I18N.tr("The uri {1} is not a valid URL\n"+e, target.toString()));
            return null;
        }

        HttpURLConnection con;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            LOGGER.error(I18N.tr("IO exception occurs.\n"+e.getLocalizedMessage()));
            return null;
        }

        // optional default is GET
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            LOGGER.error(I18N.tr("Protocol exception occurs.\n"+e.getLocalizedMessage()));
            return null;
        }

        int responseCode;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            LOGGER.error(I18N.tr("No valid response.\n"+e.getMessage()));
            return null;
        }

        if(responseCode != 200){
            LOGGER.error(I18N.tr("No valid response."));
            return null;
        }

        Unmarshaller unmarshaller;
        try {
            unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        } catch (JAXBException e) {
            LOGGER.error(I18N.tr("Unable to get an unmarshaller.\n"+e.getLocalizedMessage()));
            return null;
        }

        Object o;
        try {
            o = unmarshaller.unmarshal(con.getInputStream());
        } catch (JAXBException|IOException e) {
            LOGGER.error(I18N.tr("Error while unmarshalling response.\n"+e.getLocalizedMessage()));
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    LOGGER.error(line);
                }
            }catch (IOException ex) {
                LOGGER.error("Unable to read the stream.\n"+ex.getLocalizedMessage());
            }
            return null;
        }

        if(o instanceof ExceptionReport){
            ExceptionReport exceptionReport = (ExceptionReport)o;
            for(ExceptionType exceptionType : exceptionReport.getException()){
                StringBuilder stringBuilder = new StringBuilder(I18N.tr("Exception code '"));
                stringBuilder.append(exceptionType.getExceptionCode());
                stringBuilder.append("'");
                if(exceptionType.isSetLocator()) {
                    stringBuilder.append(I18N.tr(" with code '"));
                    stringBuilder.append(exceptionType.getExceptionCode());
                    stringBuilder.append("'");
                }
                if(exceptionType.isSetExceptionText()) {
                    stringBuilder.append(I18N.tr(" with text "));
                    for(String txt : exceptionType.getExceptionText()){
                        stringBuilder.append("'");
                        stringBuilder.append(txt);
                        stringBuilder.append("'");
                    }
                }
                LOGGER.error(stringBuilder.toString());
            }
        }
        else if(o instanceof ProcessDescriptions){
            ProcessDescriptions processDescriptions = (ProcessDescriptions)o;
            ProcessOfferings processOfferings = new ProcessOfferings();
            for(ProcessDescriptionType processDescriptionType : processDescriptions.getProcessDescription()){
                ProcessOffering processOffering = new ProcessOffering();
                net.opengis.wps._2_0.ProcessDescriptionType processDescriptionType2 =
                        Converter.convertProcessDescriptionType1to2(processDescriptionType);
                processDescriptionType2.setLang(processDescriptions.getLang());
                String id = processDescriptionType2.getIdentifier().getValue().replace(" ", "%20");
                processDescriptionType2.getIdentifier().setValue(id);
                for(InputDescriptionType input : processDescriptionType2.getInput()){
                    id = input.getIdentifier().getValue().replace(" ", "%20");
                    input.getIdentifier().setValue(id);
                }
                for(OutputDescriptionType output : processDescriptionType2.getOutput()){
                    id = output.getIdentifier().getValue().replace(" ", "%20");
                    output.getIdentifier().setValue(id);
                }
                processOffering.setProcess(processDescriptionType2);
                processOfferings.getProcessOffering().add(processOffering);
            }
            return processOfferings;
        }
        return null;
    }
}
