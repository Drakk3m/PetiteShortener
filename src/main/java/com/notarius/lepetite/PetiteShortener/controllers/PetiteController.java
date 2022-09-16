package com.notarius.lepetite.PetiteShortener.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PetiteController
{

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loadIndex()
    {
        return "index";
    }
}
