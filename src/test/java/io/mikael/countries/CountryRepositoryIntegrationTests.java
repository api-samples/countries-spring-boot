package io.mikael.countries;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.converter.JacksonConverter;
import retrofit.http.GET;
import retrofit.http.Path;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@DirtiesContext
@ActiveProfiles("test")
public class CountryRepositoryIntegrationTests {

    @Autowired
    private EmbeddedWebApplicationContext server;

    @Autowired
    private ObjectMapper objectMapper;

    private CountriesRestFacade backend;

    public interface CountriesRestFacade {
        @GET("/countries/{code}")
        Country country(@Path("code") String code);
    }

    @Before
    public void init() {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(String.format("http://127.0.0.1:%s",
                        server.getEmbeddedServletContainer().getPort()))
                .setClient(new ApacheClient())
                .setConverter(new JacksonConverter(objectMapper))
                .build();
        this.backend = restAdapter.create(CountriesRestFacade.class);
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

    @Test(expected = RetrofitError.class)
    public void findInvalid() {
        try {
            backend.country("BLAH");
        } catch (final RetrofitError e) {
            assertEquals("invalid country should not be found",
                    HttpStatus.NOT_FOUND.value(), e.getResponse().getStatus());
            throw e;
        }
    }

}
