package com.tpc.tpcgestpaie.localapp.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaginatedResponse<T> {
    private boolean success;
    private String message;
    private T data;

    @JsonProperty("currentPage")
    private int currentPage;

    @JsonProperty("pageSize")
    private int pageSize;

    @JsonProperty("totalItems")
    private long totalItems;

    @JsonProperty("totalPages")
    private int totalPages;

    @JsonProperty("isLast")
    private boolean isLast;

    public PaginatedResponse(boolean success, String message, T data,
                             int currentPage, int pageSize, long totalItems,
                             int totalPages, boolean isLast) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.isLast = isLast;
    }

    // Getters et Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isLast() { return isLast; }
    public void setLast(boolean last) { isLast = last; }
}