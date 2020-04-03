package com.n26.transaction.rest;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.infra.exceptions.TransactionOutdatedException;
import com.n26.infra.exceptions.TransactionsStatisticsError;
import com.n26.infra.exceptions.UnprocessableEntityException;
import com.n26.repository.TransactionsRepository;
import com.n26.transaction.Transaction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/transactions")
public class TransactionResource {
    public static final int TIME_TO_LIVE = 59999;
    private final TransactionsRepository repository;

    @ApiOperation(value = "", nickname = "transactionPost", notes = "Posts a transaction")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Transaction created"),
                    @ApiResponse(code = 204, message = "Transaction older than 60 seconds"),
                    @ApiResponse(code = 400, message = "No valid JSON format"),
                    @ApiResponse(code = 422, message = "Data not parseable")})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> postTransaction(@RequestBody Transaction transaction) throws Exception {
        if (Instant.now().toEpochMilli() - transaction.getTimestamp() > TIME_TO_LIVE) {
            throw new TransactionOutdatedException(TransactionsStatisticsError.TRANSACTION_OUTDATED);
        }
        if (transaction.getTimestamp() > Instant.now().toEpochMilli()) {
            throw new UnprocessableEntityException(TransactionsStatisticsError.TRANSACTION_IN_THE_FUTURE);
        }
        repository.save(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @ApiOperation(value = "", nickname = "transactionsDelete", notes = "Deletes all the transactions")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Removed all transactions")})
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTransaction() {
        repository.removeAll();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
