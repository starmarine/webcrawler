package com.holan.crawler.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.holan.crawler.constants.Constants;
import com.holan.crawler.constants.ProxyConstants;
import com.holan.crawler.processor.Context;
import com.holan.crawler.processor.dto.AppDto;
import com.holan.crawler.proxy.ProxyPool;
import com.holan.crawler.proxy.validator.simple.SimpleValidator;
import com.holan.crawler.utils.AppUtils;
import com.holan.crawler.utils.Engine;
import com.holan.crawler.utils.HttpUtil;

@RestController
@Validated
public class StatusController{
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private Engine engine;
    
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() throws IOException {   
        return "haha";
    }
    
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start(String base) throws IOException {
        return engine.start(base);
    }

	private void setBaseFolder(String baseDir) {
		Constants.BASE_IMAGE_FOLDER = baseDir + "/images";
		Constants.MAP_PATH = baseDir + "/map.log";
		Constants.TEMP_MAP_PATH = baseDir + "/tempmap.log";
		Constants.APP_LIST_FILE = baseDir + "/haoge.json";
		ProxyConstants.PROXY_WRITE_FILE = baseDir + "/validProxy.txt";
		ProxyConstants.IP_FILE_PATH = baseDir + "/ip.txt";
	}
    
}
