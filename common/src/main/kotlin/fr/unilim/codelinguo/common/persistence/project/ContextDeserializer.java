package fr.unilim.codelinguo.common.persistence.project;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import fr.unilim.codelinguo.common.model.Word;
import fr.unilim.codelinguo.common.model.context.Context;
import fr.unilim.codelinguo.common.model.context.PrimaryContext;
import fr.unilim.codelinguo.common.model.context.SecondaryContext;

import java.lang.reflect.Type;

public class ContextDeserializer implements JsonDeserializer<Context> {

    @Override
    public Context deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement wordElement = jsonObject.get("word");
        Word word = context.deserialize(wordElement, Word.class);

        float priority = jsonObject.get("priority").getAsFloat();
        if (priority == PrimaryContext.PRIMARY_CONTEXT_PRIORITY) {
            return new PrimaryContext(word);
        } else if (priority == SecondaryContext.SECONDARY_CONTEXT_PRIORITY) {
            return new SecondaryContext(word);
        }

        throw new JsonParseException("Unknown context type");
    }
}
