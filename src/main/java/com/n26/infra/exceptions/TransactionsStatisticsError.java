package com.n26.infra.exceptions;

public enum TransactionsStatisticsError {

	INTERNAL_SERVER_ERROR("Internal server error"), 
	TRANSACTION_OUTDATED("Transaction is older than 60 seconds"),
	AMOUNT_UNPARSEABLE("Amount is unparseble"), 
	TIMESTAMP_UNPARSEABLE("Timestamp is unparseble"),
	TRANSACTION_IN_THE_FUTURE("Transaction is in the future");

	private final String errorText;

	TransactionsStatisticsError(final String errorText) {
		this.errorText = errorText;
	}

	public String getErrorCode() {
		return name();
	}

	public String getErrorDescription() {
		return errorText;
	}

}
