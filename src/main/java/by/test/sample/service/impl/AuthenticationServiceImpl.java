package by.test.sample.service.impl;

import by.test.sample.dto.LoginRequest;
import by.test.sample.dto.TokenResponse;
import by.test.sample.exception.InvalidPasswordException;
import by.test.sample.exception.UserNotFoundException;
import by.test.sample.repository.UserRepository;
import by.test.sample.service.AuthenticationService;
import by.test.sample.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    @Override
    public TokenResponse authenticate(LoginRequest request) {
        var user = userRepository.findIdentity(request.identity())
                .orElseThrow(UserNotFoundException::new);
        if (!user.getPassword().equals(request.password())) {
            throw new InvalidPasswordException();
        }
        return new TokenResponse(jwtService.generateToken(user.getId()));
    }
}
