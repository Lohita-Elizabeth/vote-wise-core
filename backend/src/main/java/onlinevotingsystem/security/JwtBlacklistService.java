package onlinevotingsystem.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtBlacklistService {
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String jti, long expiresAtMillis) {
        blacklist.put(jti, expiresAtMillis);
    }

    public boolean isBlacklisted(String jti) {
        Long exp = blacklist.get(jti);
        if (exp == null) return false;
        if (Instant.now().toEpochMilli() > exp) {
            blacklist.remove(jti);
            return false;
        }
        return true;
    }
}
