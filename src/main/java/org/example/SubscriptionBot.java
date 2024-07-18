package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashSet;
import java.util.Set;

public class SubscriptionBot extends TelegramLongPollingBot {

    private Set<Long> subscribers = new HashSet<>();

    @Override
    public void onUpdateReceived(Update update) {
        // Если сообщение от пользователя
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/subscribe")) {
                subscribers.add(chatId);
                sendMessage(chatId, "Вы подписались на рассылку.");
            } else if (messageText.equals("/unsubscribe")) {
                subscribers.remove(chatId);
                sendMessage(chatId, "Вы отписались от рассылки.");
            } else {
                sendMessage(chatId, "Неизвестная команда.");
            }
        }

        // Если сообщение из канала
        if (update.getChannelPost() != null && update.getChannelPost().hasText()) {
            String channelMessage = update.getChannelPost().getText();
            for (Long subscriber : subscribers) {
                sendMessage(subscriber, channelMessage);
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "@kostick2006aye_bot"; // Замените на имя вашего бота
    }

    @Override
    public String getBotToken() {
        return "6464426082:AAFNax76s_XQfTwwoyTDSQbrqjIf5vY3r9k"; // Замените на токен вашего бота
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new SubscriptionBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}