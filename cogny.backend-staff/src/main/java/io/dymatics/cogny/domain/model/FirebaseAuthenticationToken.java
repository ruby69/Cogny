package io.dymatics.cogny.domain.model;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class FirebaseAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = -800860110696696608L;
    private final String token;

    public FirebaseAuthenticationToken(final String token) {
        super(null, null);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}