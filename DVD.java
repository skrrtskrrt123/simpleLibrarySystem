// DVD class extending LibraryItem
public class DVD extends LibraryItem {
    private String director;
    private static final double DAILY_LATE_FEE = 2.00;

    public DVD(String title, String id, String director) {
        super(title, id);
        this.director = director;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate * DAILY_LATE_FEE;
    }
}