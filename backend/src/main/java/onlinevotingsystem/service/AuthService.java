package onlinevotingsystem.service;

import onlinevotingsystem.audit.AuditService;
import onlinevotingsystem.dto.JwtResponse;
import onlinevotingsystem.dto.LoginRequest;
import onlinevotingsystem.dto.RefreshTokenRequest;
import onlinevotingsystem.dto.RegisterRequest;
import onlinevotingsystem.entity.Auth;
import onlinevotingsystem.exception.ConflictException;
import onlinevotingsystem.repository.AuthRepository;
import onlinevotingsystem.security.JwtBlacklistService;
import onlinevotingsystem.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final JwtBlacklistService blacklistService;
    private final AuditService auditService;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider, JwtBlacklistService blacklistService, AuditService auditService) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.blacklistService = blacklistService;
        this.auditService = auditService;
    }

    @Transactional
    public Auth register(RegisterRequest req) {
        authRepository.findByUsername(req.getUsername()).ifPresent(u -> { throw new ConflictException("Username already exists"); });
        authRepository.findByEmail(req.getEmail()).ifPresent(u -> { throw new ConflictException("Email already exists"); });
        Auth auth = new Auth();
        auth.setUsername(req.getUsername());
        auth.setEmail(req.getEmail());
        auth.setPassword(passwordEncoder.encode(req.getPassword()));
        auth.setRole(req.getRole());
        Auth saved = authRepository.save(auth);
        auditService.record(saved.getId(), "REGISTER", "Auth", saved.getId(), null);
        return saved;
    }

    public JwtResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String access = tokenProvider.generateAccessToken(authentication);
        String refresh = tokenProvider.generateRefreshToken(authentication);
        authRepository.findByUsername(req.getUsername()).ifPresent(a ->
                auditService.record(a.getId(), "LOGIN", "Auth", a.getId(), null));
        return new JwtResponse(access, refresh);
    }

    public JwtResponse refresh(RefreshTokenRequest req) {
        if (!tokenProvider.validate(req.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String username = tokenProvider.getUsername(req.getRefreshToken());
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
        String access = tokenProvider.generateAccessToken(authentication);
        return new JwtResponse(access, req.getRefreshToken());
    }

    public void logout(String token) {
        if (token == null || !token.startsWith("Bearer ")) return;
        String raw = token.substring(7);
        if (tokenProvider.validate(raw)) {
            String jti = tokenProvider.getJti(raw);
            // blacklist for the remaining lifetime; for simplicity, 15m
            blacklistService.blacklist(jti, System.currentTimeMillis() + 15 * 60 * 1000);
        }
    }

    public Auth profile(String username) {
        return authRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
