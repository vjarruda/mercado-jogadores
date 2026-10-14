package br.com.passe.club.converter;

import br.com.passe.club.enums.Formation;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FormationConverter implements AttributeConverter<Formation, String> {

    @Override
    public String convertToDatabaseColumn(Formation formation) {
        return formation == null ? null : formation.getCode();
    }

    @Override
    public Formation convertToEntityAttribute(String code) {
        return code == null ? null : Formation.fromCode(code);
    }
}