package com.expensemanager.service;

import com.expensemanager.factory.storage.RecurringExecutionStorageFactory;
import com.expensemanager.model.enums.FilePath;
import com.expensemanager.model.enums.RecurringExecutionStatus;
import com.expensemanager.model.transaction.Expense;
import com.expensemanager.model.transaction.RecurringExecution;
import com.expensemanager.model.transaction.RecurringExpense;
import com.expensemanager.model.transaction.Transaction;
import com.expensemanager.model.wallet.Wallet;
import com.expensemanager.exception.ExpenseManagerException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** Quản lý việc thực thi các khoản chi tiêu định kỳ. */
public class RecurringExpenseService {
    private final TransactionService transactionService;
    private final WalletService walletService;
    private final RecurringExecutionStorageFactory storageFactory;
    private final List<RecurringExecution> executions;

    /** Khởi tạo RecurringExpenseService. */
    public RecurringExpenseService(RecurringExecutionStorageFactory storageFactory,
                                   TransactionService transactionService,
                                   WalletService walletService) {
        this.storageFactory = storageFactory;
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.executions = new ArrayList<>();
        load();
    }

    /** Tải dữ liệu. */
    public void load() {
        executions.clear();
        List<RecurringExecution> loaded = storageFactory.load(FilePath.RECURRING);
        executions.addAll(loaded);
    }

    /** Lưu dữ liệu. */
    public void save() {
        storageFactory.save(FilePath.RECURRING, executions);
    }

    /** Xử lý toàn bộ khoản chi định kỳ của người dùng. */
    public void processDueExpenses(int userId) {
        LocalDate today = LocalDate.now();
        List<RecurringExpense> recurringExpenses = getRecurringExpenses(userId);
        for (RecurringExpense recurringExpense : recurringExpenses) {
            if (!recurringExpense.isActive()) {
                continue;
            }
            processRecurringExpense(recurringExpense, userId, today);
        }
        transactionService.save();
        walletService.save();
        save();
    }

    /**
     * Xử lý các kỳ đã đến hạn của một khoản chi định kỳ.
     * Nếu bỏ lỡ nhiều kỳ, các kỳ sẽ được xử lý lần lượt.
     */
    private void processRecurringExpense(RecurringExpense recurringExpense, int userId, LocalDate today) {
        while (recurringExpense.isDue(today)) {
            LocalDate dueDate = recurringExpense.getNextDueDate();
            processOneOccurrence(recurringExpense, userId, dueDate);
            recurringExpense.moveToNextDueDate();
        }
    }

    /** Xử lý một kỳ cụ thể của khoản chi định kỳ. */
    private void processOneOccurrence(RecurringExpense recurringExpense, int userId, LocalDate dueDate) {
        Wallet wallet = recurringExpense.getWallet();
        if (wallet == null) {
            recordFailedExecution(recurringExpense, userId, dueDate);
            return;
        }
        if (wallet.getBalance() < recurringExpense.getAmount()) {
            recordFailedExecution(recurringExpense, userId, dueDate);
            return;
        }
        Expense expense = new Expense(
                UUID.randomUUID().toString(),
                recurringExpense.getAmount(),
                dueDate,
                recurringExpense.getNote(),
                recurringExpense.getCategory(),
                recurringExpense.getWallet(),
                recurringExpense.getPaymentMethod(),
                userId
        );
        try {
            transactionService.addTransaction(expense, userId);
        } catch (ExpenseManagerException e) {
            recordFailedExecution(recurringExpense, userId, dueDate);
            return;
        }
        executions.add(
                new RecurringExecution(
                        UUID.randomUUID().toString(),
                        recurringExpense.getId(),
                        userId,
                        dueDate,
                        recurringExpense.getAmount(),
                        RecurringExecutionStatus.SUCCESS,
                        expense.getId()
                )
        );
    }

    /**
     * Ghi nhận một kỳ thực thi thất bại.
     * Kỳ thất bại không được tạo thành Expense và không được tự động thử lại.
     */
    private void recordFailedExecution(RecurringExpense recurringExpense, int userId, LocalDate dueDate) {
        executions.add(
                new RecurringExecution(
                        UUID.randomUUID().toString(),
                        recurringExpense.getId(),
                        userId,
                        dueDate,
                        recurringExpense.getAmount(),
                        RecurringExecutionStatus.FAILED,
                        null
                )
        );
    }

    /** Lấy lịch sử thực thi của một người dùng. */
    public List<RecurringExecution> getExecutions(int userId) {
        List<RecurringExecution> result = new ArrayList<>();

        for (RecurringExecution execution : executions) {
            if (execution.getUserId() == userId) {
                result.add(execution);
            }
        }
        return List.copyOf(result);
    }

    /** Lấy lịch sử thực thi của một khoản chi định kỳ. */
    public List<RecurringExecution> getExecutions(String recurringExpenseId, int userId) {
        List<RecurringExecution> result = new ArrayList<>();

        for (RecurringExecution execution : executions) {
            if (execution.getUserId() == userId
                    && execution.getRecurringExpenseId().equals(recurringExpenseId)) {
                result.add(execution);
            }
        }
        return List.copyOf(result);
    }

    /** Lấy toàn bộ khoản chi định kỳ của người dùng. */
    public List<RecurringExpense> getRecurringExpenses(int userId) {
        List<RecurringExpense> result = new ArrayList<>();
        for (Transaction transaction : transactionService.getTransactions(userId)) {
            if (transaction instanceof RecurringExpense recurringExpense) {
                result.add(recurringExpense);
            }
        }
        return Collections.unmodifiableList(result);
    }
}
