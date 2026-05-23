package org.example.facturepro.service;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.dto.AuthRequest;
import org.example.facturepro.dto.AuthResponse;
import org.example.facturepro.dto.RegisterRequest;
import org.example.facturepro.entity.User;
import org.example.facturepro.repository.UserRepository;
import org.example.facturepro.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé: " + request.getEmail());
        }

        String role = (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) ? "ADMIN" : "USER";

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .role(role)
                .build();

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);
        return buildResponse(user, token);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = jwtTokenProvider.generateToken(user);
        return buildResponse(user, token);
    }

    private AuthResponse buildResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .id(user.getId())
                .build();
    }
}
