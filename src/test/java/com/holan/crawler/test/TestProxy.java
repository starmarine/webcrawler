package com.holan.crawler.test;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class TestProxy {
	String url = "http://android.myapp.com/myapp/detail.htm?apkName=com.tencent.mobileqq";

	private RequestConfig buildConfig() {
		HttpHost proxy = new HttpHost("106.40.121.63", 80, "http");
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setProxy(proxy)
				.setSocketTimeout(5000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).build();
		return requestConfig;
	}

	@Test
	public void test() throws Exception {
		RequestConfig requestConfig = buildConfig();
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		get.setConfig(requestConfig);
		HttpResponse response = httpclient.execute(get);
		String html = EntityUtils.toString(response.getEntity(), "UTF-8");
		
		System.out.println(html);
	}

}
