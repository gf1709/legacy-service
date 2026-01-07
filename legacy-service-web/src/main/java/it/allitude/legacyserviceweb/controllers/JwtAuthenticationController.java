package it.allitude.legacyserviceweb.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.allitude.legacyserviceweb.DTOs.JwtRequestDTO;
import it.allitude.legacyserviceweb.DTOs.JwtResponseDTO;
import it.allitude.legacyserviceweb.authentication.JwtTokenUtil;
import it.allitude.legacyserviceweb.db.ConnectionService;
import it.allitude.legacyserviceweb.models.AppConfig;

@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping({ "/api" })

public class JwtAuthenticationController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AuthenticationManager _authenticationManager;

	@Autowired
	private JwtTokenUtil _jwtTokenUtil;

	@Autowired
	private ConnectionService _connectionService;

	@Autowired
	AppConfig _cfg;

	@PostMapping("/authenticate")
	public ResponseEntity<?> generateAuthenticationToken(@RequestBody JwtRequestDTO authenticationRequest)
			throws Exception {
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		// final UserDetails userDetails =
		// userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final UserDetails userDetails = new User(authenticationRequest.getUsername(),
				authenticationRequest.getPassword(), new ArrayList<>());

		// Aggiungo i dati di sessione
		String jdbc = String.format("jdbc:as400://%s;user=%s;password=%s;prompt=false;", _cfg.getISeriesName(), authenticationRequest.getUsername(),
				authenticationRequest.getPassword());

		String ze2amb = null;
		String ze2wid = null;
		String ze2lb1 = null;
		String ze2lb2 = null;
		String ze2lb3 = null;
		String ze2lb4 = null;

		// Con questa connessione verifico che l'utenza e la password siano corretti
		Connection con = DriverManager.getConnection(jdbc);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM LIBFCCFG.ZE2  WHERE ZE2DEV ='%s'",
				authenticationRequest.getSession().toUpperCase()));

		while (rs.next()) {
			ze2amb = rs.getString("ZE2AMB").trim();
			ze2wid = rs.getString("ZE2WID").trim();
			ze2lb1 = rs.getString("ZE2LB1").trim();
			ze2lb2 = rs.getString("ZE2LB2").trim();
			ze2lb3 = rs.getString("ZE2LB3").trim();
			ze2lb4 = rs.getString("ZE2LB4").trim();
		}
		rs.close();
		if (ze2amb == null || ze2amb.length() < 2)
			return ResponseEntity.ok("Targa Cassa/Ambiente non trovato per la sessione "
					+ authenticationRequest.getSession().toUpperCase());
		if (ze2wid == null || ze2wid.length() < 2)
			return ResponseEntity.ok("Terminale applicativo non trovato per la sessione "
					+ authenticationRequest.getSession().toUpperCase());
		if (ze2lb1 == null || ze2lb1.length() < 2)
			return ResponseEntity.ok("Libreria banca non trovata per la sessione "
					+ authenticationRequest.getSession().toUpperCase());

		ze2amb = ze2amb.trim().toUpperCase();
		ze2wid = ze2wid.trim().toUpperCase();
		ze2lb1 = ze2lb1.trim().toUpperCase();

		String targaCassa = ze2amb.substring(2, 4);
		String ambiente = ze2amb.substring(4, 5);
		String terminaleApplicativo = ze2wid;

		Map<String, Object> claims = new HashMap<>();
		claims.put("session", authenticationRequest.getSession().toUpperCase());
		String pwd = authenticationRequest.getPassword();
		
		claims.put("password", _jwtTokenUtil.encryptPassword(pwd));

		claims.put("targaCassa", targaCassa);
		claims.put("ambiente", ambiente);
		claims.put("terminaleApplicativo", terminaleApplicativo);

		claims.put("ambienteDati", ze2amb);
		claims.put("libreria1", ze2lb1);
		claims.put("libreria2", ze2lb2);
		claims.put("libreria3", ze2lb3);
		claims.put("libreria4", ze2lb4);

		stmt = con.createStatement();
		rs = stmt.executeQuery(
				String.format("SELECT * FROM LIBFCCFG.ZE1 WHERE ZE1TAC='%s' AND ZE1FPT='%s'", targaCassa, ambiente));
		while (rs.next()) {
			claims.put("libreriaTemporanea", rs.getString("ZE1TMP").trim());
			claims.put("ambienteDatiStorici", rs.getString("ZE1STO").trim());
			claims.put("libreriaRete", rs.getString("ZE1RET").trim());
			claims.put("cartellaAmbienteDati", rs.getString("ZE1FLR").trim());
			claims.put("cartellaFileTransfer", rs.getString("ZE1FLP").trim());
			claims.put("sib_Directory", rs.getString("ZE1FF1").trim());
			claims.put("server_SID2000", rs.getString("ZE1FF2").trim());
			claims.put("documentale_InfoBanking", rs.getString("ZE1FF3").trim());
			claims.put("documentale_SIB2000", rs.getString("ZE1FF4").trim());
			claims.put("libreriaCestino", rs.getString("ZE1LL1").trim().trim());
			claims.put("libreriaProcedure", rs.getString("ZE1LL2").trim().trim());
			claims.put("libreriaDatiBanca", rs.getString("ZE1LL4").trim().trim());
		}
		rs.close();

		// Controllo utente
		stmt = con.createStatement();
		String sqlCmd = String.format(
				"SELECT Z11SER, Z11TAB, Z11ELE, Z11FLD FROM %s.Z11 WHERE Z11SER = 'Z01' AND Z11TAB = 'ZZUSR' AND Z11ELE = '%s'",
				ze2amb, authenticationRequest.getUsername().toUpperCase());	
		rs = stmt.executeQuery(sqlCmd);
		if (!rs.next())
		{	
			logger.error("Errore Utente non presente in Tabella Z01, ZZUSR con SQL: " + sqlCmd);
			return ResponseEntity.ok("Errore Utente non presente in Tabella Z01, ZZUSR");
		}
		rs.close();

		String codiceFiliale = null;
		// Controllo terminale
		stmt = con.createStatement();
		sqlCmd = String.format(
				"SELECT Z11SER, Z11TAB, Z11ELE, Z11FLD FROM %s.Z11 WHERE Z11SER = 'Z01' AND Z11TAB = 'ZZWID'AND Z11ELE = '%s'",
				ze2amb, terminaleApplicativo);
		rs = stmt.executeQuery(sqlCmd);
		if (!rs.next())
		{
			logger.error("Errore Terminale non presente in Tabella Z01, ZZWID con SQL: " + sqlCmd);
			return ResponseEntity.ok("Errore Terminale non presente in Tabella Z01, ZZWID");	
		}
		codiceFiliale = rs.getString("Z11FLD").substring(3, 6);
		claims.put("codiceFiliale", codiceFiliale);
		rs.close();

		// Dati CR
		stmt = con.createStatement();
		sqlCmd = String.format(
				"SELECT Z80IN1, Z80IN2, Z80INR FROM %s.Z80 WHERE Z80SER = 'A01' AND  Z80RAP = '99999999'", ze2amb);	
		rs = stmt.executeQuery(sqlCmd);
		if (!rs.next())
			{	logger.error("Errore Non trovati dati relativi al CR in Z80 con SQL: " + sqlCmd);		
				return ResponseEntity.ok("Errore Terminale non presente in Tabella Z01, ZZWID");
			}
		claims.put("descrizione_1_CR", rs.getString("Z80IN1").trim());
		claims.put("descrizione_2_CR", rs.getString("Z80IN2").trim());
		claims.put("descrizione_breve_CR", rs.getString("Z80INR").trim());
		rs.close();

		// Codice Abi
		stmt = con.createStatement();
		sqlCmd = String.format(
				"SELECT A10CAG,A10TCA,A10CCA FROM %s.A10 WHERE A10CAG = '99999999' AND A10TCA ='200'", ze2amb);	
		rs = stmt.executeQuery(sqlCmd);
		if (!rs.next())
		{
			logger.error("Errore Codice Abi non trovato in A10 con SQL: " + sqlCmd);
			return ResponseEntity.ok("Codice Abi non trovato");
		}
		claims.put("abi", rs.getString("A10CCA").substring(0, 5).trim());
		claims.put("abi_cin", rs.getString("A10CCA").trim());
		rs.close();

		// Filiale : rapporto
		stmt = con.createStatement();
		int codiceFilialeNumerico = Integer.parseInt(codiceFiliale);
		sqlCmd = String.format(
				"SELECT A01CAG FROM %s.A01 WHERE A01FIL = %d AND A01TIC >= '32000' AND A01TIC <= '32999' AND A01SCP NOT IN ('A', 'I')",
				ze2amb, codiceFilialeNumerico);
		rs = stmt.executeQuery(sqlCmd);
		if (!rs.next())
		{
			logger.error("Errore Rapporto filiale non trovato in A01 con SQL: " + sqlCmd);
			return ResponseEntity.ok("Rapporto filiale non trovato");
		}
		String rapportoFiliale = rs.getString("A01CAG").trim();
		rs.close();

		if (!codiceFiliale.substring(0, 2).equals("99")) {
			stmt = con.createStatement();
			sqlCmd = String.format(
					"SELECT Z80IN1, Z80INR FROM %s.Z80 WHERE Z80SER = 'A01' AND Z80RAP = '%s'", ze2amb, rapportoFiliale);
			rs = stmt.executeQuery(sqlCmd);
			if (!rs.next())
			{
				logger.error("Errore Non trovati dati relativi alla Filiale in Z80 con SQL: " + sqlCmd);
				return ResponseEntity.ok("Errore Non trovati dati relativi alla Filiale in Z80");
			}
			claims.put("descrizioneFiliale", rs.getString("Z80IN1").trim());
			claims.put("descrizioneFilialeBreve", rs.getString("Z80INR").trim());
			rs.close();

			stmt = con.createStatement();
			sqlCmd = String.format(
					"SELECT A10CAG,A10TCA,A10CCA FROM %s.A10 WHERE A10CAG = '%s' AND A10TCA ='201'", ze2amb,
					rapportoFiliale);	
			rs = stmt.executeQuery(sqlCmd);
			if (!rs.next())
			{
				logger.error("Errore Non trovati dati relativi alla Filiale in A10 con SQL: " + sqlCmd);
				return ResponseEntity.ok("Errore Non trovati dati relativi alla Filiale in A10");
			}
			claims.put("cab", rs.getString("A10CCA").substring(0, 5));
			claims.put("cab_senza_cin", rs.getString("A10CCA"));
			rs.close();

		}
		con.close();
		final String token = _jwtTokenUtil.generateToken(userDetails, claims);		
		return ResponseEntity.ok(new JwtResponseDTO(token));
	}

	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
		try {
			_authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@GetMapping("/logout")
	public void logout() throws Exception {
		_connectionService.logout();		
	}
}
