package io.mikael.countries;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
public class Application {

    @Value("classpath:countries.json")
    private Resource file;

    private volatile ConcurrentMap<String, Country> data;

    @PostConstruct
    private void parseCountries() {
        try (final InputStream is = file.getInputStream()) {
            final var list = new ObjectMapper().readValue(is, new TypeReference<List<Country>>() {});
            data = list.stream().collect(Collectors.toMap(c -> c.cca2, c -> c, (a, b) -> a, ConcurrentHashMap::new));
        } catch (final IOException e) {
            throw new RuntimeException("unable to parse countries", e);
        }
    }

    @GetMapping(value="/countries/{cca2}")
    public ResponseEntity<Country> getCountry(final @PathVariable String cca2) {
        return Optional.ofNullable(data.get(cca2))
                .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public static void main(final String ... args) {
        SpringApplication.run(Application.class, args);
    }

}
