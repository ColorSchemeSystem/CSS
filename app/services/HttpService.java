package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpService {
	/**
	 * 
	 * @param url
	 * @return
	 */
	public String request(String url) {
		/*
		 * HttpClientはミリ秒単位の指定なので、タイムアウトの設定を
		 */
		  RequestConfig requestConfig = RequestConfig.custom()
		      .setConnectTimeout(1000 * 10)
		      .setSocketTimeout(1000 * 10)
		      .build();
		  List<Header> headers = new ArrayList<Header>();
		  headers.add(new BasicHeader("Accept-Charset","UTF-8"));
		  headers.add(new BasicHeader("Accept-Language","ja, en;q=0.8"));
		  headers.add(new BasicHeader("User-Agent","Apache HttpClient"));
		  CloseableHttpClient httpClient = HttpClientBuilder.create()
		      .setDefaultRequestConfig(requestConfig)
		      .setDefaultHeaders(headers).build();
		  HttpGet httpGet = new HttpGet(url);
		  String body = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			body = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		  return body;
	}
}