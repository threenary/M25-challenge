package com.n26.statistic.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.repository.TransactionsRepository;
import com.n26.statistic.Statistics;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/statistics")
public class StatisticsResource
{

    final TransactionsRepository repository;

    @ApiOperation(value = "", nickname = "statisticsGet", notes = "Gets the transactions statistics for the last 60 seconds")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Removed all transactions")})
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Statistics> getStatistics()
    {
        return new ResponseEntity<>(repository.getStatistics(), HttpStatus.OK);
    }

}
