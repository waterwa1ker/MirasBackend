package com.example.tatar.by.util;

import com.example.tatar.by.constants.PostGenre;
import org.springframework.stereotype.Service;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
@Service
public class GenresConverter implements AttributeConverter<List<PostGenre>, String> {

    @Override
    public String convertToDatabaseColumn(List<PostGenre> postGenreList) {
        return postGenreList
                .stream()
                .map(PostGenre::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<PostGenre> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split(","))
                .map(PostGenre::valueOf)
                .collect(Collectors.toList());
    }
}
