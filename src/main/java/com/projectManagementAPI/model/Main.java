package com.projectManagementAPI.model;

import com.projectManagementAPI.model.controller.BoardController;
import com.projectManagementAPI.model.repository.BoardRepository;
import com.projectManagementAPI.model.service.BoardService;

import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        port(8000);

        BoardRepository boardRepository = new BoardRepository();
        BoardService boardService = new BoardService(boardRepository);
        new BoardController(boardService);
        System.out.println("server is up");

    }
}