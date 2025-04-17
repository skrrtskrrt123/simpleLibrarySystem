// Book class extending LibraryItem
public class Book extends LibraryItem {
    private String author;
    private String isbn;
    private static final double DAILY_LATE_FEE = 1.00;

    public Book(String title, String id, String author, String isbn) {
        super(title, id);
        this.author = author;
        this.isbn = isbn;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate * DAILY_LATE_FEE;
    }
}