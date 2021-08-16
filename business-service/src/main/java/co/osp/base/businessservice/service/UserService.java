package co.osp.base.businessservice.service;

import co.osp.base.businessservice.entity.User;
import co.osp.base.businessservice.dto.UserDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

/**
 * Service Interface for managing {@link User}.
 */
public interface UserService {

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    List<String> getAuthorities();
    /**
     * Returns the user from an OAuth 2.0 login or resource server with JWT.
     * Synchronizes the user in the local repository.
     *
     * @param authToken the authentication token.
     * @return the user from the authentication.
     */
     UserDTO getUserFromAuthentication(AbstractAuthenticationToken authToken);

}
