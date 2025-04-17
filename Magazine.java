// Magazine class extending LibraryItem
public class Magazine extends LibraryItem {
    private String issueNumber;
    private static final double DAILY_LATE_FEE = 0.50;

    public Magazine(String title, String id, String issueNumber) {
        super(title, id);
        this.issueNumber = issueNumber;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate * DAILY_LATE_FEE;
    }
}