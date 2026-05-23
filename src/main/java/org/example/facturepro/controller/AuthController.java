package org.example.facturepro.controller;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.dto.AuthRequest;
import org.example.facturepro.dto.AuthResponse;
import org.example.facturepro.dto.RegisterRequest;
import org.example.facturepro.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
