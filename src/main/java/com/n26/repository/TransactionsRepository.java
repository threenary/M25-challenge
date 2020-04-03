package com.n26.repository;

import java.math.BigDecimal;
import java.util.concurrent.PriorityBlockingQueue;

import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.n26.statistic.Statistics;
import com.n26.transaction.Transaction;

@Component
public class TransactionsRepository
{
    //Comparator<Transaction> timestampSorter = Comparator.comparing(Transaction::getTimestamp);

    private PriorityBlockingQueue<Transaction> transactions = new PriorityBlockingQueue<>();

    private Statistics statistics = Statistics.builder().build();

    public PriorityBlockingQueue<Transaction> getTransactions()
    {
        return transactions;
    }


    public Statistics getStatistics()
    {
        return statistics;
    }


    public void save(Transaction tx)
    {
        Transaction oldest = transactions.peek();

        if (oldest != null && tx.getTimestamp() - oldest.getTimestamp() > 59999)
        {
            transactions.poll();
        }
        transactions.add(tx);
        updateStatistics();
    }


    public void removeAll()
    {
        transactions.clear();
        cleanUpStatistics();
    }


    public void updateStatistics()
    {
        if (transactions.isEmpty())
        {
            cleanUpStatistics();
        }
        else
        {
            BigDecimalSummaryStatistics summary =
                transactions.stream().collect(Collectors2.summarizingBigDecimal(e -> e.getAmount()));

            statistics =
                Statistics
                    .builder()
                    .sum(summary.getSum())
                    .avg(summary.getAverage())
                    .max(summary.getMax())
                    .min(summary.getMin())
                    .count(summary.getCount()).build();
        }
    }


    private void cleanUpStatistics()
    {
        statistics =
            Statistics
                .builder().avg(BigDecimal.ZERO).max(BigDecimal.ZERO).min(BigDecimal.ZERO)
                .sum(BigDecimal.ZERO).count(0).build();
    }


    @Scheduled(initialDelay = 0, fixedRate = 500)
    public void transactionsCleaner()
    {
        System.out.println("Cleaning up transactions");
        transactions.removeIf((Transaction tx) -> tx.isDisposable());
    }
}
