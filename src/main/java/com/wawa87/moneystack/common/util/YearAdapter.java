package com.wawa87.moneystack.common.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Year;

public class YearAdapter implements JsonSerializer<Year>, JsonDeserializer<Year> {
    @Override
    public JsonElement serialize(Year src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getValue());
    }

    @Override
    public Year deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Year.of(json.getAsInt());
    }
}
