package br.com.security.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionState implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("session_state")
	private String sessionState;
	
	public String getSessionState() {
		return sessionState;
	}

	public void setSessionState(String sessionState) {
		this.sessionState = sessionState;
	}


	
	
}
