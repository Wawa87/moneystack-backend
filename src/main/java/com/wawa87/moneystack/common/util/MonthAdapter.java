package com.wawa87.moneystack.common.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Month;

public class MonthAdapter implements JsonSerializer<Month>, JsonDeserializer<Month> {
    @Override
    public JsonElement serialize(Month src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name());
    }

    @Override
    public Month deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            Month month = Month.of(json.getAsInt());
            return month;
        } catch (Exception e) {
            try {
                Month month = Month.valueOf(json.getAsString());
                return month;
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
