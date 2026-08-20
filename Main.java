import java.time.LocalDateTime;
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
class Account {
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
}
class BankLedger {
    private TreeMap<Integer, Account> accounts = new TreeMap<>();
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
}
public class Main {
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        BankLedger bank = new BankLedger();
        while (true) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println("                 SECUREBANK - CONSOLE MENU");
            System.out.println("============================================================");
            System.out.println("1. Add Account");
            System.out.println("2. Add Money (Deposit)");
            System.out.println("3. Debit Money (Withdrawal)");
            System.out.println("4. Display User Statement");
            System.out.println("5. Exit");
            System.out.println("============================================================");
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
                    System.out.println(
                            "Exiting SecureBank. Goodbye!"
                    );
                    scanner.close();
                    return;
                default:
                    System.out.println(
                            "[ERROR] Invalid option. Please choose 1-5."
                    );
            }
        }
    }
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
}