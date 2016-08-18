package com.holan.crawler.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.holan.crawler.constants.Constants;
import com.holan.crawler.constants.ProxyConstants;
import com.holan.crawler.processor.Context;
import com.holan.crawler.processor.dto.AppDto;
import com.holan.crawler.processor.impl.BaseProcessor;
import com.holan.crawler.processor.impl.IconProcessor;
import com.holan.crawler.proxy.ProxyPool;
import com.holan.crawler.proxy.validator.IValidator;

@Component
public class Engine {
	private Logger logger = Logger.getLogger(getClass());
	private volatile boolean running = false;
	private ExecutorService executors = Executors.newFixedThreadPool(200);
	
	@Autowired
	private IValidator validator;
	
	public String start(String baseDir) throws IOException{
		if(running){
			return "already Running";
		}else{
			setBaseFolder(baseDir);
	        
	        List<AppDto> list = AppUtils.parse(Constants.APP_LIST_FILE);
			Context.readPkgToPathMapFromFile(Constants.MAP_PATH);
			logger.info(Context.pkgToPathMap.size());
			List<String> ipList = FileUtils.readLines(new File(ProxyConstants.IP_FILE_PATH));
			ProxyPool pool = new ProxyPool(Constants.VALIDATE_URL, ipList,validator);
			HttpUtil httpUtil= new HttpUtil(pool);
			Context.httpUtil = httpUtil;

			for (int i = 0; i < list.size(); i++) {

				try {
					logger.info(Context.pkgToPathMap.size());

					Context ctx = new Context();
					ctx.appDto = list.get(i);
					String pkg = ctx.appDto.getPackageName();
					
					if(Context.pkgToPathMap.containsKey(pkg)){
						logger.info(String.format("%s is already exist", pkg));
						continue;
					}
					logger.info("download("+i+") : " + ctx.appDto.getPackageName());
					DownloadRunnable run = new DownloadRunnable(ctx);
					executors.execute(run);
				} catch (Exception e) {
					logger.error(null,e);
				}
			}			
			return "start";
		}
	}
	
	private void setBaseFolder(String baseDir) {
		Constants.BASE_IMAGE_FOLDER = baseDir + "/images";
		Constants.MAP_PATH = baseDir + "/map.log";
		Constants.TEMP_MAP_PATH = baseDir + "/tempmap.log";
		Constants.APP_LIST_FILE = baseDir + "/haoge.json";
		ProxyConstants.PROXY_WRITE_FILE = baseDir + "/validProxy.txt";
		ProxyConstants.IP_FILE_PATH = baseDir + "/ip.txt";
	}
	
	static class DownloadRunnable implements Runnable{
		private Context ctx;
		
		public DownloadRunnable(Context ctx){
			this.ctx = ctx;
		}

		public void run() {
			BaseProcessor p1 = new BaseProcessor();
			IconProcessor p2 = new IconProcessor();
			p1.process(ctx);
			p2.process(ctx);
		}
	}
}
