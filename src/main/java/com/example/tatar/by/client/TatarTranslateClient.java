package com.example.tatar.by.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tatarTranslateClient",
        url = "https://translate.tatar"
)
@Component
public interface TatarTranslateClient {

    @GetMapping(value = "/translate?lang=1&text=Нихәл, синең исемең ничек?", produces = "text/html;charset=UTF-8")
    String translate();
    @PostMapping("/translate_array?lang=0")
    String translateArray(@RequestBody String[] text_array);

}