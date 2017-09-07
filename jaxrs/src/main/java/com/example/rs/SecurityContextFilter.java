package com.example.rs;

import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * Installs a {@link SecurityContext} that in turn delegates
 * to Spring Security's {@link SecurityContextHolder#getContext()}.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityContextFilter implements ContainerRequestFilter {

    @Context
    UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setSecurityContext(new SpringSecuritySecurityContext(uriInfo));
    }

    static class SpringSecuritySecurityContext
            implements SecurityContext {

        private final UriInfo uriInfo;

        private SpringSecuritySecurityContext(UriInfo uriInfo) {
            this.uriInfo = uriInfo;
        }

        private org.springframework.security.core.context.SecurityContext springSecurityContext() {
            return SecurityContextHolder.getContext();
        }

        @Override
        public Principal getUserPrincipal() {
            String name = springSecurityContext().getAuthentication().getName();
            return () -> name;
        }

        @Override
        public boolean isUserInRole(String role) {
            return this
                    .springSecurityContext()
                    .getAuthentication()
                    .getAuthorities()
                    .stream()
                    .anyMatch(ga -> ga.getAuthority().toLowerCase().contains(role));
        }

        @Override
        public boolean isSecure() {
            return uriInfo
                    .getAbsolutePath().toASCIIString().toLowerCase()
                    .startsWith("https");
        }

        @Override
        public String getAuthenticationScheme() {
            return "spring-security";
        }
    }
}
