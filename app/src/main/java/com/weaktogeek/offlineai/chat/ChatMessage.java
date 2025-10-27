package com.weaktogeek.offlineai.chat;

import java.util.UUID;

public class ChatMessage {
    private final String id;
    private final String text;
    private final boolean isSentByUser;
    private final long timestamp;

    public ChatMessage(String text, boolean isSentByUser) {
        this(UUID.randomUUID().toString(), text, isSentByUser, System.currentTimeMillis());
    }

    public ChatMessage(String id, String text, boolean isSentByUser, long timestamp) {
        this.id = id;
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // equals/hashCode are helpful for DiffUtil
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;

        if (isSentByUser != that.isSentByUser) return false;
        if (timestamp != that.timestamp) return false;
        if (!id.equals(that.id)) return false;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + (isSentByUser ? 1 : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
