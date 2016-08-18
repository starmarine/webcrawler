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

//		Elements links = doc.select("a[href]"); //����href���Ե�aԪ��
//		Elements pngs = doc.select("img[src$=.png]");
//		  //��չ��Ϊ.png��ͼƬ
//
//		Element masthead = doc.select("div.masthead").first();
//		  //class����masthead��div��ǩ

		Elements iconImg = doc.select("div.det-icon > img"); //��h3Ԫ��֮���aԪ��
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
