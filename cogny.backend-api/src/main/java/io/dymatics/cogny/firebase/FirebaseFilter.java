package io.dymatics.cogny.firebase;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class FirebaseFilter extends OncePerRequestFilter {
    private static String HEADER_NAME = "X-Authorization-Firebase";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HEADER_NAME);
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);

        } else {
            FirebaseToken decodedToken;
            try {
                decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(token).get();
//                Authentication auth = new FirebaseAuthenticationToken(decodedToken.getUid(), decodedToken);
//                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
