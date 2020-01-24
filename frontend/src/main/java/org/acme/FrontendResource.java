package org.acme;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/frontend")
public class FrontendResource {
    @Inject
    @RestClient
    StudentRestClient student;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return student.hello();
    }

    @Retry(maxRetries = 4, delay = 1000)
    @Fallback(fallbackMethod = "listStudentsFallback")
    // @Timeout
    @CircuitBreaker(
        requestVolumeThreshold = 4,
        failureRatio = 0.5,
        delay = 10000,
        successThreshold = 2
    )
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public List<String> listStudents() {
        List<String> students = student.listStudents();
        return students;
    }

    public List<String> listStudentsFallback() {
        // Return top students across all classes
        List<String> students = Arrays.asList("Smart Sam", "Genius Gabby", "A-Student Angie", "Intelligent Irene");
        return students;
    }
}