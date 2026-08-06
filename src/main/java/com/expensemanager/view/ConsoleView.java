package com.expensemanager.view;

import com.expensemanager.service.ExpenseManager;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.factory.model.WalletFactory;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.category.Category;
import com.expensemanager.factory.model.TransactionFactory;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.utils.DateUtils;
import com.expensemanager.service.StatisticsService;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Lớp hiển thị giao diện dòng lệnh (Console) cho người dùng thao tác
 * với hệ thống quản lý chi tiêu, tách biệt hoàn toàn khỏi tầng xử lý
 * nghiệp vụ để không phá vỡ kiến trúc phân lớp của dự án.
 */
public class ConsoleView {

    // Dùng chung 1 luồng đọc input duy nhất cho toàn bộ lớp,
    // tránh mở nhiều Scanner cùng đọc System.in gây xung đột dữ liệu.
    private final Scanner scanner = new Scanner(System.in);
    private final ExpenseManager manager = ExpenseManager.getInstance();
    /**
     * Vòng lặp chính điều phối chương trình: hiển thị menu, đọc lựa chọn
     * của người dùng và gọi đúng hàm xử lý tương ứng.
     */
    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleAddExpense();
                case "2" -> handleAddIncome();
                case "3" -> handleViewTransactions();
                case "4" -> handleAddWallet();
                case "5" -> handleViewWallets();
                case "6" -> handleAddCategory();
                case "7" -> handleViewCategories();
                case "8" -> handleStatistics();
                case "9" -> handleSettings();
                case "0" -> {
                    running = false;
                    System.out.println("Tạm biệt!");
                }
                default -> System.out.println("Lựa chọn không hợp lệ, thử lại.");
            }
        }
    }

    // Tách riêng phần hiển thị khỏi phần điều phối logic,
    // để sau này đổi giao diện chỉ cần sửa đúng 1 hàm này.
    private void printMenu() {
        System.out.println("\n===== QUẢN LÝ CHI TIÊU =====");
        System.out.println("[1] Thêm khoản chi (Expense)");
        System.out.println("[2] Thêm khoản thu (Income)");
        System.out.println("[3] Xem danh sách giao dịch");
        System.out.println("[4] Thêm ví");
        System.out.println("[5] Xem danh sách ví");
        System.out.println("[6] Thêm danh mục");
        System.out.println("[7] Xem danh mục");
        System.out.println("[8] Thống kê thu/chi");
        System.out.println("[9] Cài đặt");
        System.out.println("[0] Thoát");
        System.out.print("Chọn chức năng: ");
    }

    // Mỗi hàm xử lý 1 chức năng riêng biệt (thay vì viết code
    // trực tiếp trong switch), để sau này nối vào tầng Service thật
    // chỉ cần sửa nội dung bên trong từng hàm, không đụng vào
    // cấu trúc điều phối chính.
    private void handleAddExpense() {
        try {
            if (manager.getWallets().isEmpty()) {
                System.out.println("Chưa có ví. Hãy thêm ví trước.");
                return;
            }

            if (manager.getCategories().isEmpty()) {
                System.out.println("Chưa có danh mục. Hãy thêm danh mục trước.");
                return;
            }

            System.out.print("Nhập mã giao dịch: ");
            String id = scanner.nextLine().trim();

            System.out.print("Nhập số tiền chi: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Nhập ngày (dd/MM/yyyy): ");
            LocalDate date = DateUtils.parseDate(scanner.nextLine().trim());

            System.out.print("Nhập ghi chú: ");
            String note = scanner.nextLine().trim();

            System.out.print("Nhập tên danh mục: ");
            String categoryName = scanner.nextLine().trim();

            Category category = manager.findCategoryByName(categoryName);

            if (category == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy danh mục: " + categoryName
                );
            }

            System.out.print("Nhập tên ví: ");
            String walletName = scanner.nextLine().trim();

            Wallet wallet = manager.findWalletByName(walletName);

            if (wallet == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy ví: " + walletName
                );
            }

            System.out.print("Nhập phương thức thanh toán: ");
            String paymentMethod = scanner.nextLine().trim();

            Transaction transaction = TransactionFactory.createTransaction(
                    TransactionType.EXPENSE,
                    id,
                    amount,
                    date,
                    note,
                    category,
                    wallet,
                    null,
                    paymentMethod,
                    null
            );

            manager.addTransaction(transaction);

            System.out.println("Thêm khoản chi thành công.");
        } catch (Exception e) {
            System.err.println(
                    "\u001B[31mLỗi: " + e.getMessage() + "\u001B[0m"
            );
        }
    }
    private void handleAddIncome() {
        try {
            if (manager.getWallets().isEmpty()) {
                System.out.println("Chưa có ví. Hãy thêm ví trước.");
                return;
            }

            if (manager.getCategories().isEmpty()) {
                System.out.println("Chưa có danh mục. Hãy thêm danh mục trước.");
                return;
            }

            System.out.print("Nhập mã giao dịch: ");
            String id = scanner.nextLine().trim();

            System.out.print("Nhập số tiền thu: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Nhập ngày (dd/MM/yyyy): ");
            LocalDate date = DateUtils.parseDate(scanner.nextLine().trim());

            System.out.print("Nhập ghi chú: ");
            String note = scanner.nextLine().trim();

            System.out.print("Nhập tên danh mục: ");
            String categoryName = scanner.nextLine().trim();

            Category category = manager.findCategoryByName(categoryName);

            if (category == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy danh mục: " + categoryName
                );
            }

            System.out.print("Nhập tên ví: ");
            String walletName = scanner.nextLine().trim();

            Wallet wallet = manager.findWalletByName(walletName);

            if (wallet == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy ví: " + walletName
                );
            }

            System.out.print("Nhập nguồn thu: ");
            String source = scanner.nextLine().trim();

            Transaction transaction = TransactionFactory.createTransaction(
                    TransactionType.INCOME,
                    id,
                    amount,
                    date,
                    note,
                    category,
                    wallet,
                    source,
                    null,
                    null
            );

            manager.addTransaction(transaction);

            System.out.println("Thêm khoản thu thành công.");
        } catch (Exception e) {
            System.err.println(
                    "\u001B[31mLỗi: " + e.getMessage() + "\u001B[0m"
            );
        }
    }
    private void handleViewTransactions() {
        if (manager.getTransactions().isEmpty()) {
            System.out.println("Chưa có giao dịch nào.");
            return;
        }

        System.out.println("\n===== DANH SÁCH GIAO DỊCH =====");

        for (Transaction transaction : manager.getTransactions()) {
            System.out.println(
                    "Mã: " + transaction.getId()
                            + " | Loại: " + transaction.getType()
                            + " | Số tiền: " + String.format("%,.0f ₫", transaction.getAmount())
                            + " | Ngày: " + DateUtils.formatDate(transaction.getDate())
                            + " | Danh mục: " + transaction.getCategory().getName()
                            + " | Ví: " + transaction.getWallet().getName()
                            + " | Ghi chú: " + transaction.getNote()
            );
        }
    }
    private void handleAddWallet() {
        System.out.print("Nhập mã ví: ");
        String id = scanner.nextLine().trim();

        System.out.print("Nhập tên ví: ");
        String name = scanner.nextLine().trim();

        System.out.print("Nhập số dư ban đầu: ");
        double balance = Double.parseDouble(scanner.nextLine());

        Wallet wallet = WalletFactory.createWallet(
                id,
                name,
                balance,
                WalletType.CASH,
                0
        );

        manager.addWallet(wallet);
        System.out.println("Thêm ví thành công.");
    }
    private void handleViewWallets() {
        if (manager.getWallets().isEmpty()) {
            System.out.println("Chưa có ví nào.");
            return;
        }

        System.out.println("\n===== DANH SÁCH VÍ =====");

        for (Wallet wallet : manager.getWallets()) {
            System.out.println(
                    wallet.getName()
                            + " | Số dư: "
                            + manager.getFormattedBalance(wallet)
            );
        }
    }
    private void handleAddCategory() {
        try {
            System.out.print("Nhập mã danh mục: ");
            String id = scanner.nextLine().trim();

            System.out.print("Nhập tên danh mục: ");
            String name = scanner.nextLine().trim();

            System.out.print("Nhập mô tả: ");
            String description = scanner.nextLine().trim();

            Category category = new Category(id, name, description);
            manager.addCategory(category);

            System.out.println("Thêm danh mục thành công.");
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
    private void handleViewCategories() {
        if (manager.getCategories().isEmpty()) {
            System.out.println("Chưa có danh mục nào.");
            return;
        }

        System.out.println("\n===== DANH SÁCH DANH MỤC =====");

        for (Category category : manager.getCategories()) {
            System.out.println(
                    "Mã: " + category.getId()
                            + " | Tên: " + category.getName()
                            + " | Mô tả: " + category.getDescription()
            );
        }
    }
    private void handleStatistics() {
        if (manager.getTransactions().isEmpty()) {
            System.out.println("Chưa có giao dịch để thống kê.");
            return;
        }

        StatisticsService statisticsService = new StatisticsService();

        double totalIncome = statisticsService.calculateTotalIncome(
                manager.getTransactions()
        );

        double totalExpense = statisticsService.calculateTotalExpense(
                manager.getTransactions()
        );

        double netSaving = statisticsService.calculateNetSaving(
                manager.getTransactions()
        );

        int totalTransactions = statisticsService.countTransactions(
                manager.getTransactions()
        );

        System.out.println("\n===== THỐNG KÊ =====");
        System.out.println("Tổng số giao dịch: " + totalTransactions);
        System.out.println(
                "Tổng thu: " + String.format("%,.0f ₫", totalIncome)
        );
        System.out.println(
                "Tổng chi: " + String.format("%,.0f ₫", totalExpense)
        );
        System.out.println(
                "Chênh lệch thu - chi: " + String.format("%,.0f ₫", netSaving)
        );
    }
    private void handleSettings() { System.out.println("[Chưa nối Service] Cài đặt."); }
}