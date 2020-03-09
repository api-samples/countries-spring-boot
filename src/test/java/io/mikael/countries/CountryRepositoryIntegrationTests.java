package io.mikael.countries;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.FeignException;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class CountryRepositoryIntegrationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ObjectMapper objectMapper;

    private CountriesRestFacade backend;

    public interface CountriesRestFacade {
        @RequestLine("GET /countries/{code}")
        Country country(@Param("code") String code);
    }

    @Before
    public void init() {
        this.backend = Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .encoder(new JacksonEncoder(objectMapper))
                .target(CountriesRestFacade.class,
                        String.format("http://127.0.0.1:%s", port));
    }

    @Test
    public void findFinland() {
        final var finland = backend.country("FI");
        assertEquals("Finland", finland.name);
    }

    @Test
    public void findSweden() {
        final var sweden = backend.country("SE");
        assertEquals("Sweden", sweden.name);
    }

    @Test(expected = FeignException.class)
    public void findInvalid() {
        try {
            backend.country("BLAH");
        } catch (final FeignException e) {
            assertEquals("invalid country should not be found",
                    HttpStatus.NOT_FOUND.value(), e.status());
            throw e;
        }
    }

}
