package io.mikael.countries;

import io.mikael.countries.domain.Country;
import io.mikael.countries.domain.CountryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

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
    private CountryRepository dao;

    private RestTemplate restTemplate = new TestRestTemplate();

    private int port;

    @Before
    public void init() {
        port = server.getEmbeddedServletContainer().getPort();
    }

    @Test
    public void findFinlandDirectly() {
        final Country finland = dao.findCountry("FI")
                .orElseThrow(() -> new AssertionError("Finland not found"));
        assertEquals("Finland", finland.name);
    }

    @Test
    public void findSwedenOverWeb() {
        final String url = String.format("http://localhost:%s/countries/SE", port);
        final ResponseEntity<Country> re = restTemplate.getForEntity(url, Country.class);
        assertEquals(HttpStatus.OK, re.getStatusCode());
        final Country sweden = re.getBody();
        assertEquals("Sweden", sweden.name);
    }

    @Test
    public void findInvalid() {
        final String url = String.format("http://localhost:%s/countries/BLAH", port);
        final ResponseEntity<Country> re = restTemplate.getForEntity(url, Country.class);
        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    }

}
