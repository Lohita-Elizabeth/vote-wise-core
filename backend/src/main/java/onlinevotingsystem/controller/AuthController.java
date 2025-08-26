package onlinevotingsystem.controller;

import jakarta.validation.Valid;
import onlinevotingsystem.dto.JwtResponse;
import onlinevotingsystem.dto.LoginRequest;
import onlinevotingsystem.dto.RefreshTokenRequest;
import onlinevotingsystem.dto.RegisterRequest;
import onlinevotingsystem.entity.Auth;
import onlinevotingsystem.repository.AuthRepository;
import onlinevotingsystem.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthRepository authRepository;

    public AuthController(AuthService authService, AuthRepository authRepository) {
        this.authService = authService;
        this.authRepository = authRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        Auth saved = authService.register(req);
        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "username", saved.getUsername(),
                "email", saved.getEmail(),
                "role", saved.getRole()
        ));
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return authService.refresh(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.status(401).build();
        Auth auth = authService.profile(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "id", auth.getId(),
                "username", auth.getUsername(),
                "email", auth.getEmail(),
                "role", auth.getRole()
        ));
    }
}
