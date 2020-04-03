package com.n26.statistic;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.infra.rest.BigDecimalSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Statistics description")
@Builder
@Getter
@Setter
public class Statistics {

	public Statistics() {

	}

	public Statistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, long count) {
		super();
		this.sum = sum;
		this.avg = avg;
		this.max = max;
		this.min = min;
		this.count = count;
	}

	@ApiModelProperty(value = "Total sum of transaction value in the last 60 seconds")
	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal sum;

	@ApiModelProperty(value = "Average amount of transaction value in the last 60 seconds")
	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal avg;

	@ApiModelProperty(value = "Highest transaction value in the last 60 seconds")
	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal max;

	@ApiModelProperty(value = "Lowest transaction value in the last 60 seconds")
	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal min;

	@ApiModelProperty(value = "Total number of transactions that happened in the last 60 seconds")
	private long count;

}
