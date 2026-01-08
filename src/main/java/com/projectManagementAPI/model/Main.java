package com.projectManagementAPI.model;

import com.projectManagementAPI.model.controller.BoardController;
import com.projectManagementAPI.model.repository.BoardRepository;
import com.projectManagementAPI.model.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.port;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        port(8000);

        BoardRepository boardRepository = new BoardRepository();
        BoardService boardService = new BoardService(boardRepository);
        new BoardController(boardService);
        log.info("main started");

    }
}