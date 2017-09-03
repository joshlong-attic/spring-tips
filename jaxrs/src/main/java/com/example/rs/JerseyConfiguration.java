package com.example.rs;

import com.example.rs.customers.CustomerRepository;
import com.example.rs.customers.CustomerResource;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;

@Configuration
public class JerseyConfiguration {


    @Configuration
    public static class JerseySecurityConfiguration {

        @Bean
        SecurityContext springSecurityContext() {
            return proxy(SecurityContextHolder::getContext, SecurityContext.class);
        }

        @Bean
        Authentication authentication(SecurityContext sc) {
            return proxy(sc::getAuthentication, Authentication.class);
        }

        private <T> T proxy(ObjectFactory<T> oft, Class<T> ct) {
            ProxyFactoryBean pfb = new ProxyFactoryBean();
            pfb.addAdvice((MethodInterceptor) methodInvocation -> {
                T t = oft.getObject();
                Method method = methodInvocation.getMethod();
                return method.invoke(t, methodInvocation.getArguments());
            });
            pfb.addInterface(ct);
            return ct.cast(pfb.getObject());
        }
    }

    @Bean
    ResourceConfig resourceConfig(GenericExceptionMapper exceptionMapper,
                                  CustomerResource customerResource) {
        ResourceConfig rc = new ResourceConfig();
        rc.register(exceptionMapper);
        rc.register(customerResource);
        return rc;
    }

    @Bean
    GenericExceptionMapper exceptionMapper() {
        return new GenericExceptionMapper();
    }

    @Bean
    CustomerResource customerResource(CustomerRepository customerRepository, Authentication authentication) {
        return new CustomerResource(customerRepository, authentication);
    }
}
