package com.holan.crawler.proxy;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ProxyDto {
	private String host;
	private int port;
	private boolean active = true;

	public ProxyDto(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean equals(Object arg0) {
		if(!(arg0 instanceof ProxyDto)){
			return false;
		}
		
		ProxyDto temp = (ProxyDto)arg0;
		
		return this.host.equals(temp.host) && this.port == temp.port;
	}
	
	@Override
	public int hashCode() {
		return host.hashCode() * 3 + port * 7;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
//		return "ProxyDto [host=" + host + ", port=" + port + ", active="+ active + "]";
	}
	
	
}
