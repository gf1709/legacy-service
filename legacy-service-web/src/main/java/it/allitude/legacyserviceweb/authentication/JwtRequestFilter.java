package it.allitude.legacyserviceweb.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import it.allitude.legacyserviceweb.models.JSession;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, ExpiredJwtException {

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null, session = null, jwtToken = null, password = null, targaCassa = null, ambiente = null, terminaleApplicativo = null;
        String libreriaTemporanea = null, sib_Directory = null, descrizione_breve_CR = null, cartellaAmbienteDati = null, descrizione_2_CR = null;
        String cab = null, libreria4 = null, libreria2 = null, libreria3 = null, libreriaRete = null, documentale_SIB2000 = null, documentale_InfoBanking = null;
        String libreriaCestino = null, cartellaFileTransfer = null, libreriaDatiBanca = null, ambienteDatiStorici = null, server_SID2000 = null;
        String abi_cin = null, ambienteDati = null, descrizione_1_CR = null, descrizioneFilialeBreve = null, abi = null, cab_senza_cin = null;
        String libreria1 = null, libreriaProcedure = null, descrizioneFiliale = null, codiceFiliale = null;

        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the
        // Token
        try {
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                session = jwtTokenUtil.getSessionFromToken(jwtToken);
                password = jwtTokenUtil.getPasswordFromToken(jwtToken);

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
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("Unable to get JWT Token or JWT Token is invalid");
            response.setContentType("application/json");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(error));
        } catch (ExpiredJwtException expiredJwtException) {
            logger.error("JWT Token has expired");
            response.setContentType("application/json");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            Map<String, String> error = new HashMap<>();
            error.put("error_message", "Token expired, please logout and login again: " + expiredJwtException.getMessage());
            response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(error));
            // new ObjectMapper().writeValue(response.getOutputStream(),error);
            // qualcuno ha fatto cosi throw new CredentialsExpiredException("Expired jwt credentials ", expiredJwtException);
        }
    }

}
