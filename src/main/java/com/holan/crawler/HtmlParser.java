package com.holan.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

public class HtmlParser {
	
	public static void parse(String html) throws IOException{
		File input = new File("file/test.html");
		Document doc = Jsoup.parse(input, "UTF-8");

//		Elements links = doc.select("a[href]"); //带有href属性的a元素
//		Elements pngs = doc.select("img[src$=.png]");
//		  //扩展名为.png的图片
//
//		Element masthead = doc.select("div.masthead").first();
//		  //class等于masthead的div标签

		Elements iconImg = doc.select("div.det-icon > img"); //在h3元素之后的a元素
		System.out.println(iconImg.first());
		String url = iconImg.text();
		System.out.println(iconImg.attr("src"));
	}
	
	
	@Test
	public void test() throws Exception{
		String html = IOUtils.toString(new FileInputStream(new File("file/test.html")));
		HtmlParser.parse(html);
		
		
		
	}
}
