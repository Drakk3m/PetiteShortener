package com.notarius.lepetite.PetiteShortener.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShortenerUtils {

    private final static String REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
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

    //Logs and Messaging
    public static final String BASE_URL = "http://localhost:8080/s/";
    public static final String ALIAS_TAKEN = "Alias is already taken.";
    public static final String KEY_NOT_FOUND = "Alias not found in generated strings.";
    public static final String REDIRECT_SUCCESS = "Alias found. Redirecting to ";
    public static final String DUPLICATED_ENTRY = "URL already in shortened.";
    public static final String BAD_URL = "Given URL is not a valid address.";
    public static final String URL_FOUND = "URL already in the system. Returning assigned shortener";
}
