package org.shuting.coincombination.app.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shuting.coincombination.app.model.CoinResponse;
import org.shuting.coincombination.app.service.CoinCalculator;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoinResourceTest {

    @Mock
    private CoinCalculator mockCoinCalculator;

    private CoinResource coinResource;

    @BeforeEach
    void setUp() {
        coinResource = new CoinResource(mockCoinCalculator);
    }

    @Test
    @DisplayName("should return valid coin combination for a valid amount")
    void getCoinCombination_validAmount_shouldReturnCombinations() {
        String amountStr = "7.03";
        String coinStr = "0.01,0.5,1,5,10";

        BigDecimal amount = new BigDecimal(amountStr);
        List<BigDecimal> coins = Arrays.asList(
                new BigDecimal("0.01"),
                new BigDecimal("0.5"),
                new BigDecimal("1"),
                new BigDecimal("5"),
                new BigDecimal("10")
        );

        List<BigDecimal> resultList = Arrays.asList(
                new BigDecimal("0.01"),
                new BigDecimal("0.01"),
                new BigDecimal("0.01"),
                new BigDecimal("1"),
                new BigDecimal("1"),
                new BigDecimal("5")
        );

        when(mockCoinCalculator.isWithinRange(amount)).thenReturn(true);
        when(mockCoinCalculator.isCoinListLegal(coins)).thenReturn(true);
        when(mockCoinCalculator.isBiggerThanFirstCoin(amount, coins)).thenReturn(true);
        when(mockCoinCalculator.hasNoMoreThanTwoDecimalPlaces(amount)).thenReturn(true);
        when(mockCoinCalculator.calculateCoins(amount, coins)).thenReturn(resultList);

        String expectedMessage = String.format(
                "Input:\nTarget amount: %s\nCoin denominator: %s\nOutput: \n%s",
                amount, coins, resultList
        );

        CoinResponse expectedResponse = new CoinResponse(expectedMessage);

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(mockCoinCalculator).isWithinRange(amount);
        verify(mockCoinCalculator).isCoinListLegal(coins);
        verify(mockCoinCalculator).isBiggerThanFirstCoin(amount, coins);
        verify(mockCoinCalculator).hasNoMoreThanTwoDecimalPlaces(amount);
        verify(mockCoinCalculator).calculateCoins(amount, coins);
    }

    @Test
    @DisplayName("should return error for invalid amount format")
    void getCoinCombination_invalidAmountFormat_shouldReturnError() {
        String amountStr = "abc";
        String coinStr = "1,2,5";
        CoinResponse expectedResponse = new CoinResponse("Invalid amount format.");

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verifyNoInteractions(mockCoinCalculator);
    }

    @Test
    @DisplayName("should return error for empty coin denominations")
    void getCoinCombination_emptyCoinList_shouldReturnError() {
        String amountStr = "1";
        String coinStr = "";

        CoinResponse expectedResponse = new CoinResponse("Coin denominations list cannot be empty.");

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verifyNoInteractions(mockCoinCalculator);
    }

    @Test
    @DisplayName("should return error for invalid coin denomination format")
    void getCoinCombination_invalidCoinFormat_shouldReturnError() {
        String amountStr = "1";
        String coinStr = "1,abc,5";

        CoinResponse expectedResponse = new CoinResponse("Invalid coin denomination format.");

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verifyNoInteractions(mockCoinCalculator);
    }

    @Test
    @DisplayName("should return error when amount is out of range")
    void getCoinCombination_amountOutOfRange_shouldReturnError() {
        String amountStr = "1000";
        String coinStr = "1,2,5";

        BigDecimal amount = new BigDecimal(amountStr);
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("5"));

        when(mockCoinCalculator.isWithinRange(amount)).thenReturn(false);

        CoinResponse expectedResponse = new CoinResponse("Target amount out of range!");

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(mockCoinCalculator).isWithinRange(amount);
        verify(mockCoinCalculator, never()).isCoinListLegal(any());
        verify(mockCoinCalculator, never()).isBiggerThanFirstCoin(any(), any());
        verify(mockCoinCalculator, never()).hasNoMoreThanTwoDecimalPlaces(any());
        verify(mockCoinCalculator, never()).calculateCoins(any(), any());
    }

    @Test
    @DisplayName("should return error when coin list illegal")
    void getCoinCombination_coinListIllegal_shouldReturnError() {
        String amountStr = "10";
        String coinStr = "1,2,5";

        BigDecimal amount = new BigDecimal(amountStr);
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("5"));

        when(mockCoinCalculator.isWithinRange(amount)).thenReturn(true);
        when(mockCoinCalculator.isCoinListLegal(coins)).thenReturn(false);

        CoinResponse expectedResponse = new CoinResponse("Invalid coin denomination format.");

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(mockCoinCalculator).isWithinRange(amount);
        verify(mockCoinCalculator).isCoinListLegal(coins);
        verify(mockCoinCalculator, never()).isBiggerThanFirstCoin(any(), any());
        verify(mockCoinCalculator, never()).hasNoMoreThanTwoDecimalPlaces(any());
        verify(mockCoinCalculator, never()).calculateCoins(any(), any());
    }

    @Test
    @DisplayName("should return error when amount is not bigger than first coin")
    void getCoinCombination_notBiggerThanFirstCoin_shouldReturnError() {
        String amountStr = "0.005";
        String coinStr = "0.01,0.05,0.1";

        BigDecimal amount = new BigDecimal(amountStr);
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("0.01"), new BigDecimal("0.05"), new BigDecimal("0.1"));

        when(mockCoinCalculator.isWithinRange(amount)).thenReturn(true);
        when(mockCoinCalculator.isCoinListLegal(coins)).thenReturn(true);
        when(mockCoinCalculator.isBiggerThanFirstCoin(amount, coins)).thenReturn(false);

        CoinResponse expectedResponse = new CoinResponse("No coin combination can make up the amount.");

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(mockCoinCalculator).isWithinRange(amount);
        verify(mockCoinCalculator).isCoinListLegal(coins);
        verify(mockCoinCalculator).isBiggerThanFirstCoin(amount, coins);
        verify(mockCoinCalculator, never()).hasNoMoreThanTwoDecimalPlaces(any());
        verify(mockCoinCalculator, never()).calculateCoins(any(), any());
    }

    @Test
    @DisplayName("should return error when amount has more than two decimal places")
    void getCoinCombination_moreThanTwoDecimalPlaces_shouldReturnError() {
        String amountStr = "1.234";
        String coinStr = "0.01,0.05,0.1";

        BigDecimal amount = new BigDecimal(amountStr);
        List<BigDecimal> coins = Arrays.asList(new BigDecimal("0.01"), new BigDecimal("0.05"), new BigDecimal("0.1"));

        when(mockCoinCalculator.isWithinRange(amount)).thenReturn(true);
        when(mockCoinCalculator.isCoinListLegal(coins)).thenReturn(true);
        when(mockCoinCalculator.isBiggerThanFirstCoin(amount, coins)).thenReturn(true);
        when(mockCoinCalculator.hasNoMoreThanTwoDecimalPlaces(amount)).thenReturn(false);

        CoinResponse expectedResponse = new CoinResponse("No coin combination can make up the amount.");

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(mockCoinCalculator).isWithinRange(amount);
        verify(mockCoinCalculator).isCoinListLegal(coins);
        verify(mockCoinCalculator).isBiggerThanFirstCoin(amount, coins);
        verify(mockCoinCalculator).hasNoMoreThanTwoDecimalPlaces(amount);
        verify(mockCoinCalculator, never()).calculateCoins(any(), any());
    }

    @Test
    @DisplayName("should handle empty result list correctly")
    void getCoinCombination_emptyResultList_shouldReturnCorrectMessage() {
        String amountStr = "0.01";
        String coinStr = "0.01";

        BigDecimal amount = new BigDecimal(amountStr);
        List<BigDecimal> coins = Collections.singletonList(new BigDecimal("0.01"));
        List<BigDecimal> resultList = Collections.emptyList();

        when(mockCoinCalculator.isWithinRange(amount)).thenReturn(true);
        when(mockCoinCalculator.isCoinListLegal(coins)).thenReturn(true);
        when(mockCoinCalculator.isBiggerThanFirstCoin(amount, coins)).thenReturn(true);
        when(mockCoinCalculator.hasNoMoreThanTwoDecimalPlaces(amount)).thenReturn(true);
        when(mockCoinCalculator.calculateCoins(amount, coins)).thenReturn(resultList);

        String expectedMessage = String.format(
                "Input:\nTarget amount: %s\nCoin denominator: %s\nOutput: \n%s",
                amount, coins, resultList
        );

        CoinResponse expectedResponse = new CoinResponse(expectedMessage);

        CoinResponse actualResponse = coinResource.getCoinCombination(amountStr, coinStr);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(mockCoinCalculator).calculateCoins(amount, coins);
    }
}