package com.notarius.lepetite.PetiteShortener.rest;

import com.notarius.lepetite.PetiteShortener.controllers.ShortenerMongoRepository;
import com.notarius.lepetite.PetiteShortener.exceptions.BadRequestException;
import com.notarius.lepetite.PetiteShortener.exceptions.NotFoundException;
import com.notarius.lepetite.PetiteShortener.model.DictionaryEntry;
import com.notarius.lepetite.PetiteShortener.utils.ShortenerUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController @Log4j2
public class PetiteRestController {

    private final String BASE_URL = "http://localhost:8080/s/";
    private final String ALIAS_TAKEN = "Alias is already taken.";
    private final String KEY_NOT_FOUND = "Alias not found in generated strings.";
    private final String REDIRECT_SUCCESS = "Alias found. Redirecting.";
    private final String DUPLICATED_ENTRY = "URL already in shortened.";
    private final String BAD_URL = "Given URL is not a valid address.";
    private final String URL_FOUND = "URL already in the system. Returning assigned shortener";

    @Autowired
    private ShortenerMongoRepository persistor;

    @RequestMapping(value = "/petiteshortener", method = RequestMethod.POST)
    public ResponseEntity<Object> getShortURL(@RequestBody DictionaryEntry newEntry) throws BadRequestException {
        String entryValue = "";

        if (!ShortenerUtils.validateURL(newEntry.getFullurl()))
        {
            log.error(BAD_URL + " Provided URL: " + newEntry.getFullurl());
            throw new BadRequestException(BAD_URL);
        }
        else if (persistor.existsByFullurl(newEntry.getFullurl()))
        {
            log.info(URL_FOUND);
            entryValue = persistor.findByFullurl(newEntry.getFullurl()).get().getShorturl();

            log.info("Process fulfilled successfuly. Provided short identifier is " + entryValue);
        }
        else {
            do {
                entryValue = ShortenerUtils.entryGenerator();

                log.info("Generated a new short phrase");

            } while (persistor.existsById(entryValue));

            log.info("Found an unused identifier for they new entry");

            newEntry.setShorturl(BASE_URL + entryValue);

            log.info("Process fulfilled successfuly. Provided short identifier is " + BASE_URL + entryValue);
            persistor.save(newEntry);
        }

        return new ResponseEntity<>(entryValue, HttpStatus.OK);
    }

    @RequestMapping(value = "/s/{shortURL}", method = RequestMethod.GET)
    public void getFullURL(HttpServletResponse response, @PathVariable("shortURL") String shortURL) throws IOException, NotFoundException {
        DictionaryEntry entry = persistor.findById(shortURL).orElseThrow(() -> new NotFoundException(KEY_NOT_FOUND));

        log.info(REDIRECT_SUCCESS);
        response.sendRedirect(entry.getFullurl());
    }

    @RequestMapping(value = "/s/assign", method = RequestMethod.POST)
    public ResponseEntity<Object> AssignAliasToURL(@RequestBody @NonNull DictionaryEntry newEntry, @RequestBody @NonNull String urlAlias) throws BadRequestException {
        if (!ShortenerUtils.validateURL(newEntry.getFullurl()))
        {
            log.error(BAD_URL);
            throw new BadRequestException(BAD_URL);
        } 
        else if (persistor.existsById(urlAlias))
        {
            log.error(ALIAS_TAKEN);
            throw new BadRequestException(ALIAS_TAKEN);
        }
        else if (persistor.existsByFullurl(newEntry.getFullurl()))
        {
            log.error(DUPLICATED_ENTRY);
            throw new BadRequestException(DUPLICATED_ENTRY);
        }

        newEntry.setShorturl(BASE_URL+urlAlias);
        persistor.save(newEntry);

        return new ResponseEntity<>(urlAlias, HttpStatus.OK);
    }

}
