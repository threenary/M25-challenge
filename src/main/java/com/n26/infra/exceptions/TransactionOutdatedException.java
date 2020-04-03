package com.n26.infra.exceptions;

public class TransactionOutdatedException extends RuntimeException {

	private static final long serialVersionUID = 370780927102361949L;

	public TransactionOutdatedException(TransactionsStatisticsError exception) {
		super(exception.getErrorDescription());
	}
}
