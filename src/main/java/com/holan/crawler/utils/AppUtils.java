package com.holan.crawler.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.holan.crawler.processor.dto.AppDto;

public class AppUtils {
	
	public static List<AppDto> parse(String file) {
		List<AppDto> result = new ArrayList<AppDto>();
		try {
			List<String> AppList = FileUtils.readLines(new File(file),"UTF-8");
//			AppList = AppList.subList(0, 100);
			for(String line : AppList){
				try{
					String[] strArr = line.split("\",\"");
					String packageName = strArr[1];
					String name = strArr[0];
					AppDto appDto = new AppDto();
					appDto.setPackageName(packageName);
					appDto.setName(name);
					
					result.add(appDto);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println(line);
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
