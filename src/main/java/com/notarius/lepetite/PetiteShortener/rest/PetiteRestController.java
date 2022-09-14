package com.notarius.lepetite.PetiteShortener.rest;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class PetiteRestController {

    private String entryGenerator()
    {
        String dictEntry = "";
        String sourceChars = "QAZWSXEDCRFVTGBYHNUJMIKOLPqazwsxedcrfvtgbyhnujmikolp1234567890";

        for (int i = 0; i < 10; i++)
            dictEntry += sourceChars.charAt((int) Math.floor(Math.random()*sourceChars.length()));

        return dictEntry;
    }

}
