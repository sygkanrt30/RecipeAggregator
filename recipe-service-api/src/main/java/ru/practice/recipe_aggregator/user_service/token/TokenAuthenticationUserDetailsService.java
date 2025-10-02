package ru.practice.recipe_aggregator.user_service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;

@RequiredArgsConstructor
public class TokenAuthenticationUserDetailsService implements
        AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken)
            throws UsernameNotFoundException {
        if (authenticationToken.getPrincipal() instanceof Token token) {
            return userRepository.findByUsername(token.subject()).orElseThrow(
                    () ->  new UsernameNotFoundException("Username " + token.subject() + " not found")
            );
        }
        throw new UsernameNotFoundException("Principal must be of type Token");
    }
}
