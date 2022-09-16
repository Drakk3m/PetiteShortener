package com.notarius.lepetite.PetiteShortener.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShortenerUtils {

    private final static String REGEX = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
    private final static Pattern PATTERN = Pattern.compile(REGEX);
    private final static String SOURCE = "QAZWSXEDCRFVTGBYHNUJMIKOLPqazwsxedcrfvtgbyhnujmikolp1234567890";

    public static String entryGenerator()
    {
        String dictEntry = "";
                
        for (int i = 0; i < 10; i++)
            dictEntry += SOURCE.charAt((int) Math.floor(Math.random()*SOURCE.length()));

        return dictEntry;
    }
    

    public static boolean validateURL(String url) {
        Matcher m = PATTERN.matcher(url);
        return m.matches();
    }
}
