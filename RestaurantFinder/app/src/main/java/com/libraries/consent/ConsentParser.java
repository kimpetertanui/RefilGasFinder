package com.libraries.consent;

import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraries.ssl.MGHTTPClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.io.InputStream;

public class ConsentParser {
	
	public InputStream retrieveStream(String url) {
        try {
            HttpClient httpClient = MGHTTPClient.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "application/json");
//            DefaultHttpClient  httpClient = new DefaultHttpClient();
//            HttpPost httpPost = MGHTTPClient.getNonHttpsPost(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("Status Code", "Error " + statusCode + " for URL " + url); 
                return null;
             }
            HttpEntity getResponseEntity = httpResponse.getEntity();
            InputStream stream = getResponseEntity.getContent();
           return stream;
        } 
        catch (IOException e) {
           Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
        }
        return null;
     }
	
	
	public JsonNode getJsonRootNode(String url)	{
		InputStream source = retrieveStream(url);
		if(source == null)
			return null;
		
		JsonFactory f = new JsonFactory();
        f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        
        ObjectMapper mapper = new ObjectMapper(f);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		JsonNode rootNode = null;
		try  {
			rootNode = mapper.readTree(source);
		} 
		catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rootNode;
	}

	public Region getRegion(String url)	{
		InputStream source = retrieveStream(url);
		if(source == null)
			return null;

		JsonFactory f = new JsonFactory();
		f.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);

		ObjectMapper mapper = new ObjectMapper(f);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		Region data = new Region();

		try  {
			data = mapper.readValue(source, Region.class);
		}
		catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}
}
