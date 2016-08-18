package com.holan.crawler.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import com.holan.crawler.proxy.ProxyDto;
import com.holan.crawler.proxy.ProxyPool;
import com.holan.crawler.proxy.validator.simple.SimpleValidator;

public class TestProxyPool {
	private static String url = "http://android.myapp.com/";
	private ProxyPool pool;
	
	@Before
	public void init() throws IOException{
		List<String> list = FileUtils.readLines(new File("E://ip.txt"));
		pool = new ProxyPool(url,list,new SimpleValidator(0.9,100));
	}

	private RequestConfig buildConfig(ProxyDto proxyDto) {
		
		HttpHost proxy = new HttpHost(proxyDto.getHost(), proxyDto.getPort(), "http");
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setProxy(proxy)
				.setSocketTimeout(5000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).build();
		return requestConfig;
	}
	
	@Test
	public void testInit(){
		while(true){
			
			System.out.println(String.format("deactivePool size is :%d ; activePool size is : %d ; validateTimes is %d", pool.getDeactiveSize(),pool.activeSize(),pool.getValidateTimes()));
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void test() throws Exception {
		
		for(int i = 0;i< 100000;i++){
			System.out.println(i);
			System.out.println("pool size is : " + pool.activeSize());
			ProxyDto proxyDto = pool.fetchOne();
			System.out.println("pool size is : " + pool.activeSize());
			try{
				RequestConfig requestConfig = buildConfig(proxyDto);
				
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet get = new HttpGet(url);
				get.setConfig(requestConfig);
				HttpResponse response = httpclient.execute(get);
				String html = EntityUtils.toString(response.getEntity(), "UTF-8");
				
				System.out.println(html);
				//System.out.println(response.getEntity().getContentLength());
				pool.recycle(proxyDto,true);
				Thread.sleep(100);
			}catch(Exception ex){
				ex.printStackTrace();
				pool.recycle(proxyDto,false);
			}
			
		}
		
		
	}

}
