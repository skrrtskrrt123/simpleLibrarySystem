/**
 * Loan - Manages Library Item Borrowing Transactions
 * 
 * This class handles the borrowing process and associated events as required by
 * Requirement 3 (Event Handling and Polymorphism).
 * 
 * Key Features:
 * - Tracks borrowing dates and due dates
 * - Handles multiple items per loan (1..* relationship)
 * - Implements onBorrow and onReturn events
 * - Calculates late fees using polymorphic method calls
 * 
 * Event Handlers:
 * - onBorrow(): Updates item status and logs transaction
 * - onReturn(): Processes returns and calculates fees
 */

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Loan class to manage borrowing transactions
public class Loan {
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private List<LibraryItem> items;

    public Loan(Member member, LibraryItem item) {
        this.member = member;
        this.items = new ArrayList<>();  // Initialize the list
        this.items.add(item);           // Add the initial item
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(14); // 2-week loan period
    }

    public void onBorrow() {
        for (LibraryItem item : items) {
            item.setStatus(LibraryItem.ItemStatus.BORROWED);
            System.out.println("Item borrowed: " + item.getTitle() + " by " + member.getName());
        }
        System.out.println("Due date: " + dueDate);
    }

    public void onReturn() {
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }
        for (LibraryItem item : items) {
            item.setStatus(LibraryItem.ItemStatus.AVAILABLE);
            if (returnDate.isAfter(dueDate)) {
                long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
                double lateFee = item.calculateLateFee((int) daysLate);
                System.out.println("Late fee charged: $" + String.format("%.2f", lateFee));
                System.out.println("Days late: " + daysLate);
            }
            System.out.println("Item returned: " + item.getTitle());
        }
    }

    public LibraryItem getItem() { 
        return items.isEmpty() ? null : items.get(0); 
    }

    public List<LibraryItem> getItems() { 
        return items; 
    }

    public LocalDate getDueDate() { return dueDate; }

    public void setReturnDate(LocalDate date) { this.returnDate = date; }
}