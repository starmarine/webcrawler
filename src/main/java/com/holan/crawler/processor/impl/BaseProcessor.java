package com.holan.crawler.processor.impl;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.holan.crawler.constants.Constants;
import com.holan.crawler.processor.Context;
import com.holan.crawler.processor.Processor;

public class BaseProcessor implements Processor{
	private Logger logger = Logger.getLogger(this.getClass());

	public void process(Context ctx) {
		
		try {
			String url = Constants.BASE_URL + ctx.appDto.getPackageName();
			long start = System.currentTimeMillis();
			String html = ctx.httpUtil.accessUrl(url);
			logger.info("takes time : " + (System.currentTimeMillis()-start));
			Document doc = Jsoup.parse(html);
			ctx.document = doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
