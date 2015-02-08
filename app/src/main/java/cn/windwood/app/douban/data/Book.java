package cn.windwood.app.douban.data;

/**
 * Created by WindWood on 2015/2/8.
 */
public class Book {
    private String isbn;
    private String title;
    private boolean favorite;

    public Book(String isbn, String title, boolean favorite) {
        this.isbn = isbn;
        this.title = title;
        this.favorite = favorite;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
