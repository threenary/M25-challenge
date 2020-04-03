package repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

import com.n26.repository.TransactionsRepository;
import com.n26.transaction.Transaction;

public class TransactionsRepositoryShould {

    TransactionsRepository testSubject = new TransactionsRepository();

    @Before
    public void setUp() {
        testSubject.removeAll();
    }


    @Test
    public void store_new_transactions_in_timestamp_order() {
        Transaction tx3 = Transaction.builder().timestamp(Instant.now().minus(20, ChronoUnit.SECONDS).toEpochMilli())
                .amount(new BigDecimal("3")).build();
        testSubject.save(tx3);

        Transaction tx1 = Transaction.builder().timestamp(Instant.now().toEpochMilli())
                .amount(new BigDecimal("1")).build();
        testSubject.save(tx1);

        Transaction tx2 = Transaction.builder().timestamp(Instant.now().minus(10, ChronoUnit.SECONDS).toEpochMilli())
                .amount(new BigDecimal("2")).build();
        testSubject.save(tx2);


        assertThat(testSubject.getTransactions().size(), is(equalTo(3)));
        assertEquals(tx3, testSubject.getTransactions().poll());
        assertEquals(tx2, testSubject.getTransactions().poll());
        assertEquals(tx1, testSubject.getTransactions().poll());
        assertTrue(testSubject.getTransactions().isEmpty());
    }


    @Test
    public void retrieve_all_stored_transactions() {
        for (int i = 0; i < 5; i++) {
            testSubject.save(Transaction.builder().timestamp(Instant.now().toEpochMilli()).amount(new BigDecimal("55")).build());
        }

        assertThat(testSubject.getTransactions().size(), is(equalTo(5)));
    }


    @Test
    public void delete_all_transactions() {
        testSubject.removeAll();

        assertThat(testSubject.getTransactions().size(), is(equalTo(0)));
    }


    @Test
    public void remove_older_than_60_seconds_when_newer_is_added() {
        Transaction older =
                Transaction
                        .builder().timestamp(Instant.now().minus(80, ChronoUnit.SECONDS).toEpochMilli())
                        .amount(new BigDecimal("55")).build();
        testSubject.save(older);

        Transaction newer = Transaction.builder().timestamp(Instant.now().toEpochMilli()).amount(new BigDecimal("55")).build();
        testSubject.save(newer);

        assertFalse(testSubject.getTransactions().contains(older));
        assertTrue(testSubject.getTransactions().contains(newer));
    }


    @Test
    public void dispose_transactions_older_than_60_seconds() {
        Transaction tx80 =
                Transaction
                        .builder().timestamp(Instant.now().minus(80, ChronoUnit.SECONDS).toEpochMilli())
                        .amount(new BigDecimal("80")).build();
        testSubject.save(tx80);

        Transaction tx70 =
                Transaction
                        .builder().timestamp(Instant.now().minus(70, ChronoUnit.SECONDS).toEpochMilli())
                        .amount(new BigDecimal("70")).build();
        testSubject.save(tx70);

        Transaction should_remain = Transaction.builder().timestamp(Instant.now().toEpochMilli()).amount(new BigDecimal("1")).build();
        testSubject.save(should_remain);

        testSubject.transactionsCleaner();

        assertFalse(testSubject.getTransactions().isEmpty());
        assertTrue(testSubject.getTransactions().contains(should_remain));
    }

}
