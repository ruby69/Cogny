package io.dymatics.cogny.staff.firebase;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import io.dymatics.cogny.domain.model.FirebaseAuthenticationToken;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.persist.UserRepository;

@Component
//@Slf4j
public class FirebaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired private FirebaseAuth firebaseAuth;
    @Autowired private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> authentication) {
        return (FirebaseAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        final FirebaseAuthenticationToken authenticationToken = (FirebaseAuthenticationToken) authentication;

        ApiFuture<FirebaseToken> task = firebaseAuth.verifyIdTokenAsync(authenticationToken.getToken());
        try {
            FirebaseToken token = task.get();
            User currentUser = userRepository.findByUid(token.getUid());

            if(currentUser != null && currentUser.isUserLoginAuthorized()) return currentUser;

            User emptyUser = new User();
            emptyUser.setUserStatus(User.UserStatus.MEMBER);
            emptyUser.setUuid(token.getUid());
            emptyUser.setSignProviderByIssuer(token.getIssuer());
            emptyUser.setEmail(token.getEmail());
            emptyUser.setRole(User.Role.UNAUTHENTICATED);
            return emptyUser;

        } catch (InterruptedException | ExecutionException e) {
            throw new SessionAuthenticationException(e.getMessage());
        } 
    }
}
