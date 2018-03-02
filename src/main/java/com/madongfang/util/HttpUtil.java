package com.madongfang.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import org.springframework.stereotype.Component;

@Component
public class HttpUtil {

	public String getToString(String requestUrl) throws IOException
	{
		URL url = new URL(requestUrl);
    	HttpURLConnection httpConn = null;
  
    	httpConn = (HttpURLConnection)url.openConnection();
    	httpConn.setRequestMethod("GET");
    	httpConn.connect();
    	
    	InputStream inputStream;
    	try {
    		inputStream = httpConn.getInputStream();
		} catch (IOException e) {
			// TODO: handle exception
			inputStream = httpConn.getErrorStream();
		}
    	return getHttpBody(inputStream);
	}
	
	public String postToString(String requestUrl, String httpBody) throws IOException
	{
		URL url = new URL(requestUrl);
    	HttpURLConnection httpConn = null;
  
    	httpConn = (HttpURLConnection)url.openConnection();
    	httpConn.setRequestMethod("POST");
    	httpConn.setRequestProperty("Content-type", "application/json");
    	httpConn.setDoOutput(true);
    	httpConn.connect();
    	httpConn.getOutputStream().write(httpBody.getBytes());
    	
    	InputStream inputStream;
    	try {
    		inputStream = httpConn.getInputStream();
		} 
    	catch (IOException e) // http返回状态码不为200 OK时，捕获异常
    	{
			// TODO: handle exception
			inputStream = httpConn.getErrorStream();
		}
    	
    	return getHttpBody(inputStream);
	}
	
	public String postToStringWithCert(String requestUrl, String body, String certFilePath, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException
	{
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream inputStream = new FileInputStream(new File(certFilePath));
		try {
			keyStore.load(inputStream, password.toCharArray());
		} finally {
			// TODO: handle finally clause
			inputStream.close();
		}
		
		SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, password.toCharArray()).build();
		
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext, 
				new String[] {"TLSv1"}, 
				null, 
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		
		try {
			HttpPost httpPost = new HttpPost(requestUrl);
			StringEntity entity = new StringEntity(body, "UTF-8");
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			
			try {
				HttpEntity responseEntity = response.getEntity();
				return EntityUtils.toString(responseEntity, "UTF-8");
			} finally {
				// TODO: handle finally clause
				response.close();
			}
		} finally {
			// TODO: handle finally clause
			httpClient.close();
		}
	}
	
	public String getBody(HttpServletRequest request) throws IOException
	{
		return getHttpBody(request.getInputStream());
	}
	
	private String getHttpBody(InputStream in) throws IOException
	{
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine()) != null){
            sb.append(line);
        }

        String reqBody = sb.toString();
        
		return reqBody;
	}
}
