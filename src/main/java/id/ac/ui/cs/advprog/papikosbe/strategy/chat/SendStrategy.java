package id.ac.ui.cs.advprog.papikosbe.strategy.chat;

import id.ac.ui.cs.advprog.papikosbe.model.chat.Message;
import id.ac.ui.cs.advprog.papikosbe.enums.SendType;

public interface SendStrategy {
    void send(Message message);
    SendType getType();
}
