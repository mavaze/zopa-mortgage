package io.github.mavaze.zopa.reader;

import io.github.mavaze.zopa.dto.LenderDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.emptyList;

@Slf4j
@Component
public class CsvLenderReader implements LenderReader<File> {

    private static final String FIELD_SEPARATOR = ",";

    @Override
    public List<LenderDto> parse(@NonNull final File dataFile) {
        try (Stream<String> lines = Files.lines(dataFile.toPath())) {
            return this.toLenderStream(lines).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Failed to read market file {}. (Reason: {})", dataFile, ex.getMessage());
        }
        return emptyList();
    }

    private Stream<LenderDto> toLenderStream(@NonNull final Stream<String> lineStream) {
        return lineStream.skip(1)
                .map(this::buildLender)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Optional<LenderDto> buildLender(final String line) {
        final String[] fields = line.split(FIELD_SEPARATOR);
        if(fields.length < 3) {
            log.warn("Too less fields when we expect minimum 3 fields in sequence [name, rate, amount]");
            return Optional.empty();
        }
        try {
            BigDecimal rate = new BigDecimal(fields[1]);
            if(rate.doubleValue() > 1) {
                log.warn("Skipping entry as rate of more than 1 found at {}", line);
                throw new IllegalArgumentException(format("Rate must be less than 1 but found %s", line));
            }
            return Optional.of(LenderDto.builder()
                    .name(fields[0])
                    .rate(rate)
                    .availableAmount(Integer.parseInt(fields[2]))
                    .build());
        } catch (IllegalArgumentException ex) {
            log.warn("Skipping entry as failed to build lender from {}. (Reason: {})", line, ex.getMessage());
            return Optional.empty();
        }
    }
}
