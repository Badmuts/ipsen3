package nl.hsleiden.ipsen3.service;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.basic.BasicCredentials;
import nl.hsleiden.ipsen3.core.User;
import nl.hsleiden.ipsen3.dao.UserDAO;

public class AuthorizationService implements Authenticator<BasicCredentials, User>, Authorizer<User> {

    private final UserDAO userDAO;

    public AuthorizationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> authenticate(BasicCredentials credentials)
        throws AuthenticationException {
        User user = userDAO.getByEmail(credentials.getUsername());
        if (user != null && user.getPassword().equals(credentials.getPassword())) {
            return Optional.of(user);
        }
        return Optional.absent();
    }

    public boolean authorize(User principal, String role) {
        return false;
    }
}
