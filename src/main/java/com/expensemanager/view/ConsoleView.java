package com.expensemanager.view;

import com.expensemanager.service.ExpenseManager;
import com.expensemanager.service.StatisticsService;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.factory.WalletFactory;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.category.Category;
import com.expensemanager.factory.TransactionFactory;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.exception.ExpenseManagerException;
import com.expensemanager.utils.DateUtils;
import com.expensemanager.validation.InputValidationService;
import com.expensemanager.validation.ValidationResult;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Lớp hiển thị giao diện dòng lệnh (Console) cho người dùng thao tác
 * với hệ thống quản lý chi tiêu, tách biệt hoàn toàn khỏi tầng xử lý
 * nghiệp vụ để không phá vỡ kiến trúc phân lớp của dự án.
 *
 * Điểm khác biệt so với bản trước: mỗi trường dữ liệu được kiểm tra
 * NGAY khi vừa nhập xong (thay vì gom hết rồi validate 1 lượt ở cuối).
 * Nếu 1 trường bị sai, chỉ trường đó được hỏi lại, các trường đã nhập
 * đúng trước đó được giữ nguyên, không phải nhập lại từ đầu.
 */
public class ConsoleView {

    private static final String COLOR_RED = "\u001B[31m";
    private static final String COLOR_RESET = "\u001B[0m";

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

    // =====================================================================
    // CÁC HÀM NHẬP LIỆU DÙNG CHUNG (prompt-until-valid)
    // Mỗi hàm lặp lại việc hỏi CHÍNH trường đó cho tới khi người dùng
    // nhập đúng, thay vì để lỗi bay ra ngoài làm mất toàn bộ dữ liệu
    // các trường đã nhập trước đó.
    // =====================================================================

    /** Hiển thị 1 dòng lỗi màu đỏ, thống nhất cách báo lỗi trong toàn lớp. */
    private void printError(String message) {
        System.out.println(COLOR_RED + "Lỗi: " + message + " Vui lòng nhập lại." + COLOR_RESET);
    }

    /** Đọc 1 chuỗi bắt buộc không rỗng, hỏi lại tại chỗ nếu người dùng bỏ trống. */
    private String promptRequiredText(String label, FieldType fieldType) {
        while (true) {
            System.out.print(label + ": ");
            String input = scanner.nextLine();
            ValidationResult<String> result = InputValidationService.validateRequired(input, fieldType);
            if (result.isValid()) {
                return result.getValue();
            }
            printError(result.getMessage());
        }
    }

    /** Đọc 1 chuỗi được phép để trống (vd ghi chú), không cần lặp lại validate. */
    private String promptOptionalText(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    /** Đọc số tiền, hỏi lại tại chỗ nếu sai định dạng hoặc không lớn hơn 0. */
    private double promptAmount(String label) {
        while (true) {
            System.out.print(label + ": ");
            String input = scanner.nextLine();
            ValidationResult<Double> result = InputValidationService.validateAmount(input);
            if (result.isValid()) {
                return result.getValue();
            }
            printError(result.getMessage());
        }
    }

    /** Đọc ngày theo định dạng dd/MM/yyyy, hỏi lại tại chỗ nếu sai định dạng. */
    private LocalDate promptDate(String label) {
        while (true) {
            System.out.print(label + " (dd/MM/yyyy): ");
            String input = scanner.nextLine();
            ValidationResult<LocalDate> result = InputValidationService.validateDate(input);
            if (result.isValid()) {
                return result.getValue();
            }
            printError(result.getMessage());
        }
    }

    /**
     * Đọc tên danh mục và tra cứu danh mục tương ứng đã tồn tại trong hệ thống.
     * Nếu tên rỗng hoặc không tìm thấy danh mục, hỏi lại NGAY tại đây thay vì
     * ném lỗi ra ngoài làm mất các trường đã nhập trước đó.
     */
    private Category promptExistingCategory(String label) {
        while (true) {
            System.out.print(label + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                printError("Tên danh mục không được để trống.");
                continue;
            }
            Category category = manager.findCategoryByName(name);
            if (category == null) {
                printError("Không tìm thấy danh mục \"" + name + "\".");
                continue;
            }
            return category;
        }
    }

    /**
     * Đọc tên ví và tra cứu ví tương ứng đã tồn tại trong hệ thống.
     * Hỏi lại NGAY tại đây nếu tên rỗng hoặc không tìm thấy ví.
     */
    private Wallet promptExistingWallet(String label) {
        while (true) {
            System.out.print(label + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                printError("Tên ví không được để trống.");
                continue;
            }
            Wallet wallet = manager.findWalletByName(name);
            if (wallet == null) {
                printError("Không tìm thấy ví \"" + name + "\".");
                continue;
            }
            return wallet;
        }
    }

    /** Hỏi có/không đơn giản, chỉ chấp nhận y/n, hỏi lại nếu gõ ký tự khác. */
    private boolean promptYesNo(String label) {
        while (true) {
            System.out.print(label + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                return true;
            }
            if (input.equals("n")) {
                return false;
            }
            printError("Chỉ nhập y hoặc n.");
        }
    }

    // =====================================================================
    // CÁC HÀM XỬ LÝ CHỨC NĂNG
    // =====================================================================

    private void handleAddExpense() {
        if (manager.getWallets().isEmpty()) {
            System.out.println("Chưa có ví. Hãy thêm ví trước.");
            return;
        }
        if (manager.getCategories().isEmpty()) {
            System.out.println("Chưa có danh mục. Hãy thêm danh mục trước.");
            return;
        }

        System.out.println("\n-- Thêm khoản chi --");

        // Mỗi trường được kiểm tra ngay khi nhập xong; nếu sai chỉ trường
        // đó được hỏi lại, các trường trước không bị mất.
        String id = promptRequiredText("Nhập mã giao dịch", FieldType.ID);
        double amount = promptAmount("Nhập số tiền chi");
        LocalDate date = promptDate("Nhập ngày");
        String note = promptOptionalText("Nhập ghi chú");
        Category category = promptExistingCategory("Nhập tên danh mục");
        Wallet wallet = promptExistingWallet("Nhập tên ví");
        String paymentMethod = promptRequiredText("Nhập phương thức thanh toán", FieldType.PAYMENTMETHOD);

        // Từ đây trở đi có thể phát sinh lỗi NGHIỆP VỤ (vd không đủ số dư)
        // vốn phụ thuộc vào tổ hợp nhiều trường (ví đã chọn + số tiền),
        // không thể quy về lỗi của riêng 1 trường. Cho phép người dùng
        // chọn lại ví khác thay vì phải nhập lại toàn bộ giao dịch.
        while (true) {
            try {
                Transaction transaction = TransactionFactory.createTransaction(
                        TransactionType.EXPENSE,
                        id, amount, date, note, category, wallet,
                        null, paymentMethod, null
                );
                manager.addTransaction(transaction);
                System.out.println("Thêm khoản chi thành công.");
                return;
            } catch (ExpenseManagerException e) {
                printError(e.getMessage());
                if (!promptYesNo("Bạn có muốn chọn ví khác để thử lại không")) {
                    System.out.println("Đã hủy thêm khoản chi.");
                    return;
                }
                wallet = promptExistingWallet("Nhập tên ví khác");
            }
        }
    }

    private void handleAddIncome() {
        if (manager.getWallets().isEmpty()) {
            System.out.println("Chưa có ví. Hãy thêm ví trước.");
            return;
        }
        if (manager.getCategories().isEmpty()) {
            System.out.println("Chưa có danh mục. Hãy thêm danh mục trước.");
            return;
        }

        System.out.println("\n-- Thêm khoản thu --");

        String id = promptRequiredText("Nhập mã giao dịch", FieldType.ID);
        double amount = promptAmount("Nhập số tiền thu");
        LocalDate date = promptDate("Nhập ngày");
        String note = promptOptionalText("Nhập ghi chú");
        Category category = promptExistingCategory("Nhập tên danh mục");
        Wallet wallet = promptExistingWallet("Nhập tên ví");
        String source = promptRequiredText("Nhập nguồn thu", FieldType.SOURCE);

        try {
            Transaction transaction = TransactionFactory.createTransaction(
                    TransactionType.INCOME,
                    id, amount, date, note, category, wallet,
                    source, null, null
            );
            manager.addTransaction(transaction);
            System.out.println("Thêm khoản thu thành công.");
        } catch (ExpenseManagerException e) {
            // Khoản thu chỉ deposit (nạp tiền), không có ràng buộc "đủ số dư"
            // như khoản chi nên hiếm khi lỗi tới đây, nhưng vẫn xử lý an toàn.
            printError(e.getMessage());
            System.out.println("Đã hủy thêm khoản thu.");
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
        System.out.println("\n-- Thêm ví --");

        String id = promptRequiredText("Nhập mã ví", FieldType.ID);
        String name = promptRequiredText("Nhập tên ví", FieldType.NAME);
        double balance = promptAmountAllowZero("Nhập số dư ban đầu");

        while (true) {
            try {
                Wallet wallet = WalletFactory.createWallet(id, name, balance, WalletType.CASH, 0);
                manager.addWallet(wallet);
                System.out.println("Thêm ví thành công.");
                return;
            } catch (ExpenseManagerException e) {
                // Thường gặp nhất ở đây là trùng tên ví; cho sửa lại đúng
                // trường tên thay vì bắt nhập lại mã ví và số dư.
                printError(e.getMessage());
                name = promptRequiredText("Nhập tên ví khác", FieldType.NAME);
            }
        }
    }

    /**
     * Đọc số dư ban đầu: cho phép bằng 0 (ví mới có thể chưa có tiền),
     * chỉ hỏi lại nếu sai định dạng số hoặc là số âm.
     */
    private double promptAmountAllowZero(String label) {
        while (true) {
            System.out.print(label + ": ");
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input.replace(",", ""));
                if (value < 0) {
                    printError("Số dư không được âm.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printError("Số dư phải là một con số hợp lệ.");
            }
        }
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
        System.out.println("\n-- Thêm danh mục --");

        String id = promptRequiredText("Nhập mã danh mục", FieldType.ID);
        String name = promptRequiredText("Nhập tên danh mục", FieldType.NAME);
        String description = promptOptionalText("Nhập mô tả");

        while (true) {
            try {
                Category category = new Category(id, name, description);
                manager.addCategory(category);
                System.out.println("Thêm danh mục thành công.");
                return;
            } catch (ExpenseManagerException e) {
                // Thường gặp nhất là trùng tên danh mục; chỉ hỏi lại tên.
                printError(e.getMessage());
                name = promptRequiredText("Nhập tên danh mục khác", FieldType.NAME);
            }
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

        double totalIncome = statisticsService.calculateTotalIncome(manager.getTransactions());
        double totalExpense = statisticsService.calculateTotalExpense(manager.getTransactions());
        double netSaving = statisticsService.calculateNetSaving(manager.getTransactions());
        int totalTransactions = statisticsService.countTransactions(manager.getTransactions());

        System.out.println("\n===== THỐNG KÊ =====");
        System.out.println("Tổng số giao dịch: " + totalTransactions);
        System.out.println("Tổng thu: " + String.format("%,.0f ₫", totalIncome));
        System.out.println("Tổng chi: " + String.format("%,.0f ₫", totalExpense));
        System.out.println("Chênh lệch thu - chi: " + String.format("%,.0f ₫", netSaving));
    }

    private void handleSettings() {
        System.out.println("[Chưa nối Service] Cài đặt.");
    }
}
