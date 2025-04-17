/**
 * Member - Library Member Management Class
 * 
 * This class represents library members and manages their loan relationships.
 * Implements the Member-Loan relationship (1..*) as specified in Requirement 1.
 * 
 * Key Features:
 * - Manages member identification
 * - Tracks all loans associated with the member
 * - Supports multiple simultaneous loans
 */

import java.util.List;
import java.util.ArrayList;

// Member class to represent library members
public class Member {
    private String memberId;
    private String name;
    private List<Loan> loans;

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
        this.loans = new ArrayList<>();
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    public String getMemberId() { return memberId; }

    public String getName() { return name; }

    public List<Loan> getLoans() { return loans; }
}