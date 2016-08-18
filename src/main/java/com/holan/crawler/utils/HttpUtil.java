package com.holan.crawler.utils;

import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.holan.crawler.proxy.ProxyDto;
import com.holan.crawler.proxy.ProxyPool;

public class HttpUtil {
	private static Logger logger = Logger.getLogger(HttpUtil.class);
	private static Logger errorLogger = Logger.getLogger("error-logger");
	
	private ProxyPool pool;
	private int socketTimeout = 5000;
	private int connectTimeout = 5000;
	private int connectionRequestTimeout = 5000;

	public HttpUtil(ProxyPool pool) {
		this.pool = pool;
	}

	private RequestConfig buildConfig(ProxyDto proxyDto) {
		
		HttpHost proxy = new HttpHost(proxyDto.getHost(), proxyDto.getPort(), "http");
		RequestConfig requestConfig = RequestConfig.custom()
				.setProxy(proxy)
				.setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectionRequestTimeout).build();

		return requestConfig;
	}

	public String accessUrl(String url) {

		int i = 0;
		while (true) {
			if(i > 200){
				return "";
			}
			logger.info(String.format("accessUrl : %d", i++));
			ProxyDto proxyDto = pool.fetchOne();
			try {
				
				RequestConfig requestConfig = buildConfig(proxyDto);
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				get.setConfig(requestConfig);
				HttpResponse response = client.execute(get);
				String html = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				
				pool.recycle(proxyDto,true);
				return html;
			} catch (Exception ex) {
				pool.recycle(proxyDto,false);
				if(ex instanceof URISyntaxException || ex instanceof IllegalArgumentException){
					return null;
				}
				errorLogger.error(null, ex);
			}
		}
	}

	public byte[] downloadImage(String url) {
		int i = 0;
		byte[] data = null;
		while(true){
			if(i>200){
				return null;
			}
			logger.info(String.format("downloadImage : %d", i++));
			ProxyDto proxyDto = pool.fetchOne();
			try {
				RequestConfig requestConfig = buildConfig(proxyDto);
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				get.setConfig(requestConfig);
				HttpResponse response = client.execute(get);

				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity resEntity = response.getEntity();
					data = EntityUtils.toByteArray(resEntity);
					pool.recycle(proxyDto,true);
				}
				return data;
			} catch (Exception ex) {
				pool.recycle(proxyDto,false);
				errorLogger.error(null, ex);
			}
		}
		

	}
}
