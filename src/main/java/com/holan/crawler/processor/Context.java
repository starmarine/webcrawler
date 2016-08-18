package com.holan.crawler.processor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import com.holan.crawler.constants.Constants;
import com.holan.crawler.processor.dto.AppDto;
import com.holan.crawler.utils.HttpUtil;

public class Context {
	private static Logger logger = Logger.getLogger(Context.class);
	
	public Document document;
	public AppDto appDto;
	public static HttpUtil httpUtil;
	public static ConcurrentHashMap<String, String> pkgToPathMap = new ConcurrentHashMap<String, String>();
	
//	public String getCurrentFolder(){
//		String folder = Math.abs(appDto.getPackageName().hashCode()) % Constants.TOTAL_FOLDER_COUNT + "";
//		
//		return Constants.BASE_IMAGE_FOLDER + "/" + folder;
//	}
	
	public String getImagePath(){
		String result = Constants.BASE_IMAGE_FOLDER + "/" + getImageFileRelativePath();
		return result;
	}
	
	public String getImageFileName(){
		return appDto.getPackageName() + ".png";
	}
	
	public String getImageFileRelativePath() {
		String folder = Math.abs(appDto.getPackageName().hashCode()) % Constants.TOTAL_FOLDER_COUNT + "";
		String imageName = getImageFileName();
		
		return folder + "/" + imageName;
	}
	
//	public static void writePkgToPathMapToFile(String filePath) throws IOException{
//		List<String> result = new ArrayList<String>();
//		for(Entry<String, String> entry : pkgToPathMap.entrySet()){
//			String pkg = entry.getKey();
//			String path = entry.getValue();
//			
//			String line = pkg + Constants.SPLITTER + path;
//			result.add(line);
//		}
//		
//		FileUtils.writeLines(new File(filePath), "UTF-8", result);
//	}
	
	public synchronized static void writeToFileMap(String packageName, String path) {
		List<String> lines = new ArrayList<String>();
		String line = packageName + Constants.SPLITTER + path;
		lines.add(line);
		try {
			FileUtils.writeLines(new File(Constants.MAP_PATH), "UTF-8",lines,true);
		} catch (IOException e) {
			logger.error(null, e);
		}
	}
	
	public static void readPkgToPathMapFromFile(String filePath) throws IOException{
		List<String> lines = FileUtils.readLines(new File(filePath),"UTF-8");
		if(CollectionUtils.isNotEmpty(lines)){
			for(String line : lines){
				if(StringUtils.isNotBlank(line)){
					String[] arr = line.split(Constants.SPLITTER);
					if(arr.length == 2){
						String pkg = arr[0];
						String path = arr[1];
						pkgToPathMap.put(pkg, path);
					}
				}
			}
		}
		
	}

	
}
