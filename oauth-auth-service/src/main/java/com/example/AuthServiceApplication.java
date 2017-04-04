package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootApplication
public class AuthServiceApplication {

    @Bean
    CommandLineRunner demo(AccountRepository accountRepository) {
        return args ->
                Stream.of("jlong,spring", "dsyer,cloud", "pwebb,boot", "rwinch,security")
                        .map(tpl -> tpl.split(","))
                        .forEach(tpl -> accountRepository.save(new Account(tpl[0], tpl[1], true)));
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}

@Configuration
@EnableAuthorizationServer
class AuthServiceConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager ;

    AuthServiceConfiguration(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("html5")
                .secret("password")
                .authorizedGrantTypes("password")
                .scopes("openid");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager( this.authenticationManager);
    }
}

@Service
class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public AccountUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username)
                .map(account -> new User(account.getUsername(),
                        account.getPassword(), account.isActive(), account.isActive(), account.isActive(), account.isActive(),
                        AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")
                ))
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find the username " + username + "!"));
    }
}

interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
class Account {

    public Account(String username, String password, boolean active) {
        this.username = username;
        this.password = password;
        this.active = active;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String username, password;
    private boolean active;
}
