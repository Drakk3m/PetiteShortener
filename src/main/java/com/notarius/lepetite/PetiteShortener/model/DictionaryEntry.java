package com.notarius.lepetite.PetiteShortener.model;

import lombok.Getter;
import lombok.Setter;

public class DictionaryEntry {
    @Getter @Setter
    private String full_url;
    private String short_url;
}
