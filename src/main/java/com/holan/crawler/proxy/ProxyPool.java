package com.holan.crawler.proxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.holan.crawler.constants.ProxyConstants;
import com.holan.crawler.proxy.validator.IValidator;

public class ProxyPool {
	private Logger logger = Logger.getLogger(this.getClass());
	// ---测试URL，用于验证代理是否可用
	private String validateUrl = "http://android.myapp.com/";
	// ---可用代理
	private BlockingQueue<ProxyDto> activePool = new LinkedBlockingQueue<ProxyDto>(1000);
	// ---不可用代理，有可能是因为活动代理中的代理验证之后不可用了，暂时放入不活动代理中
	private BlockingQueue<ProxyDto> deactivePool = new LinkedBlockingQueue<ProxyDto>(1000);
	private int threadCount = 20;
	private ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
	private AtomicInteger validateTimes = new AtomicInteger(0);
	private int ipCount;
	private IValidator validator;

	public ProxyPool(String validateUrl, Collection<String> ipList,IValidator validator) {
		this.validateUrl = validateUrl;
		this.validator = validator;
		Collection<String> noDuplicateIpList = removeDuplicate(ipList);
		logger.info(String.format("no duplicate ip list is : %d", noDuplicateIpList.size()));
		
		for (String ip : noDuplicateIpList) {
			try {
				String[] ipAndPort = ip.split(":");
				if (ipAndPort.length == 2) {
					ProxyDto proxy = new ProxyDto(ipAndPort[0],
							Integer.parseInt(ipAndPort[1]));
					activePool.add(proxy);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

//		this.ipCount = deactivePool.size();
//		// ---启动线程池来验证代理
//		for (int i = 0; i < threadCount; i++) {
//			threadPool.execute(new ValidateRunnable(this));
//		}

	}

	private Collection<String> removeDuplicate(Collection<String> ipList) {
		Set<String> set = new HashSet<String>();
		for(String ipAndHost : ipList){
			set.add(ipAndHost);
		}
		
		return set;
	}


	public ProxyDto removeFromDeactivePool() {
		ProxyDto dto = null;
		try {
			dto = deactivePool.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	/**
	 * 
	 * @param proxy
	 * @param success	标志这次代理的执行时成功还是失败
	 */
	public void recycle(ProxyDto proxy,Boolean success){
		validator.addResult(proxy,success);
		boolean valid = validator.validate(proxy);
		if(valid){
			addToActivePool(proxy);
		}else{
			addToDeactivePool(proxy);
		}
	}

	private void addToDeactivePool(ProxyDto dto) {
		try {
			activePool.put(dto);
		} catch (InterruptedException e) {
			logger.error(null, e);
		}
	}

	private void addToActivePool(ProxyDto dto) {
		try {
			activePool.put(dto);
		} catch (InterruptedException e) {
			logger.error(null, e);
		}
	}

	public ProxyDto fetchOne() {
		ProxyDto dto = null;
		try {
			logger.info(String.format("fetchOne activePool size is : %d", activePool.size()));
			dto = activePool.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return dto;
	}
	

	private void writeActivePoolToFile() {
		List<String> result = new ArrayList<String>();
		for(ProxyDto dto : activePool){
			logger.info("there is an active proxy " + dto.getHost() + ":" + dto.getPort());
			result.add(dto.getHost() + ":" + dto.getPort());
		}
		
		try {
			logger.info("active pool size is :" + result);
			FileUtils.writeLines(new File(ProxyConstants.PROXY_WRITE_FILE), result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized int activeSize() {
		return activePool.size();
	}
	
	public synchronized int getDeactiveSize() {
		return deactivePool.size();
	}
	public BlockingQueue<ProxyDto> getActivePool() {
		return activePool;
	}

	public void setActivePool(BlockingQueue<ProxyDto> activePool) {
		this.activePool = activePool;
	}

	public BlockingQueue<ProxyDto> getDeactivePool() {
		return deactivePool;
	}

	public void setDeactivePool(BlockingQueue<ProxyDto> deactivePool) {
		this.deactivePool = deactivePool;
	}

	public int getValidateTimes(){
		return validateTimes.get();
	}

	class ValidateRunnable implements Runnable {
		private ProxyPool pool;

		public ValidateRunnable(ProxyPool pool) {
			this.pool = pool;
		}

		public void run() {
			while (true) {
				try {
					// ---获取一条proxy
					logger.info("remove From deactive pool : " + pool.getDeactivePool().size());
					ProxyDto dto = pool.removeFromDeactivePool();
					// ---验证proxy
					boolean result = validate(dto);
					// ---如果验证成功放入activePool,如果失败放入deactivePool
					if (result) {
						logger.info("add to active pool "+pool.getActivePool().size());
						pool.addToActivePool(dto);
						logger.info("add to active pool :"+pool.getActivePool().size() );
					} else {
						logger.info("add to deactive pool"+pool.getDeactivePool().size());
						pool.addToDeactivePool(dto);
						logger.info("add to deactive pool :"+pool.getDeactivePool().size() );
					}
					
					writeActivePoolToFile();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					validateTimes.addAndGet(1);
				}

				try {
					if(validateTimes.get() > ipCount){
						//--已经全部验证过一遍，不用高频度执行了
						logger.info("slow validate");
						Thread.sleep(10000);
					}else{
						//---还没有全部执行一遍,高频度执行
						logger.info("fast validate");
						Thread.sleep(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				

			}

		}

		private boolean validate(ProxyDto dto) {
			logger.info(String.format("validate proxy : %s", dto));
			for(int i=0;i<3;i++){
				boolean result = validateOneTime(dto,i+1);
				if(!result){
					return false;
				}
			}
			
			return true;
		}
		
		private boolean validateOneTime(ProxyDto dto,int count){
			try {
				//logger.info(String.format("validate proxy : %s as count : %d", dto,count));
				
				RequestConfig requestConfig = buildConfig(dto);

				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet get = new HttpGet(validateUrl);
				get.setConfig(requestConfig);
				HttpResponse response = httpclient.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					String html = EntityUtils.toString(response.getEntity(), "UTF-8");
					logger.info("html size is " + html.length());
					return true;
				}

				return false;
			} catch (Exception ex) {
				return false;
			}
		}

		private RequestConfig buildConfig(ProxyDto proxyDto) {

			HttpHost proxy = new HttpHost(proxyDto.getHost(),
					proxyDto.getPort(), "http");

			RequestConfig requestConfig = RequestConfig.custom()
					.setProxy(proxy).setSocketTimeout(5000)
					.setConnectTimeout(5000).setConnectionRequestTimeout(5000)
					.build();
			return requestConfig;
		}

	}
}
