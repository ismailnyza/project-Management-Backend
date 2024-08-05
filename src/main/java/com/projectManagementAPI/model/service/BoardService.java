package com.projectManagementAPI.model.service;

import com.projectManagementAPI.model.Board;
import com.projectManagementAPI.model.repository.BoardRepository;

import java.util.List;

public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> getAllBoards(){
        return boardRepository.getAllBoards();
    }
    public Board createBoard(Board board){
        return boardRepository.save(board);
    }

    public Board getBoardById(Long id){
        return boardRepository.findById(id);
    }

    public void deleteBoard(Long id){
        Board board = boardRepository.findById(id);
        boardRepository.delete(board);
    }
}