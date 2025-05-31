package com.crypto.portfolio;

import com.crypto.portfolio.telegram.ManagerBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class PortfolioApplication {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(PortfolioApplication.class, args);
	}

}
