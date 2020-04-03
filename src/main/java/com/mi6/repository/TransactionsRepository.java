package com.n26.repository;

import com.n26.statistic.Statistics;
import com.n26.transaction.Transaction;
import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

@Component
public class TransactionsRepository {
    public static final int TIME_TO_LIVE = 59999;

    Comparator<Transaction> timestampComparator = Comparator.comparing(Transaction::getTimestamp);

    private PriorityQueue<Transaction> transactions = new PriorityQueue<>(timestampComparator);

    private Statistics statistics = Statistics.builder().build();

    public synchronized PriorityQueue<Transaction> getTransactions() {
        return transactions;
    }

    public synchronized Statistics getStatistics() {
        return statistics;
    }

    public synchronized void save(Transaction tx) {
        Transaction oldest = transactions.peek();

        if (oldest != null && tx.getTimestamp() - oldest.getTimestamp() > TIME_TO_LIVE) {
            transactions.poll();
        }
        transactions.add(tx);
        updateStatistics();
    }



    @Scheduled(initialDelay = 0, fixedRate = 1000)
    public void transactionsCleaner() {
        System.out.println("Cleaning up transactions");
        transactions.removeIf(Transaction::isDisposable);
    }


    public void removeAll() {
        transactions.clear();
        cleanUpStatistics();
    }


    public void updateStatistics() {
        if (transactions.isEmpty()) {
            cleanUpStatistics();
        } else {
            BigDecimalSummaryStatistics summary =
                    transactions.stream().collect(Collectors2.summarizingBigDecimal(Transaction::getAmount));

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


    private void cleanUpStatistics() {
        statistics =
                Statistics
                        .builder().avg(BigDecimal.ZERO).max(BigDecimal.ZERO).min(BigDecimal.ZERO)
                        .sum(BigDecimal.ZERO).count(0).build();
    }

}
