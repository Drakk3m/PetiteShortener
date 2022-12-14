package com.notarius.lepetite.PetiteShortener.model;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryEntry {

    @Getter @Setter @NonNull
    private String fullurl;
    @Getter @Setter @Id @NonNull
    private String shorturl;

    @Override
    public String toString() {
        return "DictionaryEntry{" +
                "full_url='" + fullurl + '\'' +
                ", short_url='" + shorturl + '\'' +
                '}';
    }
}
