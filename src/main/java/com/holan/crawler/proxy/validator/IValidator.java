package com.holan.crawler.proxy.validator;

import com.holan.crawler.proxy.ProxyDto;

public interface IValidator<T> {
	void addResult(ProxyDto proxyDto,T result);
	boolean validate(ProxyDto proxyDto);
	void clear(ProxyDto proxy);
}
