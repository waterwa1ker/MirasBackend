package com.example.tatar.by.service;

import com.example.tatar.by.client.TatarTranslateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TatarTranslateService {

    private final TatarTranslateClient tatarTranslateClient;

    @Autowired
    public TatarTranslateService(TatarTranslateClient tatarTranslateClient) {
        this.tatarTranslateClient = tatarTranslateClient;
    }

    public String translate(int lang, String text) {
        System.out.println(lang + " " + text);
        return tatarTranslateClient.translate();
    }

    public String translateArray(String[] textArray) {
        System.out.println(textArray[0]);
        return tatarTranslateClient.translateArray(textArray);
    }

}