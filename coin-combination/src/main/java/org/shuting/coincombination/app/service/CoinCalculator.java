package org.shuting.coincombination.app.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CoinCalculator {
    private final List<BigDecimal> legalDenominations = Arrays.asList(
            new BigDecimal("0.01"), new BigDecimal("0.05"), new BigDecimal("0.1"),
            new BigDecimal("0.2"), new BigDecimal("0.5"), new BigDecimal("1"),
            new BigDecimal("2"), new BigDecimal("5"), new BigDecimal("10"),
            new BigDecimal("50"), new BigDecimal("100"), new BigDecimal("1000")
    );

    public boolean isWithinRange (BigDecimal amount){
        return amount.compareTo(BigDecimal.ZERO) >= 0 && amount.compareTo(new BigDecimal("10000.00")) <= 0;
    }

    public boolean isBiggerThanFirstCoin (BigDecimal amount, List<BigDecimal> coins){
        return amount.compareTo(coins.get(0)) > 0;
    }

    public boolean isCoinListLegal (List<BigDecimal> coins) {
        return coins != null && !coins.isEmpty() && coins.stream().allMatch(legalDenominations::contains);
    }

    public boolean hasNoMoreThanTwoDecimalPlaces(BigDecimal amount){
        return amount.stripTrailingZeros().scale() <= 2;
    }

//    public int findBiggestCoin (BigDecimal amount, List<BigDecimal> coins) {
//        for (int i = 0; i < coins.size(); i++){
//            BigDecimal divisor = coins.get(i);
//            int quotient = amount.divideToIntegralValue(divisor).intValue();
//            if (quotient < 1){
//                return i - 1;
//            }
//        }
//        return coins.size() - 1;
//    }

//    public List<BigDecimal> calculateCoins(BigDecimal amount, List<BigDecimal> denominations){
//        Map<BigDecimal, Integer> countCoin = new LinkedHashMap<>();
//        BigDecimal remaining = amount;
//
//        List<BigDecimal> sorted = new ArrayList<>(denominations);
//        sorted.sort(Comparator.naturalOrder());
//
//        int biggestCoinIndex = findBiggestCoin(amount, sorted);
//
//        for (int i = biggestCoinIndex; i >= 0; i--){
//            BigDecimal divisor = sorted.get(i);
//            int quotient = remaining.divideToIntegralValue(divisor).intValue();
//            if (quotient > 0){
//                countCoin.put(divisor, quotient);
//                remaining = remaining.remainder(divisor);
//            }
//            if (remaining.compareTo(BigDecimal.ZERO) == 0) {
//                break;
//            }
//        }
//
//        if (remaining.compareTo(BigDecimal.ZERO) == 0){
//            List<BigDecimal> result = new ArrayList<>();
//            for (Map.Entry<BigDecimal, Integer> entry : countCoin.entrySet()){
//                for (int i = 0; i < entry.getValue(); i++) {
//                    result.add(entry.getKey());
//                }
//            }
//            result.sort(Comparator.naturalOrder());
//            return result;
//        }
//        return calculateCoinsWithDP(amount, denominations);
//    }

    public List<BigDecimal> calculateCoins(BigDecimal amount, List<BigDecimal> denominations) {
        int target = amount.multiply(BigDecimal.valueOf(100)).intValueExact();

        List<Integer> coinsInCents = denominations.stream()
                .map(c -> c.multiply(BigDecimal.valueOf(100)).intValueExact())
                .sorted()
                .collect(Collectors.toList());

        int[] dp = new int[target + 1];
        int[] lastCoin = new int[target + 1];
        Arrays.fill(dp, Integer.MAX_VALUE - 1);
        dp[0] = 0;

        for (int i = 1; i <= target; i++){
            for (int coin : coinsInCents) {
                if (i >= coin && dp[i - coin] + 1 < dp[i]) {
                    dp[i] = dp[i - coin] + 1;
                    lastCoin[i] = coin;
                }
            }
        }

        if (dp[target] == Integer.MAX_VALUE - 1) {
            throw new IllegalArgumentException("No coin combination can make up the amount.");
        }

        List<BigDecimal> result = new ArrayList<>();
        for (int j = target; j > 0; j -= lastCoin[j]) {
            if (lastCoin[j] == 0) {
                throw new IllegalArgumentException("No valid combination found.");
            }
            result.add(BigDecimal.valueOf(lastCoin[j]).divide(BigDecimal.valueOf(100)));
        }

        result.sort(Comparator.naturalOrder());
        return result;
    }
}
