package com.company.tathminiv2.rest.config;

import io.jmix.core.security.AuthorizedUrlsProvider;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class GreetingAuthorizedUrlsProvider implements AuthorizedUrlsProvider {

    @Override
    public Collection<String> getAuthenticatedUrlPatterns() {
        return List.of();
    }

    @Override
    public Collection<String> getAnonymousUrlPatterns() {
        return List.of("/users/**", "/api/**");
    }
}