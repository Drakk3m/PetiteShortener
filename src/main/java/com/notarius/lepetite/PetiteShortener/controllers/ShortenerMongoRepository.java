package com.notarius.lepetite.PetiteShortener.controllers;

import com.notarius.lepetite.PetiteShortener.model.DictionaryEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShortenerMongoRepository extends MongoRepository<DictionaryEntry, String> {

    public boolean existsByFullurl(String longUrl);

    public Optional<DictionaryEntry> findByFullurl(String fullUrl);
}
