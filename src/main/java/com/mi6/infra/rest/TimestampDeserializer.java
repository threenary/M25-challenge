package com.n26.infra.rest;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.n26.infra.exceptions.TransactionsStatisticsError;
import com.n26.infra.exceptions.UnprocessableEntityException;

public class TimestampDeserializer extends JsonDeserializer<Long>
{

    @Override
    public Long deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
    {
        JsonNode node = jp.getCodec().readTree(jp);
        Instant timestamp;
        try
        {
            timestamp = Instant.parse(node.textValue());
        }
        catch (DateTimeParseException e)
        {
            throw new UnprocessableEntityException(TransactionsStatisticsError.TIMESTAMP_UNPARSEABLE);
        }
        return timestamp.toEpochMilli();
    }
}
