package dev.pillage.retention.inbox.impl;

import dev.pillage.retention.inbox.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MoneyMessage extends Message {

    private final double amount;
    private final String content;
    private final String title;
    private final String sender;

    @Override
    public double getMoney() {
        return amount;
    }

    @Override
    public String getMoneyContent() {
        return content;
    }

    @Override
    public String getMoneyTitle() {
        return title;
    }

    @Override
    public String getMoneySender() {
        return sender;
    }
}
