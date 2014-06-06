package io.mikael.countries.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class CountryRepository {

    @Value("classpath:countries.json")
    private Resource file;

    private List<Country> data = Collections.emptyList();

    @PostConstruct
    public void parseCountries() {
        try (final InputStream is = file.getInputStream()) {
            data = new ObjectMapper().readValue(is, new TypeReference<List<Country>>() {});
        } catch (final IOException e) {
            throw new RuntimeException("unable to parse countries", e);
        }
    }

    /**
     * Not optimal, but demonstrates @{code Optional} and Streams.
     */
    public Optional<Country> findCountry(final String cca2) {
        return data.stream().filter(c -> c.cca2.equals(cca2)).findFirst();
    }

}
