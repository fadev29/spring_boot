package com.example.todolist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private int status; // status
    private List<T> data;// data todolist
    private int totalPages;
    private int currentPage;
    private int size;
    private long totalElements;

    public PaginatedResponse(int status, Page<T> page){
        this.status = status;
        this.data = page.getContent();// get data todolist
        this.totalPages = page.getTotalPages();//method get total
        this.currentPage = page.getNumber();// method get current
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
    }
}
