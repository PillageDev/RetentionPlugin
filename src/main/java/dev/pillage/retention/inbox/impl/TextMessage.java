package dev.pillage.retention.inbox.impl;

import dev.pillage.retention.inbox.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TextMessage extends Message {

    private final String content;
    private final String title;
    private final String sender;

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
