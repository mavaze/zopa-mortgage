package io.github.mavaze.zopa;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "zopa.mortgage.strategy=MONTHLY")
public class MonthlyReducingMortgageIntegrationTest {

	@Autowired
	private CommandLineRunner applicationRunner;

	@Rule
	public OutputCapture output = new OutputCapture();

	@Test
	public void monthlyReducingMortgageQuoteSuccess() throws Exception {
		applicationRunner.run("lenders.csv", "1000");
		assertThat(this.output.toString()).startsWith(
				"Requested amount: £1000\n" +
				"Rate: 7.0%\n" +
				"Monthly repayment: £30.88\n" +
				"Total repayment: £1111.58"
		);
	}
}
