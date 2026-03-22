package ui;

import model.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

public final class SwingUI extends JFrame {

  private final ExpenseTracker tracker = new ExpenseTracker();

  private ExpenseTrackerView view = tracker.getExpenses();

  private final JLabel statusLabel = new JLabel(" ");

  private final JComboBox<Object> categoryFilterInput;
  private final JTextField startDateInput;
  private final JTextField endDateInput;

  private final ExpenseTableModel model = new ExpenseTableModel();

  private Path currentFilePath = null;

  public static void main(String[] args) {
    EventQueue.invokeLater(SwingUI::new);
  }

  private SwingUI() {
    setTitle("COSC 210 - Lua's Expense Tracker");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setMinimumSize(new Dimension(800, 500));
    setLayout(new BorderLayout());

    var saveButton = new JButton("Save");
    var loadButton = new JButton("Load");
    var insertButton = new JButton("Insert");
    var deleteButton = new JButton("Delete");
    var filterButton = new JButton("Filter");
    var clearButton = new JButton("Clear");

    var filterItems = new ArrayList<>();
    filterItems.add("All Categories");
    filterItems.addAll(KnownCategory.KNOWN_CATEGORIES);
    categoryFilterInput = new JComboBox<>(filterItems.toArray());

    startDateInput = new JTextField(10);
    startDateInput.setToolTipText("YYYY-MM-DD");
    endDateInput = new JTextField(10);
    endDateInput.setToolTipText("YYYY-MM-DD");

    var toolbar = new JToolBar();
    toolbar.setFloatable(false);
    toolbar.add(saveButton);
    toolbar.add(loadButton);
    toolbar.addSeparator();
    toolbar.add(insertButton);
    toolbar.add(deleteButton);
    toolbar.addSeparator();
    toolbar.add(categoryFilterInput);
    toolbar.addSeparator();
    toolbar.add(new JLabel("From:"));
    toolbar.add(Box.createHorizontalStrut(4));
    toolbar.add(startDateInput);
    toolbar.add(Box.createHorizontalStrut(4));
    toolbar.add(new JLabel("To:"));
    toolbar.add(Box.createHorizontalStrut(4));
    toolbar.add(endDateInput);
    toolbar.add(Box.createHorizontalStrut(4));
    toolbar.add(filterButton);
    toolbar.add(clearButton);

    add(toolbar, BorderLayout.NORTH);

    var table = new JTable(model, ExpenseTableModel.createColumnModel());
    table.setFillsViewportHeight(true);
    table.setRowHeight(24);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    var tablePane = new JScrollPane(table);

    add(tablePane, BorderLayout.CENTER);

    statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

    add(statusLabel, BorderLayout.SOUTH);

    saveButton.addActionListener(e -> saveToFile());
    loadButton.addActionListener(e -> loadFromFile());
    insertButton.addActionListener(e -> insertExpense());
    deleteButton.addActionListener(e -> deleteSelectedExpense(table));
    categoryFilterInput.addActionListener(e -> applyCategoryFilter());
    filterButton.addActionListener(e -> applyDateRangeFilter());
    clearButton.addActionListener(e -> clearFilters());

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        int result = JOptionPane.showConfirmDialog(
          SwingUI.this,
          "Save before quitting?",
          "Quit",
          JOptionPane.YES_NO_CANCEL_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
          if (saveToFile()) dispose();
        } else if (result == JOptionPane.NO_OPTION) {
          dispose();
        }
        // if result isn't yes or no, do nothing, the user cancelled
      }
    });

    pack();

    var sillyImage = new JLabel(new ImageIcon("./thumbsup.png"));
    sillyImage.setSize(sillyImage.getPreferredSize());

    getRootPane().getLayeredPane().add(sillyImage, JLayeredPane.PALETTE_LAYER);

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        positionSillyImage(sillyImage);
      }
    });

    SwingUtilities.invokeLater(() -> positionSillyImage(sillyImage));

    try {
      tracker.load(Path.of("./test-expenses.json"));
    } catch (IOException ignored) {}

    resetView();

    setVisible(true);
  }

  private void positionSillyImage(JLabel label) {
    var pane = getRootPane().getLayeredPane();
    label.setLocation(
      pane.getWidth() - label.getWidth() - 8,
      pane.getHeight() - label.getHeight() - 8
    );
  }

  // returns true if save succeeded
  private boolean saveToFile() {
    var chooser = new JFileChooser();
    chooser.setDialogTitle("Save Expenses");
    chooser.setSelectedFile(
      currentFilePath != null
        ? currentFilePath.toFile()
        : new File("expenses.json")
    );

    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return false;

    try {
      currentFilePath = chooser.getSelectedFile().toPath();
      tracker.save(currentFilePath);
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
        this,
        "Failed to save: " + e.getMessage(),
        "Save Error",
        JOptionPane.ERROR_MESSAGE
      );
      return false;
    }
  }

  private void loadFromFile() {
    if (!tracker.getExpenses().isEmpty()) {
      int confirm = JOptionPane.showConfirmDialog(
        this,
        "Loading will replace all current expenses. Continue?",
        "Load Expenses",
        JOptionPane.YES_NO_OPTION
      );
      if (confirm != JOptionPane.YES_OPTION) return;
    }

    var chooser = new JFileChooser();
    chooser.setDialogTitle("Load Expenses");

    if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

    try {
      currentFilePath = chooser.getSelectedFile().toPath();
      tracker.load(currentFilePath);
      clearFilters();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
        this,
        "Failed to load: " + e.getMessage(),
        "Load Error",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  private void insertExpense() {
    var dateField = new JTextField(LocalDate.now().toString(), 12);

    var categoryField = new JComboBox<>(KnownCategory.values());
    categoryField.setEditable(true);

    var amountField = new JTextField("0.00", 10);

    var descriptionField = new JTextField(24);

    var panel = new JPanel(new GridBagLayout());

    var g = new GridBagConstraints();
    g.insets = new Insets(4, 4, 4, 4);
    g.fill = GridBagConstraints.HORIZONTAL;
    g.anchor = GridBagConstraints.WEST;

    g.gridx = 0; g.gridy = 0; g.weightx = 0; panel.add(new JLabel("Date (YYYY-MM-DD):"), g);
    g.gridx = 1; g.weightx = 1; panel.add(dateField, g);

    g.gridx = 0; g.gridy = 1; g.weightx = 0; panel.add(new JLabel("Category:"), g);
    g.gridx = 1; g.weightx = 1; panel.add(categoryField, g);

    g.gridx = 0; g.gridy = 2; g.weightx = 0; panel.add(new JLabel("Amount ($):"), g);
    g.gridx = 1; g.weightx = 1; panel.add(amountField, g);

    g.gridx = 0; g.gridy = 3; g.weightx = 0; panel.add(new JLabel("Description:"), g);
    g.gridx = 1; g.weightx = 1; panel.add(descriptionField, g);

    int result = JOptionPane.showConfirmDialog(
      this,
      panel,
      "Add Expense",
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    LocalDate date = parseInputDate(dateField.getText());
    if (date == null) return;

    Category category = parseInputCategory(categoryField.getSelectedItem());
    if (category == null) return;

    BigDecimal amount = parseInputAmount(amountField.getText());
    if (amount == null) return;

    tracker.addExpense(new Expense(date, category, amount, descriptionField.getText().trim()));
    resetView();
  }

  private void deleteSelectedExpense(JTable table) {
    int selectedRow = table.getSelectedRow();
    if (selectedRow < 0) {
      JOptionPane.showMessageDialog(
        this,
        "Please select an expense to delete.",
        "No Selection",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    Expense expense = model.getExpense(selectedRow);
    if (expense == null) return;

    int confirm = JOptionPane.showConfirmDialog(
      this,
      "Delete this expense?\n  " + expense,
      "Confirm Delete",
      JOptionPane.YES_NO_OPTION
    );
    if (confirm == JOptionPane.YES_OPTION) {
      tracker.deleteExpense(expense);
      resetView();
    }
  }

  private void applyCategoryFilter() {
    Object selected = categoryFilterInput.getSelectedItem();
    if (selected instanceof Category category) {
      try {
        view = tracker.getExpenses().filterByCategory(category);
        refreshModel();
      } catch (FilterException e) {
        JOptionPane.showMessageDialog(
          this,
          "No expenses found for category: " + category.getName(),
          "No Results",
          JOptionPane.INFORMATION_MESSAGE
        );
        categoryFilterInput.setSelectedIndex(0);
        // setSelectedIndex(0) fires this listener again,
        // which hits the else branch
      }
    } else {
      view = tracker.getExpenses();
      refreshModel();
    }
  }

  private void applyDateRangeFilter() {
    LocalDate startDate = parseInputDate(startDateInput.getText());
    if (startDate == null) return;
    LocalDate endDate = parseInputDate(endDateInput.getText());
    if (endDate == null) return;

    if (startDate.isAfter(endDate)) {
      JOptionPane.showMessageDialog(
        this,
        "Start date must be before or equal to end date.",
        "Invalid Date Range",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    try {
      view = tracker.getExpenses().filterByDateRange(startDate, endDate);
    } catch (FilterException e) {
      JOptionPane.showMessageDialog(
        this,
        "No expenses found in that date range.",
        "No Results",
        JOptionPane.INFORMATION_MESSAGE
      );
      return;
    }
    refreshModel();
  }

  private void clearFilters() {
    startDateInput.setText("");
    endDateInput.setText("");
    categoryFilterInput.setSelectedIndex(0);
    // setSelectedIndex fires applyCategoryFilter which calls resetView
  }

  private void refreshModel() {
    model.refreshFromView();
  }

  private void resetView() {
    view = tracker.getExpenses();
    refreshModel();
  }

  private void updateStatus() {
    var expenses = view.toList();
    if (expenses.isEmpty()) {
      statusLabel.setText("No expenses");
      return;
    }

    BigDecimal total = expenses.stream()
      .map(Expense::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    statusLabel.setText(String.format(
      "%d expense%s  |  Total: %s",
      expenses.size(),
      expenses.size() != 1 ? "s" : "",
      NumberFormat.getCurrencyInstance(Locale.CANADA).format(total)
    ));
  }

  private final class ExpenseTableModel extends AbstractTableModel {
    private ExpenseTableModel() {
      refreshFromView();
    }

    private void refreshFromView() {
      fireTableDataChanged();
      updateStatus();
    }

    Expense getExpense(int rowIndex) {
      return view.getExpenseAt(rowIndex);
    }

    @Override public int getRowCount() { return view.size(); }
    @Override public int getColumnCount() { return 4; }

    @Override
    public String getColumnName(int col) {
      return switch (col) {
        case 0 -> "Date";
        case 1 -> "Category";
        case 2 -> "Amount";
        case 3 -> "Description";
        default -> Integer.toString(col);
      };
    }

    @Override
    public Class<?> getColumnClass(int col) {
      return switch (col) {
        case 0 -> LocalDate.class;
        case 1 -> Category.class;
        case 2 -> BigDecimal.class;
        case 3 -> String.class;
        default -> Object.class;
      };
    }

    @Override public boolean isCellEditable(int row, int col) { return true; }

    @Override
    public Object getValueAt(int row, int col) {
      var expense = getExpense(row);
      if (expense == null) return null;
      return switch (col) {
        case 0 -> expense.getDate();
        case 1 -> expense.getCategory();
        case 2 -> expense.getAmount();
        case 3 -> expense.getDescription();
        default -> null;
      };
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
      var expense = getExpense(row);
      if (expense == null) return;

      switch (col) {
        case 0 -> {
          LocalDate date = parseInputDate(value);
          if (date != null) {
            // delete + re-add to keep tracker sorted
            tracker.deleteExpense(expense);
            expense.setDate(date);
            tracker.addExpense(expense);
            SwingUtilities.invokeLater(SwingUI.this::resetView);
          }
        }
        case 1 -> {
          Category category = parseInputCategory(value);
          if (category != null) {
            expense.setCategory(category);
          }
        }
        case 2 -> {
          BigDecimal amount = parseInputAmount(value);
          if (amount != null) {
            expense.setAmount(amount);
          }
        }
        case 3 -> expense.setDescription(String.valueOf(value).trim());
      }

      fireTableCellUpdated(row, col);
      updateStatus();
    }

    private static DefaultTableColumnModel createColumnModel() {
      var dateColumn = new TableColumn(0);
      dateColumn.setHeaderValue("Date");
      dateColumn.setPreferredWidth(80);
      dateColumn.setCellEditor(new DefaultCellEditor(new JTextField()));

      // editable combo box for known categories, plus custom freeform input
      var categoryCombo = new JComboBox<>(KnownCategory.values());
      categoryCombo.setEditable(true);

      var categoryColumn = new TableColumn(1);
      categoryColumn.setHeaderValue("Category");
      categoryColumn.setPreferredWidth(140);
      categoryColumn.setCellEditor(new DefaultCellEditor(categoryCombo));
      categoryColumn.setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        protected void setValue(Object value) {
          super.setValue(value instanceof Category category ? category.toString() : value);
        }
      });

      var amountColumn = new TableColumn(2);
      amountColumn.setHeaderValue("Amount");
      amountColumn.setPreferredWidth(60);
      amountColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
      amountColumn.setCellRenderer(new DefaultTableCellRenderer() {
        { setHorizontalAlignment(SwingConstants.RIGHT); }
        @Override
        protected void setValue(Object value) {
          super.setValue(
            value instanceof BigDecimal amount
              ? NumberFormat.getCurrencyInstance(Locale.CANADA).format(amount)
              : value
          );
        }
      });

      var descriptionColumn = new TableColumn(3);
      descriptionColumn.setHeaderValue("Description");
      descriptionColumn.setPreferredWidth(400);

      var columnModel = new DefaultTableColumnModel();
      columnModel.addColumn(dateColumn);
      columnModel.addColumn(categoryColumn);
      columnModel.addColumn(amountColumn);
      columnModel.addColumn(descriptionColumn);

      return columnModel;
    }
  }

  private LocalDate parseInputDate(Object input) {
    if (input instanceof LocalDate value) return value;
    var string = String.valueOf(input).trim();
    try {
      return LocalDate.parse(string);
    } catch (DateTimeParseException e) {
      displayInvalidDateMessage(string);
      return null;
    }
  }

  private BigDecimal parseInputAmount(Object input) {
    if (input instanceof BigDecimal value) return value;
    var string = String.valueOf(input).trim().replace("$", "");
    try {
      return new BigDecimal(string).setScale(2, RoundingMode.HALF_UP);
    } catch (NumberFormatException e) {
      displayInvalidAmountMessage(string);
      return null;
    }
  }

  private Category parseInputCategory(Object input) {
    if (input instanceof Category value) return value;
    var string = String.valueOf(input).replace("(custom)", "").trim();
    if (string.isEmpty()) {
      displayEmptyCategoryMessage();
    }
    return Category.fromName(string);
  }

  private void displayInvalidDateMessage(String input) {
    JOptionPane.showMessageDialog(
      this,
      "Invalid date format, expected YYYY-MM-DD, got \"" + input + "\"",
      "Invalid Date",
      JOptionPane.ERROR_MESSAGE
    );
  }

  private void displayInvalidAmountMessage(String input) {
    JOptionPane.showMessageDialog(
      this,
      "Invalid amount, expected e.g. 12.34, got \"" + input + "\"",
      "Invalid Amount",
      JOptionPane.ERROR_MESSAGE
    );
  }

  private void displayEmptyCategoryMessage() {
    JOptionPane.showMessageDialog(
      this,
      "Expense cannot have an empty category.",
      "Empty Category",
      JOptionPane.ERROR_MESSAGE
    );
  }

}
