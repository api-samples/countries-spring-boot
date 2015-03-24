package io.mikael.countries.web;

import io.mikael.countries.domain.Country;
import io.mikael.countries.domain.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountryController {

    @Autowired
    private CountryRepository dao;

    @RequestMapping(value="/countries/{cca2}", method=RequestMethod.GET)
    public ResponseEntity<Country> getCountry(final @PathVariable String cca2) {
        return dao.findCountry(cca2)
                .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
