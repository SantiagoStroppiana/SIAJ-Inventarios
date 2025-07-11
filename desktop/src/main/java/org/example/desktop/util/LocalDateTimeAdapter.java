package org.example.desktop.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();  // 👈 Si es null, escribir null
        } else {
            out.value(value.format(formatter));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        if (in.peek() == JsonToken.BEGIN_ARRAY) {
            in.beginArray();
            int year = in.nextInt();
            int month = in.nextInt();
            int day = in.nextInt();
            int hour = in.nextInt();
            int minute = in.nextInt();
            int second = in.nextInt();

            int nano = 0;
            if (in.peek() != JsonToken.END_ARRAY) {
                nano = in.nextInt();
            }

            in.endArray();
            return LocalDateTime.of(year, month, day, hour, minute, second, nano);
        }

        return LocalDateTime.parse(in.nextString(), formatter);
    }


}
