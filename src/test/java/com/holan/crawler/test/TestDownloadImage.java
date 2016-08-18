package com.holan.crawler.test;

import org.junit.Test;

import com.holan.crawler.processor.Context;
import com.holan.crawler.processor.dto.AppDto;
import com.holan.crawler.processor.impl.BaseProcessor;
import com.holan.crawler.processor.impl.IconProcessor;

public class TestDownloadImage {

	@Test
	public void test(){
		Context ctx = new Context();
		AppDto app = new AppDto();
		app.setPackageName("com.qqgame.hlddz");
		app.setName("»¶ÀÖ¶·µØÖ÷");
		ctx.appDto = app;
		BaseProcessor p1 = new BaseProcessor();
		IconProcessor p2 = new IconProcessor();
		
		p1.process(ctx);
		p2.process(ctx);
	}
	
}
