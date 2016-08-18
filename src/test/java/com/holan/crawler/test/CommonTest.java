package com.holan.crawler.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class CommonTest {
	
	
	@Test
	public void test() throws IOException{
		for(int i =0;i<1000000;i++){
			List<String> list = new ArrayList<String>();
			System.out.println(i);
			list.add("" + i);
			FileUtils.writeLines(new File("E:/test.txt"), list, true);
		}
		
	}
}
