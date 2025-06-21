package com.collaborative.editor.configuration;

import com.collaborative.editor.model.AccountSource;
import com.collaborative.editor.model.Role;
import com.collaborative.editor.model.User;
import com.collaborative.editor.service.interfaces.UserService;
import com.collaborative.editor.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();

            if ("google".equalsIgnoreCase(registrationId)) {
                handleGoogleLogin(oauthToken.getPrincipal());
            } else if ("github".equalsIgnoreCase(registrationId)) {
                handleGitHubLogin(oauthToken.getPrincipal());
            }
        }

        String jwtToken = generateJwtToken(authentication);
        response.sendRedirect("http://localhost:3000/oauth2/redirect?token=" + jwtToken);
    }

    private void handleGoogleLogin(OAuth2User principal) {
        String username = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        userService.findUserByEmail(email)
                .ifPresentOrElse(
                        user -> {
                        },
                        () -> createNewUser(email, username, Role.USER, AccountSource.GOOGLE)
                );
    }

    private void handleGitHubLogin(OAuth2User principal) {
        String username = principal.getAttribute("login");
        String email = principal.getAttribute("email");

        if (email == null) {
            email = username;
        }

        String finalEmail = email;
        userService.findUserByEmail(finalEmail)
                .ifPresentOrElse(
                        user -> {
                        },
                        () -> createNewUser(finalEmail, username, Role.USER, AccountSource.GITHUB)
                );
    }


    private void createNewUser(String email, String username, Role role, AccountSource source) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setRole(role);
        user.setPassword(generateRandomPassword());
        user.setSource(source);
        userService.createUser(user);
    }


    private String generateRandomPassword() {
        return new SecureRandom()
                .ints(16, 33, 126)
                .mapToObj(i -> String.valueOf((char) i))
                .reduce((a, b) -> a + b)
                .orElseThrow();
    }

    private String generateJwtToken(Authentication authentication) {
        return jwtUtil.generateToken(authentication);
    }
}
