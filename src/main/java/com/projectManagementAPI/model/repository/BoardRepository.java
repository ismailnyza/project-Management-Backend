package com.projectManagementAPI.model.repository;


import com.projectManagementAPI.model.Board;

import java.util.ArrayList;
import java.util.List;

public class BoardRepository {
    private List<Board> boards = new ArrayList<>();

   public List<Board> getAllBoards(){
       return boards;
   }

   public Board save(Board board){
       boards.add(board);
       return board;
   }

   public Board findById(Long id){
       return boards.stream().filter(board -> board.getId() == id).findFirst().orElse(null);
   }

   public Void delete(Board board){
       boards.remove(board);
       return null;
   }
}