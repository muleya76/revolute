package com.revolut.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.exercise.dao.AccountService;
import com.revolut.exercise.dao.AccountServiceImpl;
import com.revolut.exercise.dao.DataStoreFactory;
import com.revolut.exercise.dao.FxService;
import com.revolut.exercise.service.MoneyTransferService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Application {

    private static final Logger log = Logger.getLogger(Application.class);
    private static final int port = 8500;

    public static void main(String[] args) throws IOException {
        ExecutorService service =  Executors.newCachedThreadPool();
        AccountService accountService = new AccountServiceImpl(DataStoreFactory.getAccountDataStore(1), new FxService() {});
        MoneyTransferService moneyTransferService = new MoneyTransferService(accountService, new ObjectMapper(), service);

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            service.shutdown();
            log.info("Server shutting down..");
        }));

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost",  port), 0);
        HttpContext context = server.createContext("/transaction");
        context.setHandler(moneyTransferService);
        server.setExecutor(null);
        log.info("Server started om address : "+ server.getAddress());
        server.start();


    }
}
