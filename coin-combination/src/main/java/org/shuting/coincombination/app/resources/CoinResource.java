package org.shuting.coincombination.app.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.shuting.coincombination.app.model.CoinResponse;
import org.shuting.coincombination.app.service.CoinCalculator;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Path("/coins")
@Produces(MediaType.APPLICATION_JSON)
public class CoinResource {

    private CoinCalculator coinCalculator = new CoinCalculator();

    public CoinResource(CoinCalculator coinCalculator) {
        this.coinCalculator = coinCalculator;
    }

    @GET
    @Path("/{amount}")
    public CoinResponse getCoinCombination(@PathParam("amount") String amountStr, @QueryParam("coins") String coinStr) {

        BigDecimal amount;
        List<BigDecimal> coins;
        List<BigDecimal> resultList;

        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e){
            return new CoinResponse("Invalid amount format.");
        }

        try {
            if (coinStr == null || coinStr.isEmpty()) {
                return new CoinResponse("Coin denominations list cannot be empty.");
            }
            coins = Arrays.stream(coinStr.split(",")).map(String::trim).map(BigDecimal::new).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return new CoinResponse("Invalid coin denomination format.");
        }

        if (!coinCalculator.isWithinRange(amount)){
            return new CoinResponse("Target amount out of range!");
        } else if (!coinCalculator.isCoinListLegal(coins)){
            return new CoinResponse("Invalid coin denomination format.");
        } else if (!coinCalculator.isBiggerThanFirstCoin(amount, coins) || !coinCalculator.hasNoMoreThanTwoDecimalPlaces(amount)) {
            return new CoinResponse("No coin combination can make up the amount.");
        } else {
            try {
                resultList = coinCalculator.calculateCoins(amount, coins);
            } catch (IllegalArgumentException e) {
                return new CoinResponse(e.getMessage());
            }
            return buildResponse(resultList, coins, amount);
        }
    }

    private CoinResponse buildResponse(List<BigDecimal> resultList, List<BigDecimal> denominatorList,BigDecimal amount){
        String message = String.format("Input:\nTarget amount: %s\nCoin denominator: %s\nOutput: \n%s", amount, denominatorList, resultList);

        return new CoinResponse(message);
    }
}
