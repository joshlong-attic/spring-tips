package com.example.rs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    UserDetailsService userDetailsService() {
        List<UserDetails> details = Arrays.asList(
                user("jlong", "password", "USER"),
                user("rwinch", "password", "USER", "ADMIN"));
        return new InMemoryUserDetailsManager(details);
    }

    private UserDetails user(String u, String pw, String... authorities) {
        return User.withUsername(u).password(pw).authorities(authorities).build();
    }

}
