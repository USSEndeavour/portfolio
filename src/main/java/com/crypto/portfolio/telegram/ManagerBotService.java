package com.crypto.portfolio.telegram;

import com.crypto.portfolio.entities.CashOfficeOperation;
import com.crypto.portfolio.entities.Currency;
import com.crypto.portfolio.utils.cashofficeoperation.OperationType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.function.Predicate;

public interface ManagerBotService {
    public void handleUpdate(Update update);
    public boolean validateClient(User user);
    public boolean validateMessageIsPrivate(Message message);
    public Integer addNewCashOfficeOperation(Message message, OperationType operationType);
    public String getRequestBodyType(Message message);
    public boolean validateRequestBodyNotNull(Message message);
    public boolean validateRequestAmountIsValid(Message message);
    public boolean validateRequestCurrencyTickerIsValid(Message message, Predicate<String> predicate);
    public boolean validateUserCashInRequestMessageBodyIsValid(Message message);
    public boolean validateCashOfficeOperationPasscodeAlreadyExists(String passcode);
    public CashOfficeOperation findCashOfficeOperationByOperationPasscode(String passcode);
    public Currency findCurrencyByTicker(String ticker);
    public HashMap<String, String> parseUserRequest(String request);
    public Long parseCashOfficeManagerResponse(String request);
    public HashMap<String, String> parseCashOfficeManagerOperationConfirmation(String confirmation);
    public com.crypto.portfolio.entities.User findUserByTelegramId(Long id);
    public com.crypto.portfolio.entities.User findUserByName(String name);
    public CashOfficeOperation findCashOfficeOperationById(Long id);
    public boolean validateUserByAnyRole(User user);
    public boolean updateCashOfficeOperationById(Long id, CashOfficeOperation newOperation);
}
