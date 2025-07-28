package com.crypto.portfolio.telegram;

import com.crypto.portfolio.entities.CashOfficeOperation;
import com.crypto.portfolio.entities.Currency;
import com.crypto.portfolio.services.UserService;
import com.crypto.portfolio.utils.cashofficeoperation.OperationStatus;
import com.crypto.portfolio.utils.cashofficeoperation.OperationType;
import com.crypto.portfolio.utils.currencies.CurrencyTicker;
import com.crypto.portfolio.utils.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Service;

import org.telegram.telegrambots.meta.api.objects.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.function.Predicate;

@Service
public class ManagerBotServiceImpl implements ManagerBotService {

    @Autowired
    private UserService service;

    private User user;

    private Message message;

    private Chat chat;

    private RestTemplate defaultClient = new RestTemplate();

    private String getUserByIdUrl = "http://localhost:8080/users/telegramId/{id}";
    private String getUserByNameUrl = "http://localhost:8080/users/userName/{userName}";
    private String postNewOperationUrl = "http://localhost:8080/cashofficeoperations/add";
    private String getCashOfficeOperationById = "http://localhost:8080/cashofficeoperations/{id}";
    private String getCashOfficeOperationByPasscode = "http://localhost:8080/cashofficeoperations/passcode/{passCode}";
    private String updateCashOfficeOperationById = "http://localhost:8080/cashofficeoperations/update/{id}";

    private String getCurrencyByTicker = "http://localhost:8080/currencies/ticker/{ticker}";

    static String cashInRequestTemplate = "\n\"Прошу код на занос: СУММА, ВАЛЮТА, ГОРОД\"";
    static String cashOutRequestTemplate = "\n\"Прошу код на выдачу: СУММА, ВАЛЮТА, ГОРОД\"";
    static String cashOfficeGroupName = "CashOfficeManagerGroup";
    static String cashInConfirmation = "занесли СУММА по коду КОД";
    static String cashOutConfirmation = "выдали СУММА по коду КОД";

    @Override
    public void handleUpdate(Update update) {}

    public boolean validateClient(User user) {
        return validateUserByRole(user, UserRole.CLIENT);
    }

    public boolean validateCashOfficeManager(User user) {
       return validateUserByRole(user, UserRole.CASH_OFFICE_MANAGER);
    }

    public boolean validateUserByRole(User user, UserRole role) {
        boolean valid = false;
        try {
            com.crypto.portfolio.entities.User client = findUserByTelegramId(user.getId());
            valid = client.getUserRole().equals(role);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return valid;
    }

    public boolean validateUserByAnyRole(User user) {
        boolean valid = false;
        try {
            com.crypto.portfolio.entities.User client = findUserByTelegramId(user.getId());
            valid = client.getUserRole().equals(UserRole.CLIENT) || client.getUserRole().equals(UserRole.MANAGER)
                    || client.getUserRole().equals(UserRole.CASH_OFFICE_MANAGER)
                    || client.getUserRole().equals(UserRole.ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return valid;
    }

    public boolean validateMessageIsPrivate(Message message) {
        return message.isUserMessage();
    }

    public String getRequestBodyType(Message message) {
        String flag = "";
        if (message.getText().toLowerCase().startsWith(cashInRequestTemplate.substring(2,20).toLowerCase())) {
            flag = "cashIn";
        } else if (message.getText().toLowerCase().startsWith(cashOutRequestTemplate.substring(2,21).toLowerCase())) {
            flag = "cashOut";
        }
        return flag;
    }

    public boolean validateRequestBodyNotNull(Message message) {
        boolean flag = false;
        HashMap<String, String> requestParams = new HashMap<>();
        requestParams = parseUserRequest(message.getText());
        String amount = requestParams.get("Amount");
        String ticker = requestParams.get("Ticker");
        String location = requestParams.get("Location");

        if (amount.isEmpty() || ticker.isEmpty() || location.isEmpty()) {
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    public boolean validateRequestAmountIsValid(Message message) {
        boolean flag = false;
        HashMap<String, String> requestParams = new HashMap<>();
        requestParams = parseUserRequest(message.getText());
        try {
            BigDecimal amount = new BigDecimal(requestParams.get("Amount"));
            flag = true;
        } catch (Exception e) {
            System.out.println(e.getMessage() + " неверно указана сумма " + requestParams.get("Amount"));;
        }
        return flag;
    }

    public Predicate<String> tickerIsValid = (ticker) -> {
        boolean flag = false;
        try {
            flag = (findCurrencyByTicker(ticker) != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    };

    public Predicate<String> operationCodeIsValid = (code) -> {
        boolean flag = false;
        try {
            flag = findCurrencyByTicker(code) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    };

    public boolean validateRequestCurrencyTickerIsValid(Message message, Predicate<String> predicate) {
        HashMap<String, String> userRequest = parseUserRequest(message.getText());
        return predicate.test(userRequest.get("Ticker"));
    }

    public boolean validateUserCashInRequestMessageBodyIsValid(Message message) {
        boolean flag = validateRequestBodyNotNull(message) && validateClient(message.getFrom())
                && validateRequestAmountIsValid(message) && validateRequestCurrencyTickerIsValid(message,tickerIsValid);
        return flag;
    }

    public Integer addNewCashOfficeOperation(Message message, OperationType operationType) {
        String requestBody = message.getText();
        HashMap<String, String> requestParams = parseUserRequest(requestBody);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CashOfficeOperation cashOfficeOperation = new CashOfficeOperation();
        cashOfficeOperation.setOperationQuantity(new BigDecimal(requestParams.get("Amount")));
        cashOfficeOperation.setOperationType(operationType);
        cashOfficeOperation.setOperationStatus(OperationStatus.PENDING);
        try {
            cashOfficeOperation.setCurrency(findCurrencyByTicker(requestParams.get("Ticker")));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        cashOfficeOperation.setClient(findUserByTelegramId(message.getFrom().getId()));
        cashOfficeOperation.setRequestMessageId((long) message.getMessageId());
        cashOfficeOperation.setRequestMessageGroupId(message.getChatId());
        cashOfficeOperation.setRequestSenderTelegramId(message.getFrom().getId());
        try {
            ResponseEntity<CashOfficeOperation> operation = defaultClient.exchange(postNewOperationUrl,
                    HttpMethod.POST, new HttpEntity<>(cashOfficeOperation, headers),CashOfficeOperation.class);
            cashOfficeOperation = operation.getBody();
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return cashOfficeOperation.getId();
    }

    public HashMap<String, String> parseUserRequest(String request) {
        HashMap<String, String> map = new HashMap<>();
        try {
            String[] parameters = request.split(",");
            String amount = parameters[0].split(":")[1].trim().replace(".","");
            String ticker = parameters[1].trim().toUpperCase();
            String location = parameters[2].trim().toUpperCase();
            map.put("Amount", amount);
            map.put("Ticker", ticker);
            map.put("Location", location);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return map;
    }

    public Long parseCashOfficeManagerResponse(String request) {
        Long code = 0L;
        try {
            code = Long.parseLong(request.split(" ")[1]);
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return code;
    }

    public HashMap<String, String> parseCashOfficeManagerOperationConfirmation(String confirmation) {
        HashMap<String, String> map = new HashMap<>();
        try {
            String[] parameters = confirmation.split(" ");
            String amount = parameters[1];
            String ticker = parameters[2].trim().toUpperCase();
            String passcode = parameters[5];
            map.put("Amount", amount);
            map.put("Ticker", ticker);
            map.put("Passcode", passcode);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return map;
    }

    public com.crypto.portfolio.entities.User findUserByTelegramId(Long id) {
        com.crypto.portfolio.entities.User client = new com.crypto.portfolio.entities.User();
        try {
            ResponseEntity<com.crypto.portfolio.entities.User> userResponseEntity = defaultClient
                    .exchange(getUserByIdUrl, HttpMethod.GET, null,
                            com.crypto.portfolio.entities.User.class, id);

            if (userResponseEntity.getStatusCode().is2xxSuccessful() && userResponseEntity.getBody()!=null) {
                client = userResponseEntity.getBody();
            } else {
                client = null;
            }
        } catch (Exception e) {
            System.err.println("Error validating " + UserRole.CLIENT + "\n" + e.getMessage());
        }
        return client;
    }

    public Currency findCurrencyByTicker(String ticker) {
        Currency currency = new Currency();
        if (ticker.equalsIgnoreCase(CurrencyTicker.valueOf(ticker).toString())) {
            try {
                ResponseEntity<Currency> currencyResponseEntity = defaultClient
                        .exchange(getCurrencyByTicker, HttpMethod.GET, null,
                                Currency.class, CurrencyTicker.valueOf(ticker));

                if (currencyResponseEntity.getStatusCode().is2xxSuccessful() && currencyResponseEntity.getBody()!=null) {
                    currency = currencyResponseEntity.getBody();
                } else {
                    currency = null;
                }
            } catch (Exception e) {
                System.err.println("Error retrieving currency by ticker \n" + e.getMessage());
            }
        }
        return currency;
    }

    public com.crypto.portfolio.entities.User findUserByName(String name) {
        com.crypto.portfolio.entities.User client = new com.crypto.portfolio.entities.User();
        try {
            ResponseEntity<com.crypto.portfolio.entities.User> userResponseEntity = defaultClient
                    .exchange(getUserByNameUrl, HttpMethod.GET, null,
                            com.crypto.portfolio.entities.User.class, name);
            if (userResponseEntity.getStatusCode().is2xxSuccessful() && userResponseEntity.getBody()!=null
                    && userResponseEntity.getBody().getUserRole()==UserRole.GROUP_MARKER) {
                client = userResponseEntity.getBody();
            } else {
                client = null;
            }
        } catch (Exception e) {
            System.err.println("Error validating " + UserRole.CLIENT + "\n" + e.getMessage());
        }
        return client;
        }

    public CashOfficeOperation findCashOfficeOperationById(Long id) {
        CashOfficeOperation operation = new CashOfficeOperation();
        try {
            ResponseEntity<CashOfficeOperation> operationResponseEntity = defaultClient
                    .exchange(getCashOfficeOperationById, HttpMethod.GET, null,
                            CashOfficeOperation.class, id);
            if (operationResponseEntity.getStatusCode().is2xxSuccessful() && operationResponseEntity.getBody() != null) {
                operation = operationResponseEntity.getBody();
            } else {
                operation = null;
            }
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return operation;
    }

    public CashOfficeOperation findCashOfficeOperationByOperationPasscode(String passcode) {
        CashOfficeOperation operation = new CashOfficeOperation();
        try {
            ResponseEntity<CashOfficeOperation> operationResponseEntity = defaultClient
                    .exchange(getCashOfficeOperationByPasscode, HttpMethod.GET, null,
                            CashOfficeOperation.class, passcode);
            if (operationResponseEntity.getStatusCode().is2xxSuccessful() && operationResponseEntity.getBody() != null) {
                operation = operationResponseEntity.getBody();
            } else {
                operation = null;
            }
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return operation;
    }

    public boolean validateCashOfficeOperationPasscodeAlreadyExists(String passcode) {
        CashOfficeOperation operation = findCashOfficeOperationByOperationPasscode(passcode);
        return operation !=null;
    }

    public boolean updateCashOfficeOperationById(Long id, CashOfficeOperation newOperation) {
        boolean flag = false;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            ResponseEntity<CashOfficeOperation> operationResponseEntity = defaultClient.exchange(updateCashOfficeOperationById,
                    HttpMethod.PUT, new HttpEntity<>(newOperation, headers),CashOfficeOperation.class, id);
            if (operationResponseEntity.getStatusCode().is2xxSuccessful() && operationResponseEntity.getBody() != null)
                flag = true;

        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return flag;
    }
}
