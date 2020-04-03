package com.n26.transaction;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.n26.infra.rest.TimestampDeserializer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@EqualsAndHashCode
@Getter
public class Transaction implements Comparable<Transaction> {
    @ApiModelProperty(value = "Amount of the transaction parseable as BigDecimal")
    private BigDecimal amount;

    @ApiModelProperty(value = "Timestamp of the transaction in format ISO8601")
    @JsonDeserialize(using = TimestampDeserializer.class)
    private long timestamp;

    public Transaction() {
    }

    public Transaction(BigDecimal amount, long timestamp) {
        super();
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Transaction tx) {
        if (timestamp > tx.getTimestamp())
            return 1;
        else if (timestamp == tx.getTimestamp())
            return 0;
        return -1;
    }

    public boolean isDisposable() {
        return (Duration.between(Instant.ofEpochMilli(timestamp), Instant.now()).toMillis() > 59999);
    }


    @Override
    public String toString() {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatted = formatter.format(new Date(timestamp));

        return dateFormatted + "\n";
    }

}
