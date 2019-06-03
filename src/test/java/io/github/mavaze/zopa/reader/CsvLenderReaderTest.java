package io.github.mavaze.zopa.reader;

import io.github.mavaze.zopa.dto.LenderDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CsvLenderReaderTest {

    @Autowired
    private CsvLenderReader csvLenderReader;

    @Test
    public void parseCsvSuccessfullySkippingWrongOnes() {
        List<LenderDto> parsedLenders = csvLenderReader.parse(new File("lenders.csv"));
        assertThat(parsedLenders, hasSize(8));
    }

    @Test
    public void skipLinesWithErroneousData() {
        List<LenderDto> parsedLenders = csvLenderReader.parse(new File("lenders.csv"));
        assertThat(parsedLenders, not(hasItem(hasProperty("name", anyOf(
                is("WrongRate"), is("WrongAmount"), is("HugeAmount"), is("LessFields")
        )))));
    }

    @Test
    public void parseLineHavingIgnoringExtraColumns() {
        List<LenderDto> parsedLenders = csvLenderReader.parse(new File("lenders.csv"));
        assertThat(parsedLenders, hasItem(hasProperty("name", is("ExtraFields"))));
    }
}
