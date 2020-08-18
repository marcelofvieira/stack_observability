package br.com.security.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;

import io.jsonwebtoken.Jwts;

public class TokenUtil {

	private static TokenResponse tokenResponse = null;
	
	//--------------------------------------------------------------------------------------------------
	//Public internal class
	//--------------------------------------------------------------------------------------------------
	
	//--------------------------------------------
	//TokenData
	//--------------------------------------------
	public static class TokenData {
		
		private static String data = "";
		
		public String getData() {
			return data;
		}

		public void setData(String data) {
			TokenData.data = data;
		}

		public TokenData getValue (String field) throws Exception {
			
			try {
			
				TokenData tokenData = new TokenData();
				
				JSONObject json = new JSONObject(getData());
				
				if ( field.trim().length() == 0 ) {
					tokenData.setData(json.toString() ) ;
				} else {
					tokenData.setData(json.get(field).toString()) ;
				}

				return tokenData;
			}
			catch (Exception e) {
				throw new Exception("getValue." + e.getMessage());
			}
		}

		public boolean fieldExists (String field) throws Exception {
			
			try {
				
				JSONObject json = new JSONObject(getData());
				
				return json.has(field);

			}
			catch (Exception e) {
				throw new Exception("getValue." + e.getMessage());
			}
		}
		
		@Override
		public String toString() {
			return data;
		}

	}
	
	
	//--------------------------------------------
	//TokenResponse
	//--------------------------------------------
	public static class TokenResponse {
		
		private static String message = "";
		private static Boolean isValid = false;

		public static String getMessage() {
			return TokenResponse.message;
		}
		public static void setMessage(String message) {
			TokenResponse.message = message;
		}
		public static Boolean getIsValid() {
			return TokenResponse.isValid;
		}
		public static void setIsValid(Boolean isValid) {
			TokenResponse.isValid = isValid;
		}
		@Override
		public String toString() {
			return "TokenResponse [isValid=" + TokenResponse.isValid + ", message=" + TokenResponse.message + "]";
		}

	}
		
	public static enum TOKEN_PART {HEADER, PAYLOAD, SIGNATURE};
	
	private static String accessToken = "";
	private static String secretKey = "";

	public static String getAccessToken() {
		return TokenUtil.accessToken;
	}

	public static void setAccessToken(String accessToken) {
		TokenUtil.accessToken = accessToken;
	}

	public static void setSecretKey(String secretKey) {
		TokenUtil.secretKey = secretKey;
	}

	
	//--------------------------------------------------------------------------------------------------
	//Private utils methods
	//--------------------------------------------------------------------------------------------------
	private static String getTokenPart (TOKEN_PART part) throws Exception {
		
		try 
		{
			String returnPart = "";
			
			if ( accessToken.trim().length() == 0 ) {
				throw new Exception("Token not found!");
			}
			
			String[] tokenPart = TokenUtil.accessToken.split("\\.");
			
			if ( tokenPart.length != 3 ) {
				throw new Exception("Invalid Token format!");
			}
			
			switch (part){
				case HEADER:
					returnPart = tokenPart[0];
					break;
					
				case PAYLOAD:
					returnPart = tokenPart[1];
					break;
					
				case SIGNATURE:
					returnPart = tokenPart[2];
					break;
				default:
					throw new Exception("Invalid Token part");
			}
			
			return returnPart;
			
		} catch (Exception e) {
			throw new Exception("getTokenPart " + e.getMessage());
		}
		
	}
	
	private static String getDecodedString(String encodedString) throws Exception { 
		try {
			return new String (Base64.decodeBase64(encodedString.getBytes(StandardCharsets.UTF_8)));
		}
		catch (Exception e ) {
			throw new Exception("getDecodeString." + e.getMessage());
		}
	}
	
	private static PublicKey getPublicKey(String publicKey) throws Exception{
		try{
	    
			byte[] certder = Base64.decodeBase64(publicKey);
			
			InputStream certstream = new ByteArrayInputStream (certder);
			
			Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate(certstream);
			
			return cert.getPublicKey();
		}
		catch(Exception e) {
		    throw new Exception ("getPublicKey." + e.getMessage());
		}
		
	}
	
	
	//--------------------------------------------------------------------------------------------------
	//Public Methods
	//--------------------------------------------------------------------------------------------------
	public static TokenResponse validateToken() 
    {
		
		try {
			
			Jwts.parser().setSigningKey(getPublicKey(TokenUtil.secretKey)).parseClaimsJws(TokenUtil.accessToken);
		
			TokenResponse.setMessage("Token is valid");
			TokenResponse.setIsValid(true);
			
		}
		catch (Exception e) {
		
			TokenResponse.setMessage("validateToken." + e.getMessage());
			TokenResponse.setIsValid(false);
		}
		
		return TokenUtil.tokenResponse ;

    }
	 
	public static String getEncodedHeader() throws Exception {
		try {
			return getTokenPart(TOKEN_PART.HEADER);
		}
		catch (Exception e ) {
			throw new Exception("getEncodedHeader." + e.getMessage());
		}
	}
	
	public static String getHeader() throws Exception {
		try
		{
			return getDecodedString(getEncodedHeader()); 
		}
		catch (Exception e) {
			throw new Exception("getHeader." + e.getMessage());
		}
	}
	
	public static String getEncodedPayLoad() throws Exception {
		try {
			return getTokenPart(TOKEN_PART.PAYLOAD);
		}
		catch (Exception e ) {
			throw new Exception("getEncodedPayLoad." + e.getMessage());
		}
	}
	
	public static String getPayLoad() throws Exception {
		try
		{
			return getDecodedString(getEncodedPayLoad()); 
		}
		catch (Exception e) {
			throw new Exception("getPayLoad." + e.getMessage());
		}
	}
	
	public static String getEncodedSignature() throws Exception {

		try {
			return getTokenPart(TOKEN_PART.SIGNATURE);
		}
		catch (Exception e ) {
			throw new Exception("getEncodedSignature." + e.getMessage());
		}
	}	
	
	public static TokenData getValue (TOKEN_PART part, String field) throws Exception {
		
		try {
		
			TokenData tokenData = new TokenData();

			switch (part) {
				case HEADER:
					tokenData.setData(getHeader());
					break;

				case PAYLOAD:
					tokenData.setData(getPayLoad());
					break;
					
				case SIGNATURE:
					throw new Exception("Token part SIGNATURE is invalid for getValue function");
				
				default:
					throw new Exception("Invalid Token part");
			}
				
			return tokenData.getValue(field);

		} catch (Exception e) {
			throw new Exception("getData." + e.getMessage());
		}
		
		
	}

}
