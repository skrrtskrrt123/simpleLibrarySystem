/**
 * LibraryItem - Abstract Base Class for Library Items
 * 
 * This abstract class serves as the foundation for all library items (books, magazines, DVDs).
 * It implements the core functionality required by Requirement 1 (Class Design and Hierarchy).
 * 
 * Key Features:
 * - Abstract method for late fee calculation (polymorphic implementation)
 * - Status tracking (Available/Borrowed)
 * - Common attributes for all library items
 * 
 * Design Patterns:
 * - Template Method: calculateLateFee() is implemented differently by each subclass
 * - State Pattern: ItemStatus enum manages item availability
 */

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Abstract base class for library items
public abstract class LibraryItem {
    private String title;
    private String id;
    private ItemStatus status;

    public enum ItemStatus {
        AVAILABLE,
        BORROWED
    }

    public LibraryItem(String title, String id) {
        this.title = title;
        this.id = id;
        this.status = ItemStatus.AVAILABLE;
    }

    public abstract double calculateLateFee(int daysLate);

    public String getTitle() { return title; }

    public String getId() { return id; }

    public ItemStatus getStatus() { return status; }

    public void setStatus(ItemStatus status) { this.status = status; }
}