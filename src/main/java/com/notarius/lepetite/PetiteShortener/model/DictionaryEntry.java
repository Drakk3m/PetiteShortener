package com.notarius.lepetite.PetiteShortener.model;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

public class DictionaryEntry {

    @Getter @Setter @NonNull
    private String full_url;
    @Getter @Setter @Id @NonNull
    private String short_url;

    @Override
    public String toString() {
        return "DictionaryEntry{" +
                "full_url='" + full_url + '\'' +
                ", short_url='" + short_url + '\'' +
                '}';
    }
}
