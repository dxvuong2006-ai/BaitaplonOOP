package com.expensemanager.view;

import com.expensemanager.service.ExpenseManager;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.factory.model.WalletFactory;
import com.expensemanager.model.enums.WalletType;
import com.expensemanager.model.category.Category;
import com.expensemanager.factory.model.TransactionFactory;
import com.expensemanager.model.enums.TransactionType;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.budget.Budget;
import com.expensemanager.model.enums.Period;
import com.expensemanager.model.enums.FieldType;
import com.expensemanager.model.report.ReportData;
import com.expensemanager.utils.DateUtils;
import com.expensemanager.utils.CurrencyUtils;
import com.expensemanager.exception.ExpenseManagerException;
import com.expensemanager.validation.InputValidationService;
import com.expensemanager.validation.ValidationResult;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Lớp hiển thị giao diện dòng lệnh (Console) cho người dùng thao tác
 * với hệ thống quản lý chi tiêu, tách biệt hoàn toàn khỏi tầng xử lý
 * nghiệp vụ để không phá vỡ kiến trúc phân lớp của dự án.
 *
 * NGUYÊN TẮC NHẬP LIỆU: mỗi trường dữ liệu được đọc bằng 1 trong các
 * hàm readXxx() bên dưới. Các hàm này tự lặp lại việc hỏi + kiểm tra
 * (qua InputValidationService/ValidationResult hoặc chính setter của
 * model) ngay tại trường đó. Nếu người dùng nhập sai, chỉ trường đó
 * được hỏi lại — không phải hủy và nhập lại toàn bộ form từ đầu.
 * Người dùng có thể gõ "#" ở bất kỳ trường nào để hủy thao tác đang làm.
 */
public class ConsoleView {

    // Dùng chung 1 luồng đọc input duy nhất cho toàn bộ lớp,
    // tránh mở nhiều Scanner cùng đọc System.in gây xung đột dữ liệu.
    private final Scanner scanner = new Scanner(System.in);
    private final ExpenseManager manager = ExpenseManager.getInstance();

    // Gõ ký tự này ở bất kỳ trường nào để hủy thao tác đang nhập dở.
    private static final String CANCEL_KEYWORD = "#";

    /** Ném ra khi người dùng chủ động hủy giữa lúc đang nhập 1 form nhiều bước. */
    private static class CancelInputException extends RuntimeException {
    }

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
                case "3" -> handleTransactionMenu();
                case "4" -> handleAddWallet();
                case "5" -> handleWalletMenu();
                case "6" -> handleAddCategory();
                case "7" -> handleCategoryMenu();
                case "8" -> handleStatistics();
                case "9" -> handleBudgetMenu();
                case "10" -> handleReportMenu();
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
        System.out.println("[3] Giao dịch (xem/sửa/xóa)");
        System.out.println("[4] Thêm ví");
        System.out.println("[5] Ví (xem/sửa/xóa)");
        System.out.println("[6] Thêm danh mục");
        System.out.println("[7] Danh mục (xem/sửa/xóa)");
        System.out.println("[8] Thống kê thu/chi");
        System.out.println("[9] Ngân sách (xem/thêm/sửa/xóa)");
        System.out.println("[10] Báo cáo theo khoảng thời gian");
        System.out.println("[0] Thoát");
        System.out.print("Chọn chức năng: ");
    }

    // =====================================================================
    // NHÓM HÀM ĐỌC DỮ LIỆU THEO TỪNG TRƯỜNG (lõi của yêu cầu "sai đâu sửa đó")
    // =====================================================================

    /** Đọc 1 dòng thô, không kiểm tra gì; dùng cho các trường được phép để trống. */
    private String readLineAllowEmpty(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        return line == null ? "" : line.trim();
    }

    /** Kiểm tra người dùng có gõ từ khóa hủy hay không, nếu có thì ném CancelInputException. */
    private void checkCancel(String raw) {
        if (CANCEL_KEYWORD.equals(raw.trim())) {
            throw new CancelInputException();
        }
    }

    /**
     * Đọc 1 chuỗi bắt buộc không rỗng, lặp lại cho tới khi hợp lệ.
     * Dùng InputValidationService.validateRequired để đồng bộ thông báo lỗi
     * với các tầng khác trong ứng dụng.
     */
    private String readRequiredString(String prompt, FieldType fieldType) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<String> result = InputValidationService.validateRequired(raw, fieldType);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            return result.getValue();
        }
    }

    /**
     * Đọc chuỗi bắt buộc, giới hạn độ dài tối đa. Dùng cho tên ví/tên danh mục.
     */
    private String readRequiredName(String prompt, FieldType fieldType, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<String> result = InputValidationService.validateName(raw, fieldType, maxLength);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            return result.getValue();
        }
    }

    /**
     * Đọc chuỗi bắt buộc, giới hạn độ dài tối đa. Dùng cho tên ví.
     */
    private String readRequiredWalletName(String prompt, FieldType fieldType, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<String> result = InputValidationService.validateName(raw, fieldType, maxLength);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            String name = result.getValue();
            if (manager.findWalletByName(name) != null) {
                printFieldError("Tên ví \"" + name + "\" đã tồn tại.");
                continue;
            }
            return name;
        }
    }

    /**
     * Đọc chuỗi bắt buộc, giới hạn độ dài tối đa. Dùng cho tên danh mục.
     */
    private String readRequiredCategoryName(String prompt, FieldType fieldType, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<String> result = InputValidationService.validateName(raw, fieldType, maxLength);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            String name = result.getValue();
            if (manager.findCategoryByName(name) != null) {
                printFieldError("Tên danh mục \"" + name + "\" đã tồn tại.");
                continue;
            }
            return name;
        }
    }

    /**
     * Đọc số tiền hợp lệ (>0, đúng định dạng tiền tệ) từ bàn phím.
     * Lặp lại chỉ tại trường này nếu người dùng gõ sai, không ảnh hưởng
     * các trường đã nhập trước đó.
     */
    private double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<Double> result = InputValidationService.validateAmount(raw);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            return result.getValue();
        }
    }

    /**
     * Đọc mã ví mới, lặp lại tại chỗ nếu mã rỗng hoặc đã tồn tại trong
     * danh sách ví hiện có. Giúp báo lỗi trùng ID ngay khi vừa nhập,
     * thay vì đợi đến khi submit toàn bộ form rồi mới bị Service từ chối.
     */
    private String readNewWalletId(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<String> result = InputValidationService.validateRequired(raw, FieldType.ID);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            String id = result.getValue();
            if (manager.findWalletById(id) != null) {
                printFieldError("Mã ví \"" + id + "\" đã tồn tại.");
                continue;
            }
            return id;
        }
    }

    /**
     * Đọc mã danh mục mới, lặp lại tại chỗ nếu mã rỗng hoặc đã tồn tại
     * trong danh sách danh mục hiện có. Giúp báo lỗi trùng ID ngay khi
     * vừa nhập, thay vì đợi đến khi submit toàn bộ form.
     */
    private String readNewCategoryId(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<String> result = InputValidationService.validateRequired(raw, FieldType.ID);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            String id = result.getValue();
            if (manager.findCategoryById(id) != null) {
                printFieldError("Mã danh mục \"" + id + "\" đã tồn tại.");
                continue;
            }
            return id;
        }
    }

    /**
     * Đọc mã giao dịch mới, lặp lại tại chỗ nếu mã rỗng hoặc đã tồn tại
     * trong danh sách danh mục hiện có. Giúp báo lỗi trùng ID ngay khi
     * vừa nhập, thay vì đợi đến khi submit toàn bộ form.
     */
    private String readNewTransactionId(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<String> result = InputValidationService.validateRequired(raw, FieldType.ID);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            String id = result.getValue();
            if (manager.findTransactionById(id) != null) {
                printFieldError("Mã giao dịch \"" + id + "\" đã tồn tại.");
                continue;
            }
            return id;
        }
    }

    /**
     * Đọc ngày hợp lệ theo định dạng dd/MM/yyyy. Nếu để trống, trả về
     * ngày hiện tại (tiện cho các thao tác nhập nhanh); nếu gõ gì đó mà
     * sai định dạng thì hỏi lại đúng trường ngày.
     */
    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            if (raw.trim().isEmpty()) {
                return LocalDate.now();
            }
            ValidationResult<LocalDate> result = InputValidationService.validateDate(raw);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            return result.getValue();
        }
    }

    /**
     * Đọc số nguyên dương (dùng cho ID kiểu int, ví dụ Budget), lặp lại
     * tại chỗ nếu gõ không phải số hoặc số âm.
     */
    private int readNonNegativeInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            try {
                int value = Integer.parseInt(raw.trim());
                if (value < 0) {
                    printFieldError("Giá trị không được âm, vui lòng nhập lại.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printFieldError("\"" + raw + "\" không phải là số nguyên hợp lệ.");
            }
        }
    }

    /**
     * Đọc lựa chọn Có/Không (y/n), lặp lại nếu gõ ký tự khác.
     */
    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String raw = scanner.nextLine().trim().toLowerCase();
            checkCancel(raw);
            if (raw.equals("y") || raw.equals("yes")) {
                return true;
            }
            if (raw.equals("n") || raw.equals("no")) {
                return false;
            }
            printFieldError("Vui lòng nhập \"y\" hoặc \"n\".");
        }
    }

    /**
     * Đọc tên danh mục và tra ra Category đã tồn tại; nếu không tìm thấy
     * thì báo lỗi và hỏi lại ngay tại trường danh mục (không văng ra
     * ngoài form), kèm gợi ý danh sách danh mục hiện có.
     */
    private Category readExistingCategory(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            if (raw.trim().isEmpty()) {
                printFieldError("Tên danh mục không được để trống.");
                continue;
            }
            Category category = manager.findCategoryByName(raw.trim());
            if (category == null) {
                printFieldError("Không tìm thấy danh mục \"" + raw.trim() + "\".");
                printAvailableCategoryNames();
                continue;
            }
            return category;
        }
    }

    /**
     * Đọc tên ví và tra ra Wallet đã tồn tại; nếu không tìm thấy thì báo
     * lỗi và hỏi lại ngay tại trường ví, kèm gợi ý danh sách ví hiện có.
     */
    private Wallet readExistingWallet(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            if (raw.trim().isEmpty()) {
                printFieldError("Tên ví không được để trống.");
                continue;
            }
            Wallet wallet = manager.findWalletByName(raw.trim());
            if (wallet == null) {
                printFieldError("Không tìm thấy ví \"" + raw.trim() + "\".");
                printAvailableWalletNames();
                continue;
            }
            return wallet;
        }
    }

    /** Đọc loại ví (CASH/BANK/EWALLET) bằng số thứ tự, lặp lại nếu chọn sai. */
    private WalletType readWalletType() {
        while (true) {
            System.out.println("Loại ví: [1] Tiền mặt  [2] Ngân hàng  [3] Ví điện tử");
            System.out.print("Chọn loại ví: ");
            String raw = scanner.nextLine();
            checkCancel(raw);
            switch (raw.trim()) {
                case "1":
                    return WalletType.CASH;
                case "2":
                    return WalletType.BANK;
                case "3":
                    return WalletType.EWALLET;
                default:
                    printFieldError("Vui lòng chọn 1, 2 hoặc 3.");
            }
        }
    }

    /** Đọc chu kỳ (DAILY/WEEKLY/MONTH/YEARLY) bằng số thứ tự, lặp lại nếu chọn sai. */
    private Period readPeriod() {
        while (true) {
            System.out.println("Chu kỳ: [1] Hàng ngày  [2] Hàng tuần  [3] Hàng tháng  [4] Hàng năm");
            System.out.print("Chọn chu kỳ: ");
            String raw = scanner.nextLine();
            checkCancel(raw);
            switch (raw.trim()) {
                case "1":
                    return Period.DAILY;
                case "2":
                    return Period.WEEKLY;
                case "3":
                    return Period.MONTH;
                case "4":
                    return Period.YEARLY;
                default:
                    printFieldError("Vui lòng chọn 1, 2, 3 hoặc 4.");
            }
        }
    }

    /** In lỗi của 1 trường theo màu đỏ rồi nhắc nhập lại đúng trường đó. */
    private void printFieldError(String message) {
        System.out.println("\u001B[31m  → Lỗi: " + (message == null ? "Giá trị không hợp lệ" : message)
                + " Vui lòng nhập lại.\u001B[0m");
    }

    /** In lỗi nghiệp vụ tổng quát (không gắn với 1 trường cụ thể) trước khi hủy thao tác. */
    private void printOperationError(String message) {
        System.err.println("\u001B[31mLỗi: " + message + "\u001B[0m");
    }

    private void printAvailableCategoryNames() {
        if (manager.getCategories().isEmpty()) {
            System.out.println("  (Hiện chưa có danh mục nào, hãy thêm danh mục trước.)");
            return;
        }
        StringBuilder sb = new StringBuilder("  Danh mục hiện có: ");
        List<Category> categories = manager.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(categories.get(i).getName());
        }
        System.out.println(sb);
    }

    private void printAvailableWalletNames() {
        if (manager.getWallets().isEmpty()) {
            System.out.println("  (Hiện chưa có ví nào, hãy thêm ví trước.)");
            return;
        }
        StringBuilder sb = new StringBuilder("  Ví hiện có: ");
        List<Wallet> wallets = manager.getWallets();
        for (int i = 0; i < wallets.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(wallets.get(i).getName());
        }
        System.out.println(sb);
    }

    // =====================================================================
    // 1-2. THÊM GIAO DỊCH (Expense / Income)
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

        System.out.println("\n-- Thêm khoản chi -- (gõ \"#\" ở bất kỳ đâu để hủy)");
        try {
            String id = readNewTransactionId("Nhập mã giao dịch: ");
            double amount = readAmount("Nhập số tiền chi: ");
            LocalDate date = readDate("Nhập ngày (dd/MM/yyyy, để trống = hôm nay): ");
            String note = readLineAllowEmpty("Nhập ghi chú (có thể để trống): ");
            Category category = readExistingCategory("Nhập tên danh mục: ");
            Wallet wallet = readExistingWallet("Nhập tên ví: ");
            String paymentMethod = readLineAllowEmpty("Nhập phương thức thanh toán (có thể để trống): ");

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
        } catch (CancelInputException e) {
            System.out.println("Đã hủy thêm khoản chi.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
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

        System.out.println("\n-- Thêm khoản thu -- (gõ \"#\" ở bất kỳ đâu để hủy)");
        try {
            String id = readNewTransactionId("Nhập mã giao dịch: ");
            double amount = readAmount("Nhập số tiền thu: ");
            LocalDate date = readDate("Nhập ngày (dd/MM/yyyy, để trống = hôm nay): ");
            String note = readLineAllowEmpty("Nhập ghi chú (có thể để trống): ");
            Category category = readExistingCategory("Nhập tên danh mục: ");
            Wallet wallet = readExistingWallet("Nhập tên ví: ");
            String source = readLineAllowEmpty("Nhập nguồn thu (có thể để trống): ");

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
        } catch (CancelInputException e) {
            System.out.println("Đã hủy thêm khoản thu.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    // =====================================================================
    // 3. GIAO DỊCH: xem / sửa / xóa
    // =====================================================================

    private void handleTransactionMenu() {
        if (manager.getTransactions().isEmpty()) {
            System.out.println("Chưa có giao dịch nào.");
            return;
        }
        System.out.println("\n[1] Xem danh sách giao dịch");
        System.out.println("[2] Sửa giao dịch");
        System.out.println("[3] Xóa giao dịch");
        System.out.print("Chọn: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleViewTransactions();
            case "2" -> handleUpdateTransaction();
            case "3" -> handleRemoveTransaction();
            default -> System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    private void handleViewTransactions() {
        System.out.println("\n===== DANH SÁCH GIAO DỊCH =====");
        for (Transaction transaction : manager.getTransactions()) {
            printTransactionLine(transaction);
        }
    }

    private void printTransactionLine(Transaction transaction) {
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

    /**
     * Tìm giao dịch theo mã, lặp lại tại trường mã nếu không tìm thấy,
     * cho tới khi tìm được hoặc người dùng hủy bằng "#".
     */
    private Transaction readExistingTransactionId(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            String id = raw.trim();
            Transaction found = null;
            for (Transaction t : manager.getTransactions()) {
                if (t.getId().equals(id)) {
                    found = t;
                    break;
                }
            }
            if (found == null) {
                printFieldError("Không tìm thấy giao dịch có mã \"" + id + "\".");
                continue;
            }
            return found;
        }
    }

    private void handleRemoveTransaction() {
        System.out.println("\n-- Xóa giao dịch -- (gõ \"#\" để hủy)");
        try {
            Transaction transaction = readExistingTransactionId("Nhập mã giao dịch cần xóa: ");
            printTransactionLine(transaction);
            if (!readYesNo("Xác nhận xóa giao dịch trên?")) {
                System.out.println("Đã hủy xóa.");
                return;
            }
            manager.removeTransaction(transaction);
            System.out.println("Xóa giao dịch thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy xóa giao dịch.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    /**
     * Sửa giao dịch: tạo giao dịch mới từ dữ liệu người dùng nhập lại,
     * cùng loại (INCOME/EXPENSE) với giao dịch cũ, rồi gọi
     * manager.updateTransaction để service tự hoàn tác nếu thất bại.
     */
    private void handleUpdateTransaction() {
        System.out.println("\n-- Sửa giao dịch -- (gõ \"#\" để hủy)");
        try {
            Transaction oldTransaction = readExistingTransactionId("Nhập mã giao dịch cần sửa: ");
            System.out.println("Thông tin hiện tại:");
            printTransactionLine(oldTransaction);
            System.out.println("Nhập thông tin mới (Enter để giữ nguyên số tiền/ngày/ghi chú không áp dụng, " +
                    "vui lòng nhập lại đầy đủ):");

            double amount = readAmount("Số tiền mới: ");
            LocalDate date = readDate("Ngày mới (dd/MM/yyyy, để trống = hôm nay): ");
            String note = readLineAllowEmpty("Ghi chú mới (có thể để trống): ");
            Category category = readExistingCategory("Danh mục mới: ");
            Wallet wallet = readExistingWallet("Ví mới: ");

            Transaction newTransaction;
            if (oldTransaction.getType() == TransactionType.EXPENSE) {
                String paymentMethod = readLineAllowEmpty("Phương thức thanh toán mới (có thể để trống): ");
                newTransaction = TransactionFactory.createTransaction(
                        TransactionType.EXPENSE, oldTransaction.getId(), amount, date, note,
                        category, wallet, null, paymentMethod, null);
            } else {
                String source = readLineAllowEmpty("Nguồn thu mới (có thể để trống): ");
                newTransaction = TransactionFactory.createTransaction(
                        TransactionType.INCOME, oldTransaction.getId(), amount, date, note,
                        category, wallet, source, null, null);
            }

            manager.updateTransaction(oldTransaction, newTransaction);
            System.out.println("Sửa giao dịch thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy sửa giao dịch.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    // =====================================================================
    // 4. THÊM VÍ
    // =====================================================================

    private void handleAddWallet() {
        System.out.println("\n-- Thêm ví -- (gõ \"#\" để hủy)");
        try {
            String id = readNewWalletId("Nhập mã ví: ");
            String name = readRequiredWalletName("Nhập tên ví: ", FieldType.NAME, 100);
            double balance = readAmount("Nhập số dư ban đầu: ");
            WalletType type = readWalletType();

            double extraFee = 0;
            if (type == WalletType.BANK) {
                extraFee = readNonNegativeAmount("Nhập phí giao dịch cố định (VNĐ, để trống = 0): ");
            } else if (type == WalletType.EWALLET) {
                extraFee = readNonNegativeAmount("Nhập tỉ lệ phí giao dịch (%, để trống = 0): ");
            }

            Wallet wallet = WalletFactory.createWallet(id, name, balance, type, extraFee);
            manager.addWallet(wallet);
            System.out.println("Thêm ví thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy thêm ví.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    /** Đọc số tiền/tỉ lệ >= 0 (được phép bằng 0), để trống nghĩa là 0. */
    private double readNonNegativeAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            if (raw.trim().isEmpty()) {
                return 0;
            }
            try {
                double value = CurrencyUtils.parseAmount(raw);
                if (value < 0) {
                    printFieldError("Giá trị không được âm.");
                    continue;
                }
                return value;
            } catch (Exception e) {
                printFieldError("\"" + raw + "\" không phải là số hợp lệ.");
            }
        }
    }

    // =====================================================================
    // 5. VÍ: xem / sửa / xóa
    // =====================================================================

    private void handleWalletMenu() {
        if (manager.getWallets().isEmpty()) {
            System.out.println("Chưa có ví nào.");
            return;
        }
        System.out.println("\n[1] Xem danh sách ví");
        System.out.println("[2] Sửa tên ví");
        System.out.println("[3] Xóa ví");
        System.out.print("Chọn: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleViewWallets();
            case "2" -> handleUpdateWallet();
            case "3" -> handleRemoveWallet();
            default -> System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    private void handleViewWallets() {
        System.out.println("\n===== DANH SÁCH VÍ =====");
        for (Wallet wallet : manager.getWallets()) {
            System.out.println(
                    "Mã: " + wallet.getId()
                            + " | Tên: " + wallet.getName()
                            + " | Loại: " + wallet.getType()
                            + " | Số dư: " + manager.getFormattedBalance(wallet)
            );
        }
    }

    private void handleUpdateWallet() {
        System.out.println("\n-- Sửa tên ví -- (gõ \"#\" để hủy)");
        try {
            Wallet oldWallet = readExistingWallet("Nhập tên ví cần sửa: ");
            String newName = readRequiredName("Nhập tên ví mới: ", FieldType.NAME, 100);
            // updateWallet chỉ hỗ trợ đổi tên; tạo 1 ví tạm cùng loại/balance
            // chỉ để mang tên mới sang, không dùng để lưu trữ thật.
            Wallet placeholder = WalletFactory.createWallet(
                    oldWallet.getId(), newName, 0, oldWallet.getType(), 0);
            manager.updateWallet(oldWallet, placeholder);
            System.out.println("Sửa tên ví thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy sửa ví.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    private void handleRemoveWallet() {
        System.out.println("\n-- Xóa ví -- (gõ \"#\" để hủy)");
        try {
            Wallet wallet = readExistingWallet("Nhập tên ví cần xóa: ");
            if (!readYesNo("Xác nhận xóa ví \"" + wallet.getName() + "\"?")) {
                System.out.println("Đã hủy xóa.");
                return;
            }
            manager.removeWallet(wallet);
            System.out.println("Xóa ví thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy xóa ví.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    // =====================================================================
    // 6. THÊM DANH MỤC
    // =====================================================================

    private void handleAddCategory() {
        System.out.println("\n-- Thêm danh mục -- (gõ \"#\" để hủy)");
        try {
            String id = readNewCategoryId("Nhập mã danh mục: ");
            String name = readRequiredCategoryName("Nhập tên danh mục: ", FieldType.NAME, 100);
            String description = readLineAllowEmpty("Nhập mô tả (có thể để trống): ");

            Category category = new Category(id, name, description);
            manager.addCategory(category);
            System.out.println("Thêm danh mục thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy thêm danh mục.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    // =====================================================================
    // 7. DANH MỤC: xem / sửa / xóa
    // =====================================================================

    private void handleCategoryMenu() {
        if (manager.getCategories().isEmpty()) {
            System.out.println("Chưa có danh mục nào.");
            return;
        }
        System.out.println("\n[1] Xem danh mục");
        System.out.println("[2] Sửa danh mục");
        System.out.println("[3] Xóa danh mục");
        System.out.print("Chọn: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleViewCategories();
            case "2" -> handleUpdateCategory();
            case "3" -> handleRemoveCategory();
            default -> System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    private void handleViewCategories() {
        System.out.println("\n===== DANH SÁCH DANH MỤC =====");
        for (Category category : manager.getCategories()) {
            System.out.println(
                    "Mã: " + category.getId()
                            + " | Tên: " + category.getName()
                            + " | Mô tả: " + category.getDescription()
            );
        }
    }

    private void handleUpdateCategory() {
        System.out.println("\n-- Sửa danh mục -- (gõ \"#\" để hủy)");
        try {
            Category oldCategory = readExistingCategory("Nhập tên danh mục cần sửa: ");
            String newName = readRequiredName("Nhập tên mới: ", FieldType.NAME, 100);
            String newDescription = readLineAllowEmpty("Nhập mô tả mới (có thể để trống): ");

            Category placeholder = new Category(oldCategory.getId(), newName, newDescription);
            manager.updateCategory(oldCategory, placeholder);
            System.out.println("Sửa danh mục thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy sửa danh mục.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    private void handleRemoveCategory() {
        System.out.println("\n-- Xóa danh mục -- (gõ \"#\" để hủy)");
        try {
            Category category = readExistingCategory("Nhập tên danh mục cần xóa: ");
            if (!readYesNo("Xác nhận xóa danh mục \"" + category.getName() + "\"?")) {
                System.out.println("Đã hủy xóa.");
                return;
            }
            manager.removeCategory(category);
            System.out.println("Xóa danh mục thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy xóa danh mục.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    // =====================================================================
    // 8. THỐNG KÊ
    // =====================================================================

    private void handleStatistics() {
        if (manager.getTransactions().isEmpty()) {
            System.out.println("Chưa có giao dịch để thống kê.");
            return;
        }

        List<Transaction> transactions = manager.getTransactions();
        double totalIncome = manager.calculateTotalIncome(transactions);
        double totalExpense = manager.calculateTotalExpense(transactions);
        double netSaving = manager.calculateNetSaving(transactions);
        int totalTransactions = manager.countTransactions(transactions);

        System.out.println("\n===== THỐNG KÊ =====");
        System.out.println("Tổng số giao dịch: " + totalTransactions);
        System.out.println("Tổng thu: " + String.format("%,.0f ₫", totalIncome));
        System.out.println("Tổng chi: " + String.format("%,.0f ₫", totalExpense));
        System.out.println("Chênh lệch thu - chi: " + String.format("%,.0f ₫", netSaving));

        System.out.println("\n-- Chi theo danh mục --");
        Map<Category, Double> expenseByCategory = manager.calculateExpenseByCategory(transactions);
        for (Map.Entry<Category, Double> entry : expenseByCategory.entrySet()) {
            String name = entry.getKey() == null ? "(Không có danh mục)" : entry.getKey().getName();
            System.out.println("  " + name + ": " + String.format("%,.0f ₫", entry.getValue()));
        }

        System.out.println("\n-- Thu theo danh mục --");
        Map<Category, Double> incomeByCategory = manager.calculateIncomeByCategory(transactions);
        for (Map.Entry<Category, Double> entry : incomeByCategory.entrySet()) {
            String name = entry.getKey() == null ? "(Không có danh mục)" : entry.getKey().getName();
            System.out.println("  " + name + ": " + String.format("%,.0f ₫", entry.getValue()));
        }

        System.out.println("\n-- Chi theo tháng --");
        Map<YearMonth, Double> expenseByMonth = manager.calculateExpenseByMonth(transactions);
        for (Map.Entry<YearMonth, Double> entry : expenseByMonth.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + String.format("%,.0f ₫", entry.getValue()));
        }
    }

    // =====================================================================
    // 9. NGÂN SÁCH: xem / thêm / sửa / xóa
    // =====================================================================

    private void handleBudgetMenu() {
        System.out.println("\n[1] Xem danh sách ngân sách");
        System.out.println("[2] Thêm ngân sách");
        System.out.println("[3] Sửa ngân sách");
        System.out.println("[4] Xóa ngân sách");
        System.out.print("Chọn: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleViewBudgets();
            case "2" -> handleAddBudget();
            case "3" -> handleUpdateBudget();
            case "4" -> handleRemoveBudget();
            default -> System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    private void handleViewBudgets() {
        if (manager.getBudgets().isEmpty()) {
            System.out.println("Chưa có ngân sách nào.");
            return;
        }
        System.out.println("\n===== DANH SÁCH NGÂN SÁCH =====");
        for (Budget budget : manager.getBudgets()) {
            printBudgetLine(budget);
        }
    }

    /**
     * In thông tin 1 ngân sách kèm % đã dùng và số tiền còn lại.
     * Bọc try-catch riêng vì các phép tính này yêu cầu ngân sách phải có
     * danh mục hợp lệ (validateBudget bên trong Service), 1 ngân sách lỗi
     * không được làm dừng việc hiển thị các ngân sách khác.
     */
    private void printBudgetLine(Budget budget) {
        try {
            double usagePercent = manager.getBudgetService().getUsagePercentage(budget);
            double remaining = manager.getBudgetService().getRemainingBudget(budget);
            System.out.println(
                    "Mã: " + budget.getId()
                            + " | Danh mục: " + (budget.getCategory() != null
                            ? budget.getCategory().getName() : "Tất cả")
                            + " | Hạn mức: " + CurrencyUtils.formatVND(budget.getLimitAmount())
                            + " | Chu kỳ: " + budget.getPeriod()
                            + " | Đã dùng: " + String.format("%.1f%%", usagePercent)
                            + " | Còn lại: " + CurrencyUtils.formatVND(remaining)
            );
        } catch (ExpenseManagerException e) {
            System.out.println(
                    "Mã: " + budget.getId() + " | (Không tính được mức sử dụng: " + e.getMessage() + ")"
            );
        }
    }

    private void handleAddBudget() {
        if (manager.getCategories().isEmpty()) {
            System.out.println("Chưa có danh mục. Hãy thêm danh mục trước.");
            return;
        }
        System.out.println("\n-- Thêm ngân sách -- (gõ \"#\" để hủy)");
        try {
            int id = readNonNegativeInt("Nhập mã ngân sách (số nguyên): ");
            Category category = readExistingCategory("Nhập tên danh mục áp dụng: ");

            if (manager.getBudgetService().findBudgetByCategory(category) != null) {
                printOperationError("Danh mục \"" + category.getName() + "\" đã có ngân sách, hãy dùng chức năng sửa.");
                return;
            }

            double limitAmount = readAmount("Nhập hạn mức chi tiêu: ");
            Period period = readPeriod();

            String budgetId = Integer.toString(id);
            Budget budget = new Budget(budgetId, category, limitAmount, period);
            manager.addBudget(budget);
            System.out.println("Thêm ngân sách thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy thêm ngân sách.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    /**
     * Tìm ngân sách theo danh mục nhập từ bàn phím, lặp lại tại chỗ nếu
     * danh mục đó chưa có ngân sách nào.
     */
    private Budget readExistingBudgetByCategory(String prompt) {
        while (true) {
            Category category = readExistingCategory(prompt);
            Budget budget = manager.getBudgetService().findBudgetByCategory(category);
            if (budget == null) {
                printFieldError("Danh mục \"" + category.getName() + "\" chưa có ngân sách nào.");
                continue;
            }
            return budget;
        }
    }

    private void handleUpdateBudget() {
        if (manager.getBudgets().isEmpty()) {
            System.out.println("Chưa có ngân sách nào để sửa.");
            return;
        }
        System.out.println("\n-- Sửa ngân sách -- (gõ \"#\" để hủy)");
        try {
            Budget oldBudget = readExistingBudgetByCategory("Nhập tên danh mục của ngân sách cần sửa: ");
            System.out.println("Thông tin hiện tại:");
            printBudgetLine(oldBudget);

            Category newCategory = readExistingCategory("Nhập danh mục mới (có thể giữ nguyên tên cũ): ");
            double newLimit = readAmount("Nhập hạn mức mới: ");
            Period newPeriod = readPeriod();

            Budget placeholder = new Budget(oldBudget.getId(), newCategory, newLimit, newPeriod);
            manager.updateBudget(oldBudget, placeholder);
            System.out.println("Sửa ngân sách thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy sửa ngân sách.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    private void handleRemoveBudget() {
        if (manager.getBudgets().isEmpty()) {
            System.out.println("Chưa có ngân sách nào để xóa.");
            return;
        }
        System.out.println("\n-- Xóa ngân sách -- (gõ \"#\" để hủy)");
        try {
            Budget budget = readExistingBudgetByCategory("Nhập tên danh mục của ngân sách cần xóa: ");
            String categoryName = budget.getCategory() != null ? budget.getCategory().getName() : "Tất cả";
            if (!readYesNo("Xác nhận xóa ngân sách của danh mục \"" + categoryName + "\"?")) {
                System.out.println("Đã hủy xóa.");
                return;
            }
            manager.removeBudget(budget);
            System.out.println("Xóa ngân sách thành công.");
        } catch (CancelInputException e) {
            System.out.println("Đã hủy xóa ngân sách.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    // =====================================================================
    // 10. BÁO CÁO
    // =====================================================================

    private void handleReportMenu() {
        if (manager.getTransactions().isEmpty()) {
            System.out.println("Chưa có giao dịch nào để lập báo cáo.");
            return;
        }
        System.out.println("\n-- Tạo báo cáo -- (gõ \"#\" để hủy)");
        try {
            LocalDate startDate = readMandatoryDate("Nhập ngày bắt đầu (dd/MM/yyyy): ");
            LocalDate endDate = readEndDateNotBeforeStart("Nhập ngày kết thúc (dd/MM/yyyy): ", startDate);

            ReportData report = manager.createReport(startDate, endDate);
            printReport(report);
        } catch (CancelInputException e) {
            System.out.println("Đã hủy tạo báo cáo.");
        } catch (ExpenseManagerException | IllegalArgumentException e) {
            printOperationError(e.getMessage());
        }
    }

    /**
     * Đọc ngày bắt buộc (không cho để trống, vì báo cáo cần khoảng thời
     * gian rõ ràng), lặp lại tại trường này nếu sai định dạng.
     */
    private LocalDate readMandatoryDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine();
            checkCancel(raw);
            ValidationResult<LocalDate> result = InputValidationService.validateDate(raw);
            if (result.hasError()) {
                printFieldError(result.getMessage());
                continue;
            }
            return result.getValue();
        }
    }

    /**
     * Đọc ngày kết thúc, đảm bảo không nhỏ hơn ngày bắt đầu; nếu sai thì
     * chỉ hỏi lại đúng trường ngày kết thúc, giữ nguyên ngày bắt đầu đã nhập.
     */
    private LocalDate readEndDateNotBeforeStart(String prompt, LocalDate startDate) {
        while (true) {
            LocalDate endDate = readMandatoryDate(prompt);
            if (endDate.isBefore(startDate)) {
                printFieldError("Ngày kết thúc không được trước ngày bắt đầu ("
                        + DateUtils.formatDate(startDate) + ").");
                continue;
            }
            return endDate;
        }
    }

    private void printReport(ReportData report) {
        System.out.println("\n===== BÁO CÁO TỪ " + DateUtils.formatDate(report.getStartDate())
                + " ĐẾN " + DateUtils.formatDate(report.getEndDate()) + " =====");
        System.out.println("Tổng số giao dịch: " + report.getTotalTransactions());
        System.out.println("Tổng thu: " + CurrencyUtils.formatVND(report.getTotalIncome()));
        System.out.println("Tổng chi: " + CurrencyUtils.formatVND(report.getTotalExpense()));
        System.out.println("Chênh lệch: " + CurrencyUtils.formatVND(report.getNetSaving()));

        System.out.println("\n-- Chi theo danh mục --");
        for (Map.Entry<Category, Double> entry : report.getExpenseByCategory().entrySet()) {
            String name = entry.getKey() == null ? "(Không có danh mục)" : entry.getKey().getName();
            System.out.println("  " + name + ": " + CurrencyUtils.formatVND(entry.getValue()));
        }

        System.out.println("\n-- Thu theo danh mục --");
        for (Map.Entry<Category, Double> entry : report.getIncomeByCategory().entrySet()) {
            String name = entry.getKey() == null ? "(Không có danh mục)" : entry.getKey().getName();
            System.out.println("  " + name + ": " + CurrencyUtils.formatVND(entry.getValue()));
        }

        System.out.println("\n-- Chi theo ví --");
        for (Map.Entry<Wallet, Double> entry : report.getExpenseByWallet().entrySet()) {
            String name = entry.getKey() == null ? "(Không có ví)" : entry.getKey().getName();
            System.out.println("  " + name + ": " + CurrencyUtils.formatVND(entry.getValue()));
        }

        System.out.println("\n-- Thu theo ví --");
        for (Map.Entry<Wallet, Double> entry : report.getIncomeByWallet().entrySet()) {
            String name = entry.getKey() == null ? "(Không có ví)" : entry.getKey().getName();
            System.out.println("  " + name + ": " + CurrencyUtils.formatVND(entry.getValue()));
        }
    }
}
