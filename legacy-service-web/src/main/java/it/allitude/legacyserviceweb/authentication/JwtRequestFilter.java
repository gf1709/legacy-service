package it.allitude.legacyserviceweb.authentication;

import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import it.allitude.legacyserviceweb.models.JSession;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String session = null;
		String jwtToken = null;
		String password = null;
		String targaCassa = null;
		String ambiente = null;
		String terminaleApplicativo = null;

		String libreriaTemporanea = null;
		String sib_Directory = null;
		String descrizione_breve_CR = null;
		String cartellaAmbienteDati = null;
		String descrizione_2_CR = null;
		String cab = null;
		String libreria4 = null;
		String libreria2 = null;
		String libreria3 = null;
		String libreriaRete = null;
		String documentale_SIB2000 = null;
		String documentale_InfoBanking = null;
		String libreriaCestino = null;
		String cartellaFileTransfer = null;
		String libreriaDatiBanca = null;
		String ambienteDatiStorici = null;
		String server_SID2000 = null;
		String abi_cin = null;
		String ambienteDati = null;
		String descrizione_1_CR = null;
		String descrizioneFilialeBreve = null;
		String abi = null;
		String cab_senza_cin = null;
		String libreria1 = null;
		String libreriaProcedure = null;
		String descrizioneFiliale = null;
		String codiceFiliale = null;

		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the
		// Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				session = jwtTokenUtil.getSessionFromToken(jwtToken);
				try {
					password = jwtTokenUtil.getPasswordFromToken(jwtToken);
				} catch (IllegalBlockSizeException | BadPaddingException e) {					
					e.printStackTrace();
				}

				targaCassa = jwtTokenUtil.getValueFromToken(jwtToken, "targaCassa");
				ambiente = jwtTokenUtil.getValueFromToken(jwtToken, "ambiente");

				terminaleApplicativo = jwtTokenUtil.getValueFromToken(jwtToken, "terminaleApplicativo");

				libreriaTemporanea = jwtTokenUtil.getValueFromToken(jwtToken, "libreriaTemporanea");
				sib_Directory = jwtTokenUtil.getValueFromToken(jwtToken, "sib_Directory");
				descrizione_breve_CR = jwtTokenUtil.getValueFromToken(jwtToken, "descrizione_breve_CR");
				cartellaAmbienteDati = jwtTokenUtil.getValueFromToken(jwtToken, "cartellaAmbienteDati");
				descrizione_2_CR = jwtTokenUtil.getValueFromToken(jwtToken, "descrizione_2_CR");
				cab = jwtTokenUtil.getValueFromToken(jwtToken, "cab");
				libreria4 = jwtTokenUtil.getValueFromToken(jwtToken, "libreria4");
				libreria2 = jwtTokenUtil.getValueFromToken(jwtToken, "libreria2");
				libreria3 = jwtTokenUtil.getValueFromToken(jwtToken, "libreria3");
				libreriaRete = jwtTokenUtil.getValueFromToken(jwtToken, "libreriaRete");
				documentale_SIB2000 = jwtTokenUtil.getValueFromToken(jwtToken, "documentale_SIB2000");
				documentale_InfoBanking = jwtTokenUtil.getValueFromToken(jwtToken, "documentale_InfoBanking");
				libreriaCestino = jwtTokenUtil.getValueFromToken(jwtToken, "libreriaCestino");
				cartellaFileTransfer = jwtTokenUtil.getValueFromToken(jwtToken, "cartellaFileTransfer");
				libreriaDatiBanca = jwtTokenUtil.getValueFromToken(jwtToken, "libreriaDatiBanca");
				ambienteDatiStorici = jwtTokenUtil.getValueFromToken(jwtToken, "ambienteDatiStorici");
				server_SID2000 = jwtTokenUtil.getValueFromToken(jwtToken, "server_SID2000");
				abi_cin = jwtTokenUtil.getValueFromToken(jwtToken, "abi_cin");
				ambienteDati = jwtTokenUtil.getValueFromToken(jwtToken, "ambienteDati");
				descrizione_1_CR = jwtTokenUtil.getValueFromToken(jwtToken, "descrizione_1_CR");
				descrizioneFilialeBreve = jwtTokenUtil.getValueFromToken(jwtToken, "descrizioneFilialeBreve");
				abi = jwtTokenUtil.getValueFromToken(jwtToken, "abi");
				cab_senza_cin = jwtTokenUtil.getValueFromToken(jwtToken, "cab_senza_cin");
				libreria1 = jwtTokenUtil.getValueFromToken(jwtToken, "libreria1");
				libreriaProcedure = jwtTokenUtil.getValueFromToken(jwtToken, "libreriaProcedure");
				descrizioneFiliale = jwtTokenUtil.getValueFromToken(jwtToken, "descrizioneFiliale");
				codiceFiliale = jwtTokenUtil.getValueFromToken(jwtToken, "codiceFiliale");

			} catch (IllegalArgumentException e) {
				logger.warn("Unable to get JWT Token");
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			JSession details = new JSession();
			details.setJwt(jwtToken);
			details.setTerminal(session);
			details.setUser(username);

			details.setTargaCassa(targaCassa);
			details.setAmbiente(ambiente);
			details.setTerminaleApplicativo(terminaleApplicativo);

			details.setLibreriaTemporanea(libreriaTemporanea);
			details.setSib_Directory(sib_Directory);
			details.setDescrizione_breve_CR(descrizione_breve_CR);
			details.setCartellaAmbienteDati(cartellaAmbienteDati);
			details.setDescrizione_2_CR(descrizione_2_CR);
			details.setCab(cab);
			details.setLibreria4(libreria4);
			details.setLibreria2(libreria2);
			details.setLibreria3(libreria3);
			details.setLibreriaRete(libreriaRete);
			details.setDocumentale_SIB2000(documentale_SIB2000);
			details.setDocumentale_InfoBanking(documentale_InfoBanking);
			details.setLibreriaCestino(libreriaCestino);
			details.setCartellaFileTransfer(cartellaFileTransfer);
			details.setLibreriaDatiBanca(libreriaDatiBanca);
			details.setAmbienteDatiStorici(ambienteDatiStorici);
			details.setServer_SID2000(server_SID2000);
			details.setAbi_cin(abi_cin);
			details.setAmbienteDati(ambienteDati);
			details.setDescrizione_1_CR(descrizione_1_CR);
			details.setDescrizioneFilialeBreve(descrizioneFilialeBreve);
			details.setAbi(abi);
			details.setCab_senza_cin(cab_senza_cin);
			details.setLibreria1(libreria1);
			details.setLibreriaProcedure(libreriaProcedure);
			details.setDescrizioneFiliale(descrizioneFiliale);
			details.setCodiceFiliale(codiceFiliale);
			
			// logger.info("--------------------->>");
			// logger.info("username is: <" + username + ">");
			// logger.info("password is: <" + password + ">");

			final UserDetails userDetails = new User(username, password, new ArrayList<>());
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(details);
				// .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// After setting the Authentication in the context, we specify that the current
				// user is authenticated. So it passes the Spring Security
				// Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

}
