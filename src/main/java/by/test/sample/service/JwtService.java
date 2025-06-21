package by.test.sample.service;

public interface JwtService {

    String generateToken(Long userId);

    Long extractUserId(String token);

}
