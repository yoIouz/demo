package by.test.sample.security;

import by.test.sample.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static by.test.sample.utils.ApplicationConstants.AUTHORIZATION_HEADER;
import static by.test.sample.utils.ApplicationConstants.SERVER_ERROR_MESSAGE;
import static by.test.sample.utils.ApplicationConstants.TOKEN_BEARER;
import static by.test.sample.utils.ApplicationConstants.TOKEN_ERROR_MESSAGE;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = this.extractToken(request);
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Long userId = jwtService.extractUserId(token);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            this.setResponse(response, SC_UNAUTHORIZED, TOKEN_ERROR_MESSAGE);
            return;
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            this.setResponse(response, SC_INTERNAL_SERVER_ERROR, SERVER_ERROR_MESSAGE);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        return (bearer != null && bearer.startsWith(TOKEN_BEARER)) ?
                bearer.substring(7) : null;
    }

    private void setResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
        response.getWriter().flush();
    }
}
