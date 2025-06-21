package by.test.sample.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;

import static by.test.sample.utils.ApplicationConstants.CUSTOM_DATE_TIME_FORMATTER;

public class CustomLocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.format(CUSTOM_DATE_TIME_FORMATTER));
    }
}
