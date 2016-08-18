package com.holan.crawler.processor.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.holan.crawler.constants.Constants;
import com.holan.crawler.processor.Context;
import com.holan.crawler.processor.Processor;
import com.holan.crawler.utils.HttpUtil;

public class IconProcessor implements Processor {
	private Logger logger = Logger.getLogger(this.getClass());
	
	public void process(Context ctx) {

		try {
			Document doc = ctx.document;
			Elements iconImg = doc.select("div.det-icon > img");
			String url = iconImg.attr("src");

			if (org.apache.commons.lang.StringUtils.isNotBlank(url)) {
				long start = System.currentTimeMillis();
				byte[] data = ctx.httpUtil.downloadImage(url);
				logger.info("takes time : " + (System.currentTimeMillis()-start));
				if(data != null){
					FileUtils.writeByteArrayToFile(new File(ctx.getImagePath()),data);
					String packageName = ctx.appDto.getPackageName();
					String path = ctx.getImageFileRelativePath();
					Context.pkgToPathMap.put(packageName,path);
					Context.writeToFileMap(packageName,path);
				}else{
					String packageName = ctx.appDto.getPackageName(); 
					System.out.println("找不到应用:"+packageName);
					Context.pkgToPathMap.put(packageName, Constants.NOT_EXIST);
					Context.writeToFileMap(packageName,Constants.NOT_EXIST);
				}
			}else{
				String packageName = ctx.appDto.getPackageName();
				System.out.println("找不到应用:"+packageName);
				Context.pkgToPathMap.put(packageName, Constants.NOT_EXIST);
				Context.writeToFileMap(packageName,Constants.NOT_EXIST);
			}

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	

}
