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
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class CountryRepositoryIntegrationTests {

    @Autowired
    private EmbeddedWebApplicationContext server;

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
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .target(CountriesRestFacade.class,
                        String.format("http://127.0.0.1:%s",
                                server.getEmbeddedServletContainer().getPort()));
    }

    @Test
    public void findFinland() {
        final Country finland = backend.country("FI");
        assertEquals("Finland", finland.name);
    }

    @Test
    public void findSweden() {
        final Country sweden = backend.country("SE");
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
