/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows.remote;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
/**
 * Helpers methods that get remote content. This class is built on top of
 * the apache http components framework.
 * 
 * @author cleglaun
 */
public class OwsServiceUtils {
    
    
    /**
     * Sends a GET request to the selected url.
     * 
     * @param serviceUrl The REST service's url to send an http request to
     * @return Result of the GET request
     */
    public static InputStream callServiceGet(String serviceUrl) {
        InputStream instream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(serviceUrl);
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                entity = new BufferedHttpEntity(entity);
                instream = new BufferedInputStream(entity.getContent());

                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                httpClient.getConnectionManager().shutdown();
            }
        } catch (ClientProtocolException ex) {
            Logger.getLogger(OwsServiceUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return instream;
    }
    
    /**
     * Sends a POST request to the selected url.
     * 
     * @param serviceUrl The REST service's url to send an http request to
     * @param formparams A list of post parameters
     * @param connectionShutdown True to shutdown the connection once the data has been retrieved
     * @return Result of the POST request
     */
    public static InputStream callServicePost(String serviceUrl, List<NameValuePair> formparams, 
            boolean connectionShutdown) {
        
        InputStream instream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(serviceUrl);
            
            UrlEncodedFormEntity owcEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(owcEntity);
            
            HttpResponse response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                responseEntity = new BufferedHttpEntity(responseEntity);
                instream = new BufferedInputStream(responseEntity.getContent());

                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                if (connectionShutdown) {
                    httpClient.getConnectionManager().shutdown();
                }
            }
        } catch (ClientProtocolException ex) {
            Logger.getLogger(OwsServiceUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OwsServiceUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        return instream;
    }
}
