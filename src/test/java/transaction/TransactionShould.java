package transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.n26.transaction.Transaction;

public class TransactionShould
{

    @Test
    public void compare_two_transactions_by_timestamp()
    {
        Transaction olderTransaction = Transaction.builder().amount(new BigDecimal(1)).timestamp(1585922346).build();
        Transaction newerTransaction = Transaction.builder().amount(new BigDecimal(1)).timestamp(1585918746).build();

        assertEquals(1, olderTransaction.compareTo(newerTransaction));
    }


    @Test
    public void return_true_when_transaction_older_than_60_seconds()
    {
        Transaction tx = Transaction.builder().amount(new BigDecimal(1)).
            timestamp(Instant.now().minus(60, ChronoUnit.SECONDS).toEpochMilli()).build();

        assertTrue(tx.isDisposable());
    }

}
