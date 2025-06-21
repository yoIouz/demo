package by.test.sample.service;

import by.test.sample.dto.LoginRequest;
import by.test.sample.dto.TokenResponse;

public interface AuthenticationService {

    TokenResponse authenticate(LoginRequest request);
}
