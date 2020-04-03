package statistics;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.statistic.Statistics;

public class RowMapperShould {

	@Test
	public void parse_big_decimal_with_two_decimals_into_string() throws Exception {
		Statistics m = Statistics.builder().max(new BigDecimal("5.0")).avg(new BigDecimal("20.3")).count(1L).build();

		ObjectMapper mapper = new ObjectMapper();

		assertEquals("{\"sum\":null,\"avg\":\"20.30\",\"max\":\"5.00\",\"min\":null,\"count\":1}",
				mapper.writeValueAsString(m));
	}
}
