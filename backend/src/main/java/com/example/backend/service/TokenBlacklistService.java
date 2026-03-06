package com.example.backend.service;

import com.example.backend.entity.BlacklistedToken;
import com.example.backend.repository.BlacklistedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public TokenBlacklistService(BlacklistedTokenRepository blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Transactional
    public void blacklistToken(String token, Instant expiryDate) {
        if (!blacklistedTokenRepository.existsByToken(token)) {
            blacklistedTokenRepository.save(new BlacklistedToken(token, expiryDate));
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    /**
     * Periodically clean up expired blacklisted tokens (every hour).
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void purgeExpiredTokens() {
        blacklistedTokenRepository.deleteExpiredTokens(Instant.now());
    }
}
