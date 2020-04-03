package statistics;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.n26.Application;
import com.n26.repository.TransactionsRepository;
import com.n26.statistic.Statistics;
import com.n26.statistic.rest.StatisticsResource;
import com.n26.transaction.Transaction;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsResourcesAllLayersShould {

	@Value("http://localhost:${local.server.port}")
	String localhost;

	String getURI;

	TestRestTemplate restTemplate;

	@Autowired
	TransactionsRepository repository;

	@Before
	public void setUp() {
		restTemplate = new TestRestTemplate();

		getURI = UriComponentsBuilder
				.fromHttpUrl(localhost + StatisticsResource.class.getAnnotation(RequestMapping.class).path()[0])
				.toUriString();

		repository.removeAll();
	}

	@Test
	public void return_last_60_seconds_transactions_statistics() {
		Transaction min = Transaction.builder().amount(new BigDecimal("30")).timestamp(Instant.now().toEpochMilli()).build();
		Transaction max = Transaction.builder().amount(new BigDecimal("50")).timestamp(Instant.now().toEpochMilli()).build();

		repository.save(min);
		repository.save(max);

		final ResponseEntity<Statistics> response = restTemplate.exchange(getURI, HttpMethod.GET, null,
				Statistics.class);

		final Statistics body = response.getBody();

		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(body.getMax(), is(equalTo(max.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP))));
		assertThat(body.getMin(), is(equalTo(min.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP))));
		assertThat(body.getCount(), is(equalTo(2L)));
		assertThat(body.getSum(),
				is(equalTo(max.getAmount().add(min.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP))));
		assertThat(body.getAvg(), is(equalTo(min.getAmount().add(max.getAmount()).divide(new BigDecimal("2"))
				.setScale(2, BigDecimal.ROUND_HALF_UP))));
	}
}
