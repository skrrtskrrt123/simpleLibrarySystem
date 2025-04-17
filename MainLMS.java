/**
 * MainLMS - Main Library Management System Class
 * 
 * This class serves as the primary interface and control center for the Library Management System.
 * It implements both member and librarian interfaces using Java Swing for the GUI.
 * 
 * Key Features:
 * - Dual interface (Member/Librarian) with role-based access
 * - Real-time dashboard for loan tracking and fee calculation
 * - Integrated borrowing and return management
 * - Dynamic status updates for library items
 * 
 * Requirements Addressed:
 * - Event Handling: Manages borrowing and return events
 * - Polymorphism: Uses LibraryItem's polymorphic methods for fee calculation
 * - User Interface: Provides GUI for system interaction
 */

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MainLMS {
    private List<LibraryItem> items = new ArrayList<>();
    private List<Member> members = new ArrayList<>();
    private JTextArea outputArea;
    private Member currentMember;
    private JFrame currentFrame;
    private JPanel dashboardPanel;

    public MainLMS() {
        initializeLibrary();
        showLoginDialog();
    }

    private void initializeLibrary() {
        items.add(new Book("A Little Life", "B001", "Hanya Yanagihara", "978-0385539258"));
        items.add(new Book("The Midnight Library", "B002", "Matt Haig", "978-0525559474"));
        items.add(new Book("Project Hail Mary", "B003", "Andy Weir", "978-0593135204"));
        items.add(new Magazine("Mastika", "M001", "January 2025"));
        items.add(new DVD("The Hunger Games", "D001", "Gary Ross"));

        // Initialize members
        Member aleesya = new Member("A001", "Aleesya Najwa");
        members.add(aleesya);
        members.add(new Member("A002", "Amirul Danial"));
        members.add(new Member("A003", "Alya Natasha"));
        members.add(new Member("A004", "Arieq Danish"));

        // Create an overdue loan for Aleesya (a sample)
        LibraryItem book = findItem("B001"); // A Little Life
        if (book != null) {
            Loan overdueLoan = new Loan(aleesya, book) {
                    // Override getDueDate to simulate an overdue loan
                    @Override
                    public LocalDate getDueDate() {
                        return LocalDate.now().minusDays(5); // 5 days overdue
                    }
                };
            overdueLoan.onBorrow();
            aleesya.addLoan(overdueLoan);
        }
    }

    // Updates the dashboard display with current loans and fees
    private void showDashboard() {
        if (dashboardPanel != null) {
            dashboardPanel.removeAll();
        } else {
            dashboardPanel = new JPanel(new BorderLayout());
        }

        // Create dashboard content
        JPanel statsPanel = new JPanel(new GridLayout(3, 1));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Dashboard"));

        // Show borrowed items, due dates, and fees
        StringBuilder dashboardText = new StringBuilder();
        dashboardText.append("Current Loans:\n");
        if (currentMember != null) {
            double totalFees = 0;
            for (Loan loan : currentMember.getLoans()) {
                LocalDate dueDate = loan.getDueDate();
                LocalDate now = LocalDate.now();
                long daysLate = 0;
                double fee = 0;

                if (now.isAfter(dueDate)) {
                    daysLate = ChronoUnit.DAYS.between(dueDate, now);
                    fee = loan.getItem().calculateLateFee((int) daysLate);
                    totalFees += fee;
                }

                dashboardText.append(String.format("%s - Due: %s\n", 
                        loan.getItem().getTitle(),
                        dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

                if (daysLate > 0) {
                    dashboardText.append(String.format("    Days Late: %d, Late Fee: $%.2f\n", 
                            daysLate, fee));
                }
            }

            if (totalFees > 0) {
                dashboardText.append(String.format("\nTotal Outstanding Fees: $%.2f\n", totalFees));
            }
        }

        JTextArea dashboardArea = new JTextArea(dashboardText.toString());
        dashboardArea.setEditable(false);
        statsPanel.add(new JScrollPane(dashboardArea));

        dashboardPanel.add(statsPanel, BorderLayout.NORTH);
        currentFrame.add(dashboardPanel, BorderLayout.SOUTH);
        currentFrame.revalidate();
        currentFrame.repaint();
    }

    private void showLoginDialog() {
        JFrame loginFrame = new JFrame("Library System Login");
        loginFrame.setSize(300, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel roleLabel = new JLabel("Select your role:");
        JButton memberBtn = new JButton("Member");
        JButton librarianBtn = new JButton("Librarian");

        panel.add(roleLabel);
        panel.add(memberBtn);
        panel.add(librarianBtn);

        memberBtn.addActionListener(e -> {
                    String memberId = JOptionPane.showInputDialog("Enter Member ID (A001-A004):");
                    if (memberId != null) {
                        for (Member member : members) {
                            if (member.getMemberId().equals(memberId)) {
                                currentMember = member;
                                loginFrame.dispose(); 
                                showMemberInterface();
                                return;
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Invalid Member ID");
                    }
            });

        librarianBtn.addActionListener(e -> {
                    loginFrame.dispose(); 
                    showLibrarianInterface();
            });

        loginFrame.add(panel);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    // Handles the item borrow process with fee calculation
    private void borrowItem() {
        String itemId = JOptionPane.showInputDialog("Enter Item ID to borrow:");
        if (itemId != null) {
            LibraryItem selectedItem = findItem(itemId);
            if (selectedItem != null) {
                if (selectedItem.getStatus() == LibraryItem.ItemStatus.AVAILABLE) {
                    Loan loan = new Loan(currentMember, selectedItem);
                    currentMember.addLoan(loan);
                    loan.onBorrow();
                    showFeedbackMessage("Borrowing Successful!", "Item borrowed successfully.");
                    showDashboard(); // Refresh dashboard
                } else {
                    showFeedbackMessage("Item not available", "This item is currently borrowed.");
                }
            } else {
                showFeedbackMessage("Error", "Item not found.");
            }
        }
    }

    // Handles the item return process with fee calculation
    private void returnItem() {
        if (currentMember.getLoans().isEmpty()) {
            showFeedbackMessage("No items to return", "You have no borrowed items.");
            return;
        }

        String[] items = currentMember.getLoans().stream()
            .map(loan -> loan.getItem().getId() + " - " + loan.getItem().getTitle())
            .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                currentFrame,
                "Select item to return:",
                "Return Item",
                JOptionPane.QUESTION_MESSAGE,
                null,
                items,
                items[0]);

        if (selected != null) {
            String itemId = selected.split(" - ")[0];
            for (Loan loan : currentMember.getLoans()) {
                if (loan.getItem().getId().equals(itemId)) {
                    LocalDate now = LocalDate.now();
                    LocalDate dueDate = loan.getDueDate();
                    double fee = 0;

                    if (now.isAfter(dueDate)) {
                        long daysLate = ChronoUnit.DAYS.between(dueDate, now);
                        fee = loan.getItem().calculateLateFee((int) daysLate);
                        showFeedbackMessage("Late Return Fee", 
                            String.format("Late fee charged: $%.2f\nDays late: %d", fee, daysLate));
                    }

                    loan.onReturn();
                    currentMember.getLoans().remove(loan);
                    showFeedbackMessage("Return Successful!", 
                        fee > 0 ? "Item returned successfully. Please pay the late fee." 
                        : "Item returned successfully.");
                    showDashboard(); // Refresh dashboard
                    return;
                }
            }
        }
    }

    private void showFeedbackMessage(String title, String message) {
        JOptionPane.showMessageDialog(currentFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private LibraryItem findItem(String id) {
        return items.stream()
        .filter(item -> item.getId().equals(id))
        .findFirst()
        .orElse(null);
    }

    private void showMemberInterface() {
        JFrame memberFrame = new JFrame("₊‧°𐐪♡𐑂°‧₊ Member Interface - " + currentMember.getName() + " ₊‧°𐐪♡𐑂°‧₊");
        currentFrame = memberFrame;
        memberFrame.setSize(600, 500);
        memberFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Button panel at the top
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton viewItemsBtn = new JButton("View Available Items");
        JButton borrowBtn = new JButton("Borrow Item");
        JButton returnBtn = new JButton("Return Item");
        JButton viewLoansBtn = new JButton("View My Loans");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(viewItemsBtn);
        buttonPanel.add(borrowBtn);
        buttonPanel.add(returnBtn);
        buttonPanel.add(viewLoansBtn);
        buttonPanel.add(logoutBtn);

        // Main content area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        viewItemsBtn.addActionListener(e -> displayAvailableItems());
        borrowBtn.addActionListener(e -> borrowItem());
        returnBtn.addActionListener(e -> returnItem());
        viewLoansBtn.addActionListener(e -> displayCurrentLoans());
        logoutBtn.addActionListener(e -> logout());

        memberFrame.add(mainPanel);
        memberFrame.setLocationRelativeTo(null);
        memberFrame.setVisible(true);

        // Show initial dashboard
        showDashboard();
    }

    private void showLibrarianInterface() {
        JFrame librarianFrame = new JFrame("₊‧°𐐪♡𐑂°‧₊ Librarian Interface ₊‧°𐐪♡𐑂°‧₊");
        currentFrame = librarianFrame;  // Store current frame for logout
        librarianFrame.setSize(500, 400);
        librarianFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10)); 
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton viewAllItemsBtn = new JButton("View All Items");
        JButton viewMembersBtn = new JButton("View All Members");
        JButton viewLoansBtn = new JButton("View Current Loans");
        JButton logoutBtn = new JButton("Logout");  // New logout button

        buttonPanel.add(viewAllItemsBtn);
        buttonPanel.add(viewMembersBtn);
        buttonPanel.add(viewLoansBtn);
        buttonPanel.add(logoutBtn);  // Add logout button

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        librarianFrame.setLayout(new BorderLayout());
        librarianFrame.add(buttonPanel, BorderLayout.NORTH);
        librarianFrame.add(scrollPane, BorderLayout.CENTER);

        viewAllItemsBtn.addActionListener(e -> displayAllItems());
        viewMembersBtn.addActionListener(e -> displayAllMembers());
        viewLoansBtn.addActionListener(e -> displayCurrentLoans());
        logoutBtn.addActionListener(e -> logout());  // Add logout action

        librarianFrame.setLocationRelativeTo(null);
        librarianFrame.setVisible(true);
    }

    private void logout() {
        currentFrame.dispose();  // Close current window
        currentMember = null;    // Reset current member
        showLoginDialog();       // Show login dialog again
    }

    private void displayAvailableItems() {
        StringBuilder sb = new StringBuilder("Available Items:\n\n");
        for (LibraryItem item : items) {
            if (item.getStatus() == LibraryItem.ItemStatus.AVAILABLE) {
                sb.append(String.format("ID: %s - %s\n", item.getId(), item.getTitle()));
            }
        }
        outputArea.setText(sb.toString());
    }

    private void displayAllItems() {
        StringBuilder sb = new StringBuilder("All Items:\n\n");
        for (LibraryItem item : items) {
            sb.append(String.format("ID: %s - %s - Status: %s\n", 
                    item.getId(), item.getTitle(), item.getStatus()));
        }
        outputArea.setText(sb.toString());
    }

    private void displayAllMembers() {
        StringBuilder sb = new StringBuilder("All Members:\n\n");
        for (Member member : members) {
            sb.append(String.format("ID: %s - %s\n", member.getMemberId(), member.getName()));
        }
        outputArea.setText(sb.toString());
    }

    private void displayCurrentLoans() {
        StringBuilder sb = new StringBuilder("Current Loans:\n\n");
        for (Member member : members) {
            for (Loan loan : member.getLoans()) {
                sb.append(String.format("Member: %s - Item: %s (ID: %s)\n",
                        member.getName(), loan.getItem().getTitle(), loan.getItem().getId()));
            }
        }
        outputArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainLMS());
    }
}