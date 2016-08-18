package com.holan.crawler.proxy.validator.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.holan.crawler.proxy.ProxyDto;
import com.holan.crawler.proxy.validator.IValidator;

@Component
public class SimpleValidator implements IValidator<Boolean>{
	private Map<ProxyDto, List<Boolean>> map = new HashMap<ProxyDto, List<Boolean>>();
	
	private double threshHold = 0.9;
	private int timesThreshHold = 100;
	
	public SimpleValidator(){
	}
	
	public SimpleValidator(double threshHold,int timesThreshHold){
		if(threshHold >= 1){
			throw new RuntimeException("thresh hold must be larger than 1");
		}
		this.threshHold = threshHold;
		this.timesThreshHold = timesThreshHold;
	}

	public boolean validate(ProxyDto proxy) {
		
		List<Boolean> list = map.get(proxy);
		
		double currentThreshHold = computeThreshHold(proxy);
		
		if(  (list != null && list.size() >= timesThreshHold) && currentThreshHold >= threshHold){
			return false;
		}
		
		return true;
	}

	public void clear(ProxyDto proxy) {
		map.remove(proxy);
	}

	public void addResult(ProxyDto proxyDto, Boolean result) {
		List<Boolean> list = map.get(proxyDto);
		if(list == null){
			list = new ArrayList<Boolean>();
			map.put(proxyDto, list);
		}
		
		list.add(result);
	}
	
	public double computeThreshHold(ProxyDto proxy){
		List<Boolean> list = map.get(proxy);
		if(CollectionUtils.isEmpty(list)){
			return 0;
		}
		
		double total = 0;
		double fail = 0;
		for(Boolean b : list){
			if(Boolean.FALSE.equals(b)){
				fail++;
			}
			total++;
		}
		
		return fail/total;
	}

}
