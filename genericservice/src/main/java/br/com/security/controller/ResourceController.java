package br.com.security.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import br.com.security.model.ResponseError;
import br.com.security.model.SessionState;
import br.com.security.model.Token;
import br.com.security.util.TokenUtil;

@Controller
@RequestMapping(value = "/resource")
public class ResourceController {

	@Value("${service.outgoing.url}")
	private String serviceOutgoing;

	@Value("${spring.application.name}")
	private String serviceName;

	
	@Autowired
	private RestTemplate restTemplate;


	@CrossOrigin
	@RequestMapping(value = "/method", method = RequestMethod.POST)
	public ResponseEntity<?> method(@RequestHeader HttpHeaders reqHeaders, @RequestBody Token token) {

		HttpHeaders headers = new HttpHeaders();		
		
		SessionState sessionState = getSession(token);

		ViewHeader(serviceName, reqHeaders, "x-request-id");
		ViewHeader(serviceName, reqHeaders, "x-b3-traceid");
		ViewHeader(serviceName, reqHeaders, "x-b3-spanid");
		ViewHeader(serviceName, reqHeaders, "x-b3-parentspanid");
		ViewHeader(serviceName, reqHeaders, "x-b3-sampled");
		ViewHeader(serviceName, reqHeaders, "x-b3-flags");
		ViewHeader(serviceName, reqHeaders, "x-ot-span-context");
		ViewHeader(serviceName, reqHeaders, "x-b3-session-id");
		
		System.out.println("Token Session: " + sessionState.getSessionState());
		
		if ( serviceOutgoing.trim().length() > 0 ) {
			
			System.out.println("Send Request to: " + serviceOutgoing);
			
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			headers.add("x-b3-session-id", sessionState.getSessionState());
	
			org.springframework.http.HttpEntity<Token> entity = new org.springframework.http.HttpEntity(token, headers);
			
			String result = restTemplate.postForObject(serviceOutgoing, entity, String.class);
		
			return new ResponseEntity<>(result, HttpStatus.OK);
			
		} else {
		
			System.out.println("Echo response");
			
			return new ResponseEntity<>(token, HttpStatus.OK);
			
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/methodwitherror", method = RequestMethod.POST)
	public ResponseEntity<ResponseError> methodError(@RequestHeader HttpHeaders reqHeaders) {
	
		return new ResponseEntity<ResponseError>(new ResponseError("99", "Forced error"), HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	

	private static void ViewHeader(String method, HttpHeaders headers, String key) {
		
		List<String> vals = headers.get(key);
		
		if (vals != null && !vals.isEmpty()) {
			System.out.println(method + " => " + key + ": " + vals.get(0) );
		}
		
	}

	private String getJsonInfo(String jsonObject, String field) {

		String result = "";

		try {

			JSONObject json = new JSONObject(jsonObject);

			result = json.getString(field);

		} catch (Exception e) {
			result = e.getMessage();
		}

		return result;

	}

	private SessionState getSession(Token token) {

		TokenUtil.setAccessToken(token.getToken());

		String userSession = "";

		try {
			userSession = getJsonInfo(TokenUtil.getPayLoad(), "session_state");
		} catch (Exception e) {
			e.printStackTrace();
		}

		SessionState sessionState = new SessionState();

		sessionState.setSessionState(userSession);

		return sessionState;

	}

}
