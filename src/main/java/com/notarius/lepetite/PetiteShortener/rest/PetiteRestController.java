package com.notarius.lepetite.PetiteShortener.rest;

import com.notarius.lepetite.PetiteShortener.exceptions.BadRequestException;
import com.notarius.lepetite.PetiteShortener.exceptions.NotFoundException;
import com.notarius.lepetite.PetiteShortener.model.DictionaryEntry;
import com.notarius.lepetite.PetiteShortener.utils.ShortenerUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController @Log4j2
public class PetiteRestController {

    String BASE_URL = "http://localhost:8080/petiteshortener/";
    String ALIAS_TAKEN = "Alias is already taken.";
    String KEY_NOT_FOUND = "Alias not found in generated strings";
    String REDIRECT_SUCCESS = "Alias found. Redirecting.";

    String DUPLICATED_ENTRY = "URL already in shortened.";
    Map<String, DictionaryEntry> dictionary = new HashMap<>();

    @RequestMapping(value = "/petiteshortener", method = RequestMethod.POST)
    public ResponseEntity<Object> getShortURL(@RequestBody DictionaryEntry newEntry)
    {
        String entryValue = "";
        do {
            entryValue = ShortenerUtils.entryGenerator();
        } while (dictionary.containsKey(entryValue));

        newEntry.setShort_url(BASE_URL+entryValue);
        dictionary.put(entryValue,newEntry);

        return new ResponseEntity<Object>(entryValue, HttpStatus.OK);
    }

    @RequestMapping(value = "/petiteshortener/s/{shortURL}", method = RequestMethod.GET)
    public void getFullURL(HttpServletResponse response, @PathVariable("shortURL") String shortURL) throws IOException, NotFoundException {
        if (!dictionary.containsKey(shortURL))
        {
            log.error(KEY_NOT_FOUND);
            throw new NotFoundException(KEY_NOT_FOUND);
        }

        log.info(REDIRECT_SUCCESS);

        response.sendRedirect(dictionary.get(shortURL).getFull_url());
    }

    @RequestMapping(value = "/petiteshortener/assign", method = RequestMethod.POST)
    public ResponseEntity<Object> AssignAliasToURL(@RequestBody DictionaryEntry newEntry, @RequestBody String urlAlias) throws BadRequestException {
        if (dictionary.containsKey(urlAlias))
        {
            log.error(ALIAS_TAKEN);
            throw new BadRequestException(ALIAS_TAKEN);
        }

        if (dictionary.containsValue(newEntry.getFull_url()))
        {
            log.error(DUPLICATED_ENTRY);
            throw new BadRequestException(DUPLICATED_ENTRY);
        }

        newEntry.setShort_url(BASE_URL+urlAlias);
        dictionary.put(urlAlias, newEntry);

        return new ResponseEntity<Object>(urlAlias, HttpStatus.OK);
    }

}
