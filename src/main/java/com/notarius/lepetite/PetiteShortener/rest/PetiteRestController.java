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
import java.util.HashMap;
import java.util.Map;

@RestController @Log4j2
public class PetiteRestController {

    String BASE_URL = "http://localhost:8080/petiteshortener/s/";
    String ALIAS_TAKEN = "Alias is already taken.";
    String KEY_NOT_FOUND = "Alias not found in generated strings";
    String REDIRECT_SUCCESS = "Alias found. Redirecting.";
    String DUPLICATED_ENTRY = "URL already in shortened.";

    @Autowired
    private ShortenerMongoRepository persistor;

    @RequestMapping(value = "/petiteshortener", method = RequestMethod.POST)
    public ResponseEntity<Object> getShortURL(@RequestBody DictionaryEntry newEntry)
    {
        String entryValue = "";
        do {
            entryValue = ShortenerUtils.entryGenerator();
            log.info("Generated a new short phrase");
        } while (persistor.existsById(entryValue));

        newEntry.setShorturl(BASE_URL+entryValue);
        persistor.save(newEntry);

        return new ResponseEntity<Object>(entryValue, HttpStatus.OK);
    }

    @RequestMapping(value = "/petiteshortener/s/{shortURL}", method = RequestMethod.GET)
    public void getFullURL(HttpServletResponse response, @PathVariable("shortURL") String shortURL) throws IOException, NotFoundException {
        DictionaryEntry entry = persistor.findById(shortURL).orElseThrow(() -> new NotFoundException(KEY_NOT_FOUND));

        log.info(REDIRECT_SUCCESS);
        response.sendRedirect(entry.getFullurl());
    }


    @RequestMapping(value = "/petiteshortener/assign", method = RequestMethod.POST)
    public ResponseEntity<Object> AssignAliasToURL(@RequestBody @NonNull DictionaryEntry newEntry, @RequestBody @NonNull String urlAlias) throws BadRequestException {
        if (persistor.existsById(urlAlias))
        {
            log.error(ALIAS_TAKEN);
            throw new BadRequestException(ALIAS_TAKEN);
        }

        if (persistor.existsByFullurl(newEntry.getFullurl()))
        {
            log.error(DUPLICATED_ENTRY);
            throw new BadRequestException(DUPLICATED_ENTRY);
        }

        newEntry.setShorturl(BASE_URL+urlAlias);
        persistor.save(newEntry);

        return new ResponseEntity<Object>(urlAlias, HttpStatus.OK);
    }

}
