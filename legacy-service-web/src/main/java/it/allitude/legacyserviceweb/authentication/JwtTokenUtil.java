package it.allitude.legacyserviceweb.authentication;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.allitude.legacyserviceweb.models.EncryptUtil;

@Component
public class JwtTokenUtil implements Serializable {

	@Autowired 
	EncryptUtil _encryptUtil;

	private static final long serialVersionUID = -2550185165626007488L;
	
	public static final long JWT_TOKEN_VALIDITY = 5*60*60;
	private String getJWTSecret()
	{
		// return ConfigParms.getPropertyValue("jwt.secret");		
		return "TODOGREGADFASDNDNNquestoeilsecretchedovrebbeesseremessonelfileapplicationpropertieselettodaquestometodoDOVREBBEesserediversodaambienteadambienteAFSDFfasdrf2354";
	}
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	public String getSessionFromToken(String token) {
		return getValueFromToken(token, "session");
	}
	public String getPasswordFromToken(String token) throws IllegalBlockSizeException, BadPaddingException {
		String pwd = getValueFromToken(token, "password");
		return _encryptUtil.decrypt(pwd);
		// return  new String(Base64.decodeBase64(pwd));
	}
	public String encryptPassword(String pwd) throws IllegalBlockSizeException, BadPaddingException {
		return _encryptUtil.encrypt(pwd);
	}


	public String getValueFromToken(String token, String aKey ) {
		final Claims claims = getAllClaimsFromToken(token);
		String value = claims.get(aKey, String.class);
		return value;
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String aToken) {
		String token = aToken.replace("Bearer", "").replace(" ", "");
		return Jwts.parser().setSigningKey(getJWTSecret()).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	// public String generateToken(UserDetails userDetails) {
	// 	Map<String, Object> claims = new HashMap<>();
	// 	return doGenerateToken(claims, userDetails.getUsername());
	// }
	
	public String generateToken(UserDetails userDetails, Map<String, Object> claims) {		 
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY*1000)).signWith(SignatureAlgorithm.HS512, 
						getJWTSecret()).compact();				
	}

	public Boolean canTokenBeRefreshed(String token) {
		return (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
