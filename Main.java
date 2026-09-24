import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;

class Transaction {

    private LocalDateTime timestamp;
    private String type;
    private double amount;
    private String description;

    public Transaction(LocalDateTime timestamp,
                       String type,
                       double amount,
                       String description) {

        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}


/*
 * Account implements Comparable<Account>
 *
 * Natural ordering:
 * Accounts are sorted by Account ID.
 */
class Account implements Comparable<Account> {

    private Integer id;
    private String customerName;
    private double balance;

    private NavigableMap<LocalDateTime, Transaction> transactions;

    public Account(Integer id,
                   String customerName,
                   double balance) {

        this.id = id;
        this.customerName = customerName;
        this.balance = balance;
        this.transactions = new TreeMap<>();
    }

    public Integer getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getBalance() {
        return balance;
    }

    public NavigableMap<LocalDateTime, Transaction> getTransactions() {
        return transactions;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /*
     * Comparable:
     * Natural ordering is based on Account ID.
     */
    @Override
    public int compareTo(Account other) {
        return Integer.compare(this.id, other.id);
    }
}


/*
 * BankLedger
 *
 * Main banking operations + Week 4 sorting and
 * Stream-based portfolio summary.
 */
class BankLedger {

    /*
     * TreeMap automatically keeps accounts sorted by ID.
     */
    private TreeMap<Integer, Account> accounts = new TreeMap<>();


    // ============================================================
    // OPERATION 1 - ADD ACCOUNT
    // ============================================================

    public void addAccount(int id,
                           String name,
                           double initialBalance) {

        Account account =
                new Account(id, name, initialBalance);

        accounts.put(id, account);

        System.out.printf(
                "[SUCCESS] Account %d created for %s | Balance: $%,.2f%n",
                id,
                name,
                initialBalance
        );
    }


    // ============================================================
    // OPERATION 2 - DEPOSIT
    // ============================================================

    public void addMoney(int accountId,
                         double amount,
                         LocalDateTime time,
                         String description) {

        Account account =
                accounts.get(accountId);

        if (account == null) {

            System.out.println(
                    "[ERROR] Account not found."
            );

            return;
        }

        account.setBalance(
                account.getBalance() + amount
        );

        Transaction transaction =
                new Transaction(
                        time,
                        "CREDIT",
                        amount,
                        description
                );

        account.getTransactions()
                .put(time, transaction);

        System.out.printf(
                "[SUCCESS] Account %d credited with +$%,.2f | New Balance: $%,.2f%n",
                accountId,
                amount,
                account.getBalance()
        );
    }


    // ============================================================
    // OPERATION 3 - WITHDRAWAL
    // ============================================================

    public void debitMoney(int accountId,
                           double amount,
                           LocalDateTime time,
                           String description) {

        Account account =
                accounts.get(accountId);

        if (account == null) {

            System.out.println(
                    "[ERROR] Account not found."
            );

            return;
        }

        if (account.getBalance() >= amount) {

            account.setBalance(
                    account.getBalance() - amount
            );

            Transaction transaction =
                    new Transaction(
                            time,
                            "DEBIT",
                            amount,
                            description
                    );

            account.getTransactions()
                    .put(time, transaction);

            System.out.printf(
                    "[SUCCESS] Account %d debited with -$%,.2f | New Balance: $%,.2f%n",
                    accountId,
                    amount,
                    account.getBalance()
            );

        } else {

            System.out.println(
                    "[ERROR] Insufficient funds."
            );
        }
    }


    // ============================================================
    // OPERATION 4 - ACCOUNT STATEMENT
    // ============================================================

    public NavigableMap<LocalDateTime, Transaction>
    getStatement(int accountId,
                 LocalDateTime startDate,
                 LocalDateTime endDate) {

        Account account =
                accounts.get(accountId);

        if (account == null) {
            return new TreeMap<>();
        }

        return account.getTransactions()
                .subMap(
                        startDate,
                        true,
                        endDate,
                        true
                );
    }


    public Account getAccount(int accountId) {
        return accounts.get(accountId);
    }


    // ============================================================
    // WEEK 4 - SORTED LEDGER
    // ============================================================

    /*
     * Natural ordering.
     *
     * Account implements Comparable<Account>,
     * so TreeMap keeps accounts ordered by Account ID.
     */
    public void printNaturalOrderLedger() {

        System.out.println();
        System.out.println(
                "============================================================"
        );
        System.out.println(
                "              SORTED LEDGER - NATURAL ORDER"
        );
        System.out.println(
                "              (Account ID - Comparable)"
        );
        System.out.println(
                "============================================================"
        );

        System.out.printf(
                "%-12s %-20s %15s%n",
                "ACCOUNT ID",
                "CUSTOMER",
                "BALANCE"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        accounts.values()
                .stream()
                .forEach(account ->
                        System.out.printf(
                                "%-12d %-20s $%,12.2f%n",
                                account.getId(),
                                account.getCustomerName(),
                                account.getBalance()
                        )
                );

        System.out.println(
                "------------------------------------------------------------"
        );

        if (!accounts.isEmpty()) {

            System.out.println(
                    "First Account ID : " + accounts.firstKey()
            );

            System.out.println(
                    "Last Account ID  : " + accounts.lastKey()
            );
        }
    }


    // ============================================================
    // WEEK 4 - COMPARATOR LEDGER
    // ============================================================

    /*
     * Alternate ordering using Comparator.
     *
     * Accounts are sorted by balance.
     */
    public void printBalanceOrderLedger() {

        System.out.println();
        System.out.println(
                "============================================================"
        );
        System.out.println(
                "              SORTED LEDGER - BALANCE ORDER"
        );
        System.out.println(
                "              (Account Balance - Comparator)"
        );
        System.out.println(
                "============================================================"
        );

        System.out.printf(
                "%-12s %-20s %15s%n",
                "ACCOUNT ID",
                "CUSTOMER",
                "BALANCE"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        accounts.values()
                .stream()
                .sorted(
                        Comparator.comparingDouble(
                                Account::getBalance
                        )
                )
                .forEach(account ->
                        System.out.printf(
                                "%-12d %-20s $%,12.2f%n",
                                account.getId(),
                                account.getCustomerName(),
                                account.getBalance()
                        )
                );

        System.out.println(
                "------------------------------------------------------------"
        );
    }


    // ============================================================
    // WEEK 4 - RANGE QUERY USING subMap()
    // ============================================================

    /*
     * Returns accounts from startId (inclusive)
     * to endId (exclusive).
     *
     * Example:
     * subMap(100, 104)
     *
     * returns:
     * 101
     * 103
     */
    public void printAccountRange(int startId,
                                  int endId) {

        System.out.println();
        System.out.println(
                "============================================================"
        );
        System.out.println(
                "                  ACCOUNT RANGE QUERY"
        );
        System.out.println(
                "============================================================"
        );

        System.out.println(
                "Range: " + startId +
                " (inclusive) to " +
                endId +
                " (exclusive)"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        NavigableMap<Integer, Account> range =
                accounts.subMap(
                        startId,
                        true,
                        endId,
                        false
                );

        if (range.isEmpty()) {

            System.out.println(
                    "No accounts found in this range."
            );

            return;
        }

        range.values()
                .stream()
                .forEach(account ->
                        System.out.printf(
                                "Account ID: %d | Customer: %s | Balance: $%,.2f%n",
                                account.getId(),
                                account.getCustomerName(),
                                account.getBalance()
                        )
                );

        System.out.println(
                "------------------------------------------------------------"
        );
    }


    // ============================================================
    // WEEK 4 - PORTFOLIO SUMMARY
    // ============================================================

    /*
     * Portfolio Summary is calculated using Streams.
     *
     * No manual accumulator loop is used.
     */
    public void printPortfolioSummary() {

        System.out.println();
        System.out.println(
                "============================================================"
        );
        System.out.println(
                "                  PORTFOLIO SUMMARY"
        );
        System.out.println(
                "============================================================"
        );


        // --------------------------------------------------------
        // TOTAL BALANCE
        // --------------------------------------------------------

        double totalBalance =
                accounts.values()
                        .stream()
                        .map(Account::getBalance)
                        .reduce(
                                0.0,
                                Double::sum
                        );

        System.out.printf(
                "Total Portfolio Balance : $%,.2f%n",
                totalBalance
        );


        // --------------------------------------------------------
        // TOP 5 ACCOUNTS BY BALANCE
        // --------------------------------------------------------

        System.out.println();
        System.out.println(
                "Top 5 Accounts by Balance:"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        accounts.values()
                .stream()
                .sorted(
                        Comparator.comparingDouble(
                                Account::getBalance
                        ).reversed()
                )
                .limit(5)
                .forEach(account ->
                        System.out.printf(
                                "Account %d | %-20s | $%,.2f%n",
                                account.getId(),
                                account.getCustomerName(),
                                account.getBalance()
                        )
                );

        System.out.println(
                "------------------------------------------------------------"
        );
    }
}


/*
 * Main class
 */
public class Main {

    static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {

        BankLedger bank = new BankLedger();

        while (true) {

            System.out.println();

            System.out.println(
                    "============================================================"
            );

            System.out.println(
                    "                 SECUREBANK - CONSOLE MENU"
            );

            System.out.println(
                    "============================================================"
            );

            System.out.println("1. Add Account");
            System.out.println("2. Add Money (Deposit)");
            System.out.println("3. Debit Money (Withdrawal)");
            System.out.println("4. Display User Statement");
            System.out.println("5. Natural Sorted Ledger");
            System.out.println("6. Balance Sorted Ledger");
            System.out.println("7. Account Range Query");
            System.out.println("8. Portfolio Summary");
            System.out.println("9. Exit");

            System.out.println(
                    "============================================================"
            );

            System.out.print("Select Option: ");

            int choice =
                    Integer.parseInt(scanner.nextLine());


            switch (choice) {

                case 1:
                    addAccountMenu(bank);
                    break;

                case 2:
                    depositMenu(bank);
                    break;

                case 3:
                    withdrawalMenu(bank);
                    break;

                case 4:
                    statementMenu(bank);
                    break;

                case 5:
                    bank.printNaturalOrderLedger();
                    break;

                case 6:
                    bank.printBalanceOrderLedger();
                    break;

                case 7:
                    rangeQueryMenu(bank);
                    break;

                case 8:
                    bank.printPortfolioSummary();
                    break;

                case 9:

                    System.out.println(
                            "Exiting SecureBank. Goodbye!"
                    );

                    scanner.close();

                    return;

                default:

                    System.out.println(
                            "[ERROR] Invalid option. Please choose 1-9."
                    );
            }
        }
    }


    // ============================================================
    // ADD ACCOUNT MENU
    // ============================================================

    public static void addAccountMenu(BankLedger bank) {

        System.out.print("Enter Account ID: ");

        int id =
                Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Customer Name: ");

        String name =
                scanner.nextLine();

        System.out.print("Enter Initial Balance: ");

        double balance =
                Double.parseDouble(scanner.nextLine());

        bank.addAccount(
                id,
                name,
                balance
        );
    }


    // ============================================================
    // DEPOSIT MENU
    // ============================================================

    public static void depositMenu(BankLedger bank) {

        System.out.print("Enter Account ID: ");

        int accountId =
                Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Amount: ");

        double amount =
                Double.parseDouble(scanner.nextLine());

        System.out.print(
                "Enter Date-Time (YYYY-MM-DDTHH:MM:SS): "
        );

        LocalDateTime time =
                LocalDateTime.parse(
                        scanner.nextLine()
                );

        System.out.print("Enter Description: ");

        String description =
                scanner.nextLine();

        bank.addMoney(
                accountId,
                amount,
                time,
                description
        );
    }


    // ============================================================
    // WITHDRAWAL MENU
    // ============================================================

    public static void withdrawalMenu(BankLedger bank) {

        System.out.print("Enter Account ID: ");

        int accountId =
                Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Amount: ");

        double amount =
                Double.parseDouble(scanner.nextLine());

        System.out.print(
                "Enter Date-Time (YYYY-MM-DDTHH:MM:SS): "
        );

        LocalDateTime time =
                LocalDateTime.parse(
                        scanner.nextLine()
                );

        System.out.print("Enter Description: ");

        String description =
                scanner.nextLine();

        bank.debitMoney(
                accountId,
                amount,
                time,
                description
        );
    }


    // ============================================================
    // STATEMENT MENU
    // ============================================================

    public static void statementMenu(BankLedger bank) {

        System.out.print("Enter Account ID: ");

        int accountId =
                Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Start Date-Time: ");

        LocalDateTime startDate =
                LocalDateTime.parse(
                        scanner.nextLine()
                );

        System.out.print("Enter End Date-Time: ");

        LocalDateTime endDate =
                LocalDateTime.parse(
                        scanner.nextLine()
                );

        Account account =
                bank.getAccount(accountId);

        if (account == null) {

            System.out.println(
                    "[ERROR] Account not found."
            );

            return;
        }

        NavigableMap<LocalDateTime, Transaction>
                statement =
                bank.getStatement(
                        accountId,
                        startDate,
                        endDate
                );

        System.out.println();

        System.out.println(
                "============================================================"
        );

        System.out.printf(
                " ACCOUNT STATEMENT: %d (%s)%n",
                account.getId(),
                account.getCustomerName()
        );

        System.out.printf(
                " Filter Period: %s to %s%n",
                startDate.toLocalDate(),
                endDate.toLocalDate()
        );

        System.out.println(
                "============================================================"
        );

        System.out.println(
                "DATE & TIME | TYPE | AMOUNT | DESCRIPTION"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        for (Map.Entry<LocalDateTime, Transaction> entry
                : statement.entrySet()) {

            Transaction transaction =
                    entry.getValue();

            String sign;

            if (transaction.getType().equals("CREDIT")) {
                sign = "+";
            } else {
                sign = "-";
            }

            System.out.printf(
                    "%s | %s | %s$%,.2f | %s%n",
                    transaction.getTimestamp(),
                    transaction.getType(),
                    sign,
                    transaction.getAmount(),
                    transaction.getDescription()
            );
        }

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.printf(
                "Statement complete (%d transaction(s) found in date range)%n",
                statement.size()
        );
    }


    // ============================================================
    // RANGE QUERY MENU
    // ============================================================

    public static void rangeQueryMenu(BankLedger bank) {

        System.out.print(
                "Enter starting Account ID (inclusive): "
        );

        int startId =
                Integer.parseInt(scanner.nextLine());

        System.out.print(
                "Enter ending Account ID (exclusive): "
        );

        int endId =
                Integer.parseInt(scanner.nextLine());

        bank.printAccountRange(
                startId,
                endId
        );
    }
}