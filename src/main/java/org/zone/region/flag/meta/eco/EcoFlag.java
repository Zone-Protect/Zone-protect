package org.zone.region.flag.meta.eco;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.zone.region.flag.Flag;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.meta.eco.transaction.DepositTransaction;
import org.zone.region.flag.meta.eco.transaction.TransactionBuilder;
import org.zone.region.flag.meta.eco.transaction.TransactionState;
import org.zone.region.flag.meta.eco.transaction.WithdrawTransaction;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Flag used to hold the zones balance
 */
public class EcoFlag implements Flag {

    private final @NotNull Map<Currency, BigDecimal> money = new HashMap<>();

    public EcoFlag() {
        this(new HashMap<>());
    }

    public EcoFlag(Map<? extends Currency, ? extends Number> money) {
        this.money.putAll(money
                .entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(),
                        BigDecimal.valueOf(entry.getValue().doubleValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey,
                        AbstractMap.SimpleImmutableEntry::getValue)));
    }

    /**
     * Gets all balances found within the zone.
     *
     * @return A map of all balances within the zone separated by currency
     */
    public @NotNull Map<Currency, BigDecimal> getMoney() {
        return Collections.unmodifiableMap(this.money);
    }

    /**
     * Gets the balance found within the zone
     *
     * @param currency The currency to get
     *
     * @return Gets the balance from the zone, if the zone does not contain that currency, a balance of 0 will be used unless there is no eco plugin found, in that case it will be {@link Double#MAX_VALUE}
     */
    public @NotNull BigDecimal getMoney(@NotNull Currency currency) {
        return this.money.getOrDefault(currency,
                BigDecimal.valueOf(Sponge
                        .serviceProvider()
                        .provide(EconomyService.class)
                        .isPresent() ? 0 : Double.MAX_VALUE));
    }

    /**
     * Checks if the flag has the specified balance
     *
     * @param currency The currency to check
     * @param amount   The amount to check
     *
     * @return If the zone has that much, returns true if no eco plugin was found
     */
    public boolean hasBalance(@NotNull Currency currency, @NotNull BigDecimal amount) {
        return this.getMoney(currency).compareTo(amount) >= 0;
    }

    /**
     * Sets the balance of a specific currency. This will override the current value. Will not take affect if no currency plugin was found
     *
     * @param currency The currency to use
     * @param amount   The amount to set
     */
    public void setBalance(@NotNull Currency currency, @NotNull BigDecimal amount) {
        if (Sponge.serviceProvider().provide(EconomyService.class).isEmpty()) {
            return;
        }
        if (this.money.containsKey(currency)) {
            this.money.replace(currency, amount);
            return;
        }
        this.money.put(currency, amount);
    }

    /**
     * Adds the specified amount into the zone
     *
     * @param currency The currency to use
     * @param amount   The amount to deposit
     *
     * @return The transaction
     */
    public @NotNull DepositTransaction deposit(
            @NotNull Currency currency, @NotNull BigDecimal amount) {
        if (Sponge.serviceProvider().provide(EconomyService.class).isEmpty()) {
            return new DepositTransaction(new TransactionBuilder()
                    .setFlag(this)
                    .setAfter(BigDecimal.valueOf(Double.MAX_VALUE))
                    .setOriginal(BigDecimal.valueOf(Double.MAX_VALUE))
                    .setState(TransactionState.FAIL));
        }
        BigDecimal money = this.getMoney(currency);
        BigDecimal nextMoney = money.add(amount);
        this.setBalance(currency, money);
        return new DepositTransaction(new TransactionBuilder()
                .setState(TransactionState.SUCCESS)
                .setOriginal(money)
                .setAfter(nextMoney)
                .setFlag(this));
    }

    /**
     * Removes the specified amount into the zone. If the zone does not have enough or no economy plugin is found then the transaction will fail
     *
     * @param currency The currency to use
     * @param amount   The amount to withdraw
     *
     * @return The transaction
     */
    public @NotNull WithdrawTransaction withdraw(
            @NotNull Currency currency, @NotNull BigDecimal amount) {
        if (Sponge.serviceProvider().provide(EconomyService.class).isEmpty()) {
            return new WithdrawTransaction(new TransactionBuilder()
                    .setFlag(this)
                    .setAfter(BigDecimal.valueOf(Double.MAX_VALUE))
                    .setOriginal(BigDecimal.valueOf(Double.MAX_VALUE))
                    .setState(TransactionState.FAIL));
        }
        BigDecimal money = this.getMoney(currency);
        BigDecimal nextMoney = money.subtract(amount);
        this.setBalance(currency, money);
        return new WithdrawTransaction(new TransactionBuilder()
                .setState(TransactionState.SUCCESS)
                .setOriginal(money)
                .setAfter(nextMoney)
                .setFlag(this));
    }

    @Override
    public @NotNull EcoFlagType getType() {
        return FlagTypes.ECO;
    }
}
