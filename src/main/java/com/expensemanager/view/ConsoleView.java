package com.expensemanager.view;

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
    private void handleAddExpense() { System.out.println("[Chưa nối Service] Thêm khoản chi."); }
    private void handleAddIncome() { System.out.println("[Chưa nối Service] Thêm khoản thu."); }
    private void handleViewTransactions() { System.out.println("[Chưa nối Service] Xem giao dịch."); }
    private void handleAddWallet() { System.out.println("[Chưa nối Service] Thêm ví."); }
    private void handleViewWallets() { System.out.println("[Chưa nối Service] Xem ví."); }
    private void handleAddCategory() { System.out.println("[Chưa nối Service] Thêm danh mục."); }
    private void handleViewCategories() { System.out.println("[Chưa nối Service] Xem danh mục."); }
    private void handleStatistics() { System.out.println("[Chưa nối Service] Thống kê."); }
    private void handleSettings() { System.out.println("[Chưa nối Service] Cài đặt."); }
}