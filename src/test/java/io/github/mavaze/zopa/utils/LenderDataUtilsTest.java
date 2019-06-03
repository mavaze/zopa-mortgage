package io.github.mavaze.zopa.utils;

import io.github.mavaze.zopa.dto.LenderDto;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.math.BigDecimal.valueOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class LenderDataUtilsTest {

    @Test
    public void groupingAmountsByRateOrdered() {

        // given
        final List<LenderDto> lenders = new ArrayList<>();
        lenders.add(buildLender(0.049, 1000));
        lenders.add(buildLender(0.075, 5600));
        lenders.add(buildLender(0.025, 5000));
        lenders.add(buildLender(0.075, 5550));
        lenders.add(buildLender(0.025, 4000));
        lenders.add(buildLender(0.034, 1200));
        lenders.add(buildLender(0.100, 8200));

        // when
        Map<BigDecimal, Long> amountsByRate = LenderDataUtils.groupingAmountsByRate(lenders);

        // then
        assertEquals(amountsByRate.size(), 5);
        assertArrayEquals(amountsByRate.keySet().toArray(), new BigDecimal[] {
                valueOf(0.025), valueOf(0.034), valueOf(0.049), valueOf(0.075), valueOf(0.1)
        });

        assertEquals(amountsByRate.get(valueOf(0.025)).longValue(), 9000L);
        assertEquals(amountsByRate.get(valueOf(0.075)).longValue(), 11150);
    }

    @Ignore("Too expensive to create so many instances")
    @Test(expected = RuntimeException.class)
    public void groupingAmountByRateExceedsLongMaxValue() {

        List<LenderDto> lenders = LongStream.range(0L, 4294967300L)
                .mapToObj(i -> buildLender(0.25, Integer.MAX_VALUE))
                .collect(Collectors.toList());
        LenderDataUtils.groupingAmountsByRate(lenders);
    }

    private LenderDto buildLender(double rate, int amount) {
        return new LenderDto("", valueOf(rate), amount);
    }
}
