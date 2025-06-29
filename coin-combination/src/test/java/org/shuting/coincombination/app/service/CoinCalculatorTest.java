package org.shuting.coincombination.app.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CoinCalculatorTest {

    private final CoinCalculator coinCalculator = new CoinCalculator();

    private final List<BigDecimal> legalDenominations = Arrays.asList(
            new BigDecimal("0.01"), new BigDecimal("0.05"), new BigDecimal("0.1"),
            new BigDecimal("0.2"), new BigDecimal("0.5"), new BigDecimal("1"),
            new BigDecimal("2"), new BigDecimal("5"), new BigDecimal("10"),
            new BigDecimal("50"), new BigDecimal("100"), new BigDecimal("1000")
    );

    @Test
    public void testIsWithRange(){
        assertTrue(coinCalculator.isWithinRange(new BigDecimal("0")));
        assertTrue(coinCalculator.isWithinRange(new BigDecimal("10000.00")));
        assertFalse(coinCalculator.isWithinRange(new BigDecimal("-0.01")));
        assertFalse(coinCalculator.isWithinRange(new BigDecimal("10000.01")));
    }

    @Test
    public void testHasNoMoreThanTwoDecimalPlaces() {
        assertTrue(coinCalculator.hasNoMoreThanTwoDecimalPlaces(new BigDecimal("1.2")));
        assertTrue(coinCalculator.hasNoMoreThanTwoDecimalPlaces(new BigDecimal("1.23")));
        assertFalse(coinCalculator.hasNoMoreThanTwoDecimalPlaces(new BigDecimal("1.234")));
    }

    @Test
    public void testIsCoinListLegal(){
        List<BigDecimal> validCoins = Arrays.asList(new BigDecimal("0.01"), new BigDecimal("1"), new BigDecimal("10"));
        List<BigDecimal> invalidCoins = Arrays.asList(new BigDecimal("0.03"), new BigDecimal("1"));
        assertTrue(coinCalculator.isCoinListLegal(validCoins));
        assertFalse(coinCalculator.isCoinListLegal(invalidCoins));
        assertFalse(coinCalculator.isCoinListLegal(Collections.emptyList()));
        assertFalse(coinCalculator.isCoinListLegal(null));
    }

    @Test
    public void testIsBiggerThanFirstCoin() {
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("0.05"), new BigDecimal("0.1"));
        assertTrue(coinCalculator.isBiggerThanFirstCoin(new BigDecimal("0.08"), coins));
        assertFalse(coinCalculator.isBiggerThanFirstCoin(new BigDecimal("0.03"), coins));
    }

    @Test
    public void testCalculateCoinsWithGreedySuccess() {
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("0.01"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("5"), new BigDecimal(10));
        BigDecimal amount = new BigDecimal("7.03");

        List<BigDecimal> result = coinCalculator.calculateCoins(amount, coins);

        Map<BigDecimal, Long> countMap = result.stream().collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        assertEquals(3L, countMap.get(new BigDecimal("0.01")));
        assertEquals(2L, countMap.get(new BigDecimal("1")));
        assertEquals(1L, countMap.get(new BigDecimal("5")));
    }

    @Test
    public void testCalculateCoinsWithDPFallback() {
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("2"), new BigDecimal("5"));
        BigDecimal amount = new BigDecimal("6");

        List<BigDecimal> result = coinCalculator.calculateCoins(amount, coins);

        Map<BigDecimal, Long> countMap = result.stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        assertEquals(3L, countMap.get(new BigDecimal("2")));
        assertFalse(countMap.containsKey(new BigDecimal("5")));
    }

    @Test
    public void testCalculateCoinsImpossible() {
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("2"), new BigDecimal("5"));
        BigDecimal amount = new BigDecimal("3");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coinCalculator.calculateCoins(amount, coins);
        });

        assertEquals("No coin combination can make up the amount.", exception.getMessage());
    }

    @Test
    public void testCalculateCoinsExactSingleCoin() {
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("5"), new BigDecimal("10"));
        BigDecimal amount = new BigDecimal("10");

        List<BigDecimal> result = coinCalculator.calculateCoins(amount, coins);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("10"), result.get(0));
    }

    @Test
    public void testCalculateCoinsWithOriginalDenomination() {
        BigDecimal amount = new BigDecimal("1234.56");

        List<BigDecimal> result = coinCalculator.calculateCoins(amount, legalDenominations);

        assertNotNull(result);

        BigDecimal sum = result.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, sum.compareTo(amount));
    }
}
