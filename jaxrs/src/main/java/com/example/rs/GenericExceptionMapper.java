package com.example.rs;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Slf4j
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        log.error("error!", exception);
        return Response
                .serverError()
                .entity(exception.getMessage())
                .build();
    }

}