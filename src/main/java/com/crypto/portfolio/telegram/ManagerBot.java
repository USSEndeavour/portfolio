package com.crypto.portfolio.telegram;

import com.crypto.portfolio.entities.CashOfficeOperation;
import com.crypto.portfolio.utils.cashofficeoperation.OperationType;
import com.crypto.portfolio.utils.user.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static com.crypto.portfolio.telegram.ManagerBotServiceImpl.cashInRequestTemplate;

@Component
public class ManagerBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.username}")
    private String botUsername;

    private ManagerBotService service;

    public ManagerBot(ManagerBotService service) {
        this.service = service;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
        public void onUpdateReceived(Update update) {
        // if update is command
        if (update.hasMessage() && update.getMessage().isCommand() && service.validateUserByAnyRole(update.getMessage().getFrom())
                && !service.validateMessageIsPrivate(update.getMessage())) {
            handleCommand(update.getMessage());

        // if update is callback
        } else if (update.hasCallbackQuery() && service.validateUserByAnyRole(update.getCallbackQuery().getFrom())) {
            handleCallbackQuery(update.getCallbackQuery());

        // if update is message
        } else if (update.hasMessage()){
            handleMessage(update.getMessage());
        }
    }

    public void handleCallbackQuery(CallbackQuery query) {
        Message parentMessage = query.getMessage().getReplyToMessage();
        if (query.getFrom().equals(parentMessage.getFrom()) && service.getRequestBodyType(parentMessage)=="cashIn"
        && service.findUserByTelegramId(parentMessage.getFrom().getId()).getUserRole() == UserRole.CLIENT) {
            handleCashInCallbackQuery(query);
        }
    }

    public void handleCommand(Message msg) {
        if (msg.getText().equals("/cashin@natalie_manager_bot")){
           handleCashInCommand(msg);
        } else if (msg.getText().equals("/cashout@natalie_manager_bot")) {
            handleCashOutCommand(msg);
        }
    }

    public void handleMessage(Message message) {
        if (message.isUserMessage()) {
            handlePrivateMessage(message);

        } else if (!message.isUserMessage() && service.getRequestBodyType(message)=="cashIn"
                && service.validateUserCashInRequestMessageBodyIsValid(message)
                && service.findUserByTelegramId(message.getFrom().getId()).getUserRole() == UserRole.CLIENT) {
            requestCashInConfirmation(message);

        } else if (!message.isUserMessage() && service.validateUserByAnyRole(message.getFrom())
                && service.findUserByTelegramId(message.getFrom().getId()).getUserRole() == UserRole.CASH_OFFICE_MANAGER) {
            handleCashOfficeManagerCodeResponse(message);

        } else if (!message.isUserMessage() && !service.validateClient(message.getFrom())) {
            replyMessage(message, "user ID: " + message.getFrom().getId() + "\ngroup ID: "
                    + message.getChat().getId() +
                    "\n@" + message.getFrom().getUserName() + "\nнезарегистрированный пользователь!");
        } else {
            replyMessage(message, "Неверный формат ввода!");
        }
    }


    // Handles cash-in request. Asks the client to provide the parameters: amount, currency and location
    public void handleCashInCommand(Message msg) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId((msg.getChat().getId()).toString())
                .replyToMessageId(msg.getMessageId())
                .text("@"+msg.getFrom().getUserName() + " напишите запрос в формате: "
                        + cashInRequestTemplate).build();

        try {
            execute(sendMessage);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void handleCashOutCommand(Message msg) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId((msg.getChat().getId()).toString())
                .replyToMessageId(msg.getMessageId())
                .text("@"+msg.getFrom().getUserName() + " напишите запрос в формате: "
                        + ManagerBotServiceImpl.cashOutRequestTemplate).build();

        try {
            execute(sendMessage);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    // Warn the client not to send private messages to the bot.
    public void handlePrivateMessage(Message msg) {

        User sender = msg.getFrom();
        if (service.validateClient(sender)) {
            replyMessage(msg,"@" + sender.getUserName() + " \nid: " + sender.getId() + ", приватные сообщения запрещены!" +
                        "\nОбратитесь, пожалуйста, в чат с дежурным менеджером!");
        } else {
            replyMessage(msg, "@" + sender.getUserName() + " \nid: "
                    + sender.getId() + " \nвы не зарегистрированы в системе!");
        }
    }

    public void replyMessage(Message message, String text) {
        Integer messageId = message.getMessageId();
        Long chatId = message.getChat().getId();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .replyToMessageId(messageId)
                .text(text).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(Message message) {
        DeleteMessage deleteMessage = new DeleteMessage(message.getChat().getId().toString(), message.getMessageId());
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.err.println(e);
        }
    }

    public void requestCashInConfirmation(Message message) {
        var confirm = InlineKeyboardButton.builder()
                .text("подтвердить").callbackData("confirm")
                .build();
        var reject = InlineKeyboardButton.builder()
                .text("отменить").callbackData("reject")
                .build();

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(confirm))
                .keyboardRow(List.of(reject)).build();

        // check if cash-in request has all parameters assigned
        if (service.validateRequestBodyNotNull(message) && service.validateRequestAmountIsValid(message)) {
            HashMap<String, String> parameters = service.parseUserRequest(message.getText());
            String amount = parameters.get("Amount");
            String ticker = parameters.get("Ticker");
            String location = parameters.get("Location");
            String replyText = "Подтверждаете запрос на занос " + amount + " " + ticker
                    + " в локации: " + location + "?";
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(message.getChat().getId().toString())
                    .replyToMessageId(message.getMessageId())
                    .text(replyText).replyMarkup(keyboard).build();
            try {
                execute(sendMessage);                        //Actually sending the message
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);      //Any error will be printed here
            }
        } else {
            replyMessage(message, " необходимо правильно указать все параметры: сумма, валюта, локация ");
        }
    }

    public void handleCashInCallbackQuery(CallbackQuery query) {
        Message parentMessage = query.getMessage().getReplyToMessage();
        HashMap<String, String> requestBody = service.parseUserRequest(parentMessage.getText());
        var amount = requestBody.get("Amount");
        var ticker = requestBody.get("Ticker");
        var location = requestBody.get("Location");

        int operationId;

        if (query.getData().equals("confirm")) {
            User sender = query.getMessage().getFrom();
            operationId = service.addNewCashOfficeOperation(parentMessage, OperationType.CASH_IN);
            try {
                sendCodeRequestToCashOfficeManagerGroup(parentMessage, operationId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            replyMessage(parentMessage, "Ваш запрос в обработке:\n" + (new BigDecimal(amount)).toString() + " " + ticker + " " + location);
            deleteMessage(query.getMessage());
        } else if (query.getFrom().equals(parentMessage.getFrom()) && service.getRequestBodyType(parentMessage)=="cashIn") {
            User sender = query.getMessage().getFrom();
            replyMessage(parentMessage, " ваш запрос отменен ");
            deleteMessage(query.getMessage());
        }
    }

    public void sendCodeRequestToCashOfficeManagerGroup(Message message, Integer requestId) {
        com.crypto.portfolio.entities.User group = service.findUserByName(ManagerBotServiceImpl.cashOfficeGroupName);
        com.crypto.portfolio.entities.User client = service.findUserByTelegramId(message.getFrom().getId());
        HashMap<String, String> requestParams = service.parseUserRequest(message.getText());
        String text = "id:" +requestId + "\n" + ManagerBotServiceImpl.cashInRequestTemplate.replace("СУММА", requestParams.get("Amount"))
                .replace("ВАЛЮТА", requestParams.get("Ticker"))
                .replace(("ГОРОД"),requestParams.get("Location")).replaceAll("\"","");
        SendMessage sendMessage = SendMessage.builder()
                .chatId((group.getTelegramGroupId().toString()))
                .text(text).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleCashOfficeManagerCodeResponse(Message message) {
        Long code = service.parseCashOfficeManagerResponse(message.getText());
        System.out.println((message.getReplyToMessage().getText()).replace("\n", " ").split(":")[1].split(" ")[0]);
        var operationId = Long.parseLong((message.getReplyToMessage().getText()).replace("\n", " ").split(":")[1].split(" ")[0]);
        CashOfficeOperation operation = service.findCashOfficeOperationById(operationId);
        var groupId = operation.getRequestMessageGroupId();
        var senderId = operation.getRequestSenderTelegramId();
        var messageId = operation.getRequestMessageId();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(groupId.toString())
                .replyToMessageId(Integer.parseInt(messageId.toString()))
                .text(code.toString()).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        

    }
}