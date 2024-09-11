package goldenage.delfis.auth.controller;

import goldenage.delfis.auth.model.LoginRequest;
import goldenage.delfis.apiusersql.model.UserRole;
import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.service.AppUserService;
import goldenage.delfis.apiusersql.service.UserRoleService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    public final SecretKey secretKey;
    private final AppUserService appUserService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;


    public AuthController(AppUserService appUserService, UserRoleService userRoleService,
                          PasswordEncoder passwordEncoder, SecretKey secretKey) {
        this.appUserService = appUserService;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
        this.secretKey = secretKey;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        AppUser appUser = appUserService.getAppUserByUsername(loginRequest.getUsername());

        if (appUser != null && passwordEncoder.matches(loginRequest.getPassword(), appUser.getPassword())) {
            UserRole userRole = userRoleService.getUserRoleById(appUser.getFkUserRoleId());
            try {
                String token = Jwts.builder()
                        .setSubject(loginRequest.getUsername())
                        .claim("user_role", userRole.getName())
                        .setExpiration(new Date(System.currentTimeMillis() + 86_400_000))
                        .signWith(secretKey, SignatureAlgorithm.HS512)
                        .compact();

                logger.info("Token gerado: {}", token);
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", "Bearer " + token));
            } catch (Exception e) {
                logger.error("Erro ao gerar o token JWT: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar o token JWT: " + e);
            }
        } else {
            logger.error("Credenciais inválidas para: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
    }

    @GetMapping("/api/auth/keep-alive")
    public ResponseEntity<?> keepAlive() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
