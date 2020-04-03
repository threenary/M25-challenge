package com.n26.infra.exceptions;

public class UnprocessableEntityException extends RuntimeException {

	private static final long serialVersionUID = 8826274214217012846L;

	public UnprocessableEntityException(TransactionsStatisticsError exception) {
		super(exception.getErrorDescription());
	}

}
