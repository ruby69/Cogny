package io.dymatics.cogny.firebase;

public class FirebaseAuthenticationToken { //extends AbstractAuthenticationToken {
//
//    private static final long serialVersionUID = -1869548136546750302L;
//    private final Object principal;
//    private Object credentials;
//
//    public FirebaseAuthenticationToken(Object principal, Object credentials) {
//        super(null);
//        this.principal = principal;
//        this.credentials = credentials;
//        setAuthenticated(false);
//    }
//
//    public FirebaseAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
//        super(authorities);
//        this.principal = principal;
//        this.credentials = credentials;
//        super.setAuthenticated(true);
//    }
//
//    @Override
//    public Object getCredentials() {
//        return this.credentials;
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return this.principal;
//    }
//
//    @Override
//    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
//        if (isAuthenticated) {
//            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
//        }
//        super.setAuthenticated(false);
//    }
//
//    @Override
//    public void eraseCredentials() {
//        super.eraseCredentials();
//        credentials = null;
//    }
}
