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

    @Autowired
    private ShortenerMongoRepository persistor;

    @RequestMapping(value = "/petiteshortener", method = RequestMethod.POST)
    public ResponseEntity<Object> getShortURL(@RequestBody DictionaryEntry newEntry) throws BadRequestException {
        String entryValue = "";

        if (!ShortenerUtils.validateURL(newEntry.getFullurl()))
        {
            log.error(ShortenerUtils.BAD_URL + " Provided URL: " + newEntry.getFullurl());
            throw new BadRequestException(ShortenerUtils.BAD_URL);
        }
        else if (persistor.existsByFullurl(newEntry.getFullurl()))
        {
            log.info(ShortenerUtils.URL_FOUND);
            entryValue = persistor.findByFullurl(newEntry.getFullurl()).get().getShorturl();

            log.info("Process fulfilled successfuly. Provided short identifier is " + entryValue);
        }
        else {
            do {
                entryValue = ShortenerUtils.entryGenerator();

                log.info("Generated a new short phrase");

            } while (persistor.existsById(entryValue));

            log.info("Found an unused identifier for they new entry");

            newEntry.setShorturl(ShortenerUtils.BASE_URL + entryValue);

            log.info("Process fulfilled successfuly. Provided short identifier is " + ShortenerUtils.BASE_URL + entryValue);
            persistor.save(newEntry);
        }

        return new ResponseEntity<>(newEntry.getShorturl(), HttpStatus.OK);
    }

    @RequestMapping(value = "/s/{shortURL}", method = RequestMethod.GET)
    public void getFullURL(HttpServletResponse response, @NonNull @PathVariable("shortURL") String shortURL) throws IOException, NotFoundException {
        if (!persistor.existsById(ShortenerUtils.BASE_URL + shortURL))
        {
            log.error(ShortenerUtils.KEY_NOT_FOUND);
            throw new NotFoundException(ShortenerUtils.KEY_NOT_FOUND);
        }

        DictionaryEntry entry = persistor.findById(ShortenerUtils.BASE_URL + shortURL).get();

        log.info(ShortenerUtils.REDIRECT_SUCCESS + entry.getFullurl());
        response.sendRedirect(entry.getFullurl());
    }

    @RequestMapping(value = "/s/assign", method = RequestMethod.POST)
    public ResponseEntity<Object> AssignAliasToURL(@RequestBody @NonNull DictionaryEntry newEntry) throws BadRequestException {
        if (!ShortenerUtils.validateURL(newEntry.getFullurl()))
        {
            log.error(ShortenerUtils.BAD_URL);
            throw new BadRequestException(ShortenerUtils.BAD_URL);
        } 
        else if (persistor.existsById(newEntry.getShorturl()))
        {
            log.error(ShortenerUtils.ALIAS_TAKEN);
            throw new BadRequestException(ShortenerUtils.ALIAS_TAKEN);
        }
        else if (persistor.existsByFullurl(newEntry.getFullurl()))
        {
            log.error(ShortenerUtils.DUPLICATED_ENTRY);
            throw new BadRequestException(ShortenerUtils.DUPLICATED_ENTRY);
        }

        persistor.save(newEntry);

        return new ResponseEntity<>(newEntry.getShorturl(), HttpStatus.OK);
    }

}
