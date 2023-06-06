package com.gangoffive.birdtradingplatform.dto;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonSerializationContext;
import com.nimbusds.jose.shaded.gson.JsonSerializer;
import lombok.*;

import java.lang.reflect.Type;

@Data
public class ProductDetailDto implements JsonSerializer<ProductDto> {

    @Override
    public JsonElement serialize(ProductDto productDto, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", productDto.getId());
//        jsonObject.addProperty("attribute2", productDto.getAttribute2());
        // Add other attributes of ProductDto as needed
        return jsonObject;
    }
}
