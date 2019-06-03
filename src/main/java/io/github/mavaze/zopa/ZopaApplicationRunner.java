package io.github.mavaze.zopa;

import io.github.mavaze.zopa.processor.MortgageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZopaApplicationRunner implements CommandLineRunner {

    private final MortgageProcessor mortgageProcessor;

    @Override
    public void run(String... args) throws Exception {
        log.info("Running Command Line Runner");

        try {
            validateArgs(args);
            mortgageProcessor.process(args);
        } catch (RuntimeException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    /**
     * Validates Parameters: [application] [market_file] [loan_amount]
     * @param args
     * @throws IllegalArgumentException
     */
    private static void validateArgs(final String[] args) throws IllegalArgumentException {
        if (args.length != 2) {
            throw new IllegalArgumentException("You must provide exactly 2 arguments: market_file & loan_amount");
        }

        final File f = new File(args[0]);
        if(!f.exists() || f.isDirectory()) {
            throw new IllegalArgumentException(String.format("Failed to load file : '%s'", args[0]));
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount < 1000 || amount > 15000 || amount % 100 != 0) {
                throw new IllegalArgumentException("Amount must be in between 1000 and 15000 and in multiples of 100");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("Invalid requested amount '%s'", args[1]));
        }
    }
}
