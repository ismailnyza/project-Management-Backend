package com.projectManagementAPI.model.controller;

import com.projectManagementAPI.model.entity.Board;
import com.projectManagementAPI.model.service.BoardService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class BoardController {
    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        get("/api/boards", this::getAllBoards);
        post("/api/boards", this::createBoard);
        get("/api/boards/:id", this::getBoardById);
        delete("/api/boards/:id", this::deleteBoard);
        // Additional endpoints (PUT, PATCH) can be added here
    }

    private List<Board> getAllBoards(Request req, Response res) {
        res.type("application/json");
//        return boardService.getAllBoards();
        return boardService.getAllBoards();
    }

    private Board createBoard(Request req, Response res) {
        res.type("application/json");
        String name = req.queryParams("name");
        // Here you can parse users, statuses, tasks from request parameters if needed
        Board board = new Board(null, name, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        return boardService.createBoard(board);
    }

    private Board getBoardById(Request req, Response res) {
        res.type("application/json");
        Long id = Long.parseLong(req.params("id"));
        return boardService.getBoardById(id);
    }

    private String deleteBoard(Request req, Response res) {
        res.type("application/json");
        Long id = Long.parseLong(req.params("id"));
        boardService.deleteBoard(id);
        return "{\"status\":\"success\"}";
    }
}