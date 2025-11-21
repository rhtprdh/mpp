package com.soms.auth_service.controller;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.soms.auth_service.dto.AuthRequest;
import com.soms.auth_service.dto.AuthResponse;
import com.soms.auth_service.dto.RegisterRequest;
import com.soms.auth_service.model.UserEntity;
import com.soms.auth_service.security.JwtUtil;
import com.soms.auth_service.service.AuthService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        UserEntity u = authService.register(req.getUsername(), req.getPassword());
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", u.getId());
        resp.put("username", u.getUsername());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());

        Authentication auth = authenticationManager.authenticate(token);

        UserDetails ud = (UserDetails) auth.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", ud.getAuthorities());

        String jwt = jwtUtil.generateToken(ud.getUsername(), claims);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing Bearer token");
        }
        String token = header.substring(7);

        if (!jwtUtil.validate(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        return ResponseEntity.ok(jwtUtil.parseToken(token).getBody());
    }
}
