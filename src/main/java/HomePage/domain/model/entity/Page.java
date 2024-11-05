package HomePage.domain.model.entity;

import java.util.List;

public class Page<T>{
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private int pageSize;

    public Page(List<T> content, int currentPage, int totalPages, int pageSize) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
    }

    public boolean hasNext() {
          return currentPage < totalPages;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }

    public List<T> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }
}
