package com.holan.crawler.test.validator.simple;

import org.junit.Assert;
import org.junit.Test;

import com.holan.crawler.proxy.ProxyDto;
import com.holan.crawler.proxy.validator.simple.SimpleValidator;

public class TestSimpleValidator {
	
	@Test
	public void test(){
		SimpleValidator validator = new SimpleValidator(0.9, 100);
		
		ProxyDto p = new ProxyDto("xxx", 1);
		for(int i=0;i<10;i++){
			if(i == 0){
				validator.addResult(p, true);
			}else{
				validator.addResult(p, false);
			}
		}
		
		Assert.assertEquals(0.9d, validator.computeThreshHold(p),0.000001);
		System.out.println(validator.computeThreshHold(p));
		Assert.assertTrue(validator.validate(p));
		
		for(int i=0;i<100;i++){
			if(i%10 == 0){
				validator.addResult(p, true);
			}else{
				validator.addResult(p, false);
			}
		}
		
		Assert.assertEquals(0.9d, validator.computeThreshHold(p),0.000001);
		System.out.println(validator.computeThreshHold(p));
		Assert.assertFalse(validator.validate(p));
		
	}
}
