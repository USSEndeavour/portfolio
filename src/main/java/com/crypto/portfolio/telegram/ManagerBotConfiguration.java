//package com.crypto.portfolio.telegram;
//
//import org.springframework.context.annotation.Configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Configuration
//public class ManagerBotConfiguration {
//
//    @Bean
//    public ManagerBot managerBot() {
//        return new ManagerBot(managerBotService());
//    }
//
//    @Bean
//    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
//        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//        botsApi.registerBot(managerBot());
//        return botsApi;
//    }
//
//    @Bean
//    public ManagerBotService managerBotService() {
//        return new ManagerBotServiceImpl();
//    }
//}
