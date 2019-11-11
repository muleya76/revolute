package com.revolut.exercise.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.exercise.dao.AccountService;
import com.revolut.exercise.model.MoneyServiceResponse;
import com.revolut.exercise.model.UserTransaction;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class MoneyTransferService implements HttpHandler {

	private static final Logger log = Logger.getLogger(MoneyTransferService.class);

	private  final AccountService accountService;
	private final ObjectMapper objectMapper;
	private final Executor executorService;

	public MoneyTransferService(AccountService accountService, ObjectMapper mapper, Executor executorService) {

		this.accountService = accountService;
		this.objectMapper = mapper;
		this.executorService = executorService;
	}

	/*
	@POST
	public MoneyServiceResponse transferFund(UserTransaction transaction) {

		Objects.requireNonNull(transaction);
		return accountService.transferAccountBalance(transaction);
	}
	*/

	@Override
	public void handle(HttpExchange exchange)  {
		executorService.execute(() -> handleRequest(exchange));
	}

	private void handleRequest(HttpExchange exchange) {
		log.info("Received request with type "+ exchange.getRequestMethod());

		try {
			MoneyServiceResponse response = null;
			UserTransaction transaction = objectMapper.readValue(exchange.getRequestBody(), UserTransaction.class);
			Objects.requireNonNull(transaction);
			log.info("Received request with TransactionId "+ transaction.getTransactionId());
			if ("POST".equals(exchange.getRequestMethod()))
				response = accountService.transferAccountBalance(transaction);
			else
				response = new MoneyServiceResponse(true, Optional.of("Request Type : " + exchange.getRequestMethod()
						+ " not supported. TransactionId : " + transaction.getTransactionId()));

				OutputStream os = exchange.getResponseBody();
				os.write(objectMapper.writeValueAsBytes(response));
				os.close();
				log.info("Response sent for request with TransactionId " + transaction.getTransactionId());

		} catch (Exception ex) {
			log.error("Error in processing request :" + ex.getMessage());
			ex.printStackTrace();
		}

	}

}
