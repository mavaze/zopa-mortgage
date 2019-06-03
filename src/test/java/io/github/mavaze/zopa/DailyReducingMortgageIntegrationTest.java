package io.github.mavaze.zopa;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DailyReducingMortgageIntegrationTest {

	@Rule
	public OutputCapture output = new OutputCapture();

	@Autowired
	private CommandLineRunner applicationRunner;

	@Test
	public void dailyReducingMortgageQuoteSuccess() throws Exception {
		applicationRunner.run("lenders.csv", "1000");
		assertThat(this.output.toString()).startsWith(
				"Requested amount: £1000\n" +
				"Rate: 7.0%\n" +
				"Monthly repayment: £30.80\n" +
				"Total repayment: £1108.74"
		);
	}

	@Test
	public void marketFileDoesNotExistFailure() throws Exception {
		applicationRunner.run("no_such_file.csv", "1000");
		assertThat(this.output.toString()).startsWith("Failed to load file : 'no_such_file.csv'");
	}

	@Test
	public void requestedLoanTooLowFailure() throws Exception {
		applicationRunner.run("lenders.csv", "100");
		assertThat(this.output.toString()).startsWith("Amount must be in between 1000 and 15000 and in multiples of 100");
	}

	@Test
	public void requestedLoanTooHighFailure() throws Exception {
		applicationRunner.run("lenders.csv", "20000");
		assertThat(this.output.toString()).startsWith("Amount must be in between 1000 and 15000 and in multiples of 100");
	}

	@Test
	public void requestedLoanNotInMultiplesOfHundredsFailure() throws Exception {
		applicationRunner.run("lenders.csv", "1150");
		assertThat(this.output.toString()).startsWith("Amount must be in between 1000 and 15000 and in multiples of 100");
	}
}
