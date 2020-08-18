package br.com.security.model;

import java.io.Serializable;

public class Token implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	} 

}
