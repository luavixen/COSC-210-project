package ui;

import model.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

public final class SwingUI extends JFrame {

  private final ExpenseTracker tracker = new ExpenseTracker();

  private ExpenseTrackerView view = tracker.getExpenses();
  private ExpenseTableModel model = new ExpenseTableModel();

  public static void main(String[] args) {
    EventQueue.invokeLater(SwingUI::new);
  }

  private SwingUI() {
    setTitle("COSC 210 - Lua's Expense Tracker :3c");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    setMinimumSize(new Dimension(640, 480));
    setLayout(new BorderLayout());

    var saveButton = new JButton("Save");
    var loadButton = new JButton("Load");

    var insertButton = new JButton("Insert");
    var deleteButton = new JButton("Delete");

    var categoryInput = new JComboBox<Category>(KnownCategory.ALL_KNOWN_CATEGORIES.toArray(KnownCategory[]::new));

    var startDateInput = new JTextField("YYYY-MM-DD");
    var endDateInput = new JTextField("YYYY-MM-DD");

    var toolbar = new JToolBar();
    toolbar.setFloatable(false);
    toolbar.add(saveButton);
    toolbar.add(loadButton);
    toolbar.addSeparator();
    toolbar.add(insertButton);
    toolbar.add(deleteButton);
    toolbar.addSeparator();
    toolbar.add(categoryInput);
    toolbar.addSeparator();
    toolbar.add(new JLabel("From: "));
    toolbar.add(startDateInput);
    toolbar.add(new JLabel(" To: "));
    toolbar.add(endDateInput);

    add(toolbar, BorderLayout.NORTH);

    var table = new JTable(model, ExpenseTableModel.createColumnModel());

    add(new JScrollPane(table), BorderLayout.CENTER);

    pack();

    setVisible(true);

    try {
      tracker.load(Path.of("./test-expenses.json"));
    } catch (IOException ignored) {}
    resetView();
  }

  private void refreshModel() {
    model.refreshFromView();
  }

  private void resetView() {
    view = tracker.getExpenses();
    refreshModel();
  }

  private final class ExpenseTableModel extends AbstractTableModel {
    private List<Expense> expenses;

    private void refreshFromView() {
      expenses = view.toList();
      fireTableDataChanged();
    }

    private ExpenseTableModel() {
      refreshFromView();
    }

    private Expense getExpense(int rowIndex) {
      if (rowIndex < 0 || rowIndex >= expenses.size()) {
        return null;
      } else {
        return expenses.get(rowIndex);
      }
    }

    @Override
    public int getRowCount() {
      return expenses.size();
    }

    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
      return switch (columnIndex) {
        case 0 -> "Date";
        case 1 -> "Category";
        case 2 -> "Amount";
        case 3 -> "Description";
        default -> Integer.toString(columnIndex);
      };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return switch (columnIndex) {
        case 0 -> LocalDate.class;
        case 1 -> Category.class;
        case 2 -> BigDecimal.class;
        case 3 -> String.class;
        default -> Object.class;
      };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      var expense = getExpense(rowIndex);
      if (expense == null) return null;
      return switch (columnIndex) {
        case 0 -> expense.getDate();
        case 1 -> expense.getCategory();
        case 2 -> expense.getAmount();
        case 3 -> expense.getDescription();
        default -> null;
      };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      var expense = getExpense(rowIndex);
      if (expense == null) return;
      var input = String.valueOf(aValue).trim();
      try {
        switch (columnIndex) {
          case 0 -> expense.setDate(LocalDate.parse(input));
          case 1 -> expense.setCategory(Category.fromName(input));
          case 2 -> expense.setAmount(new BigDecimal(input.replace("$", "")));
          case 3 -> expense.setDescription(input);
          default -> throw new NoSuchElementException();
        }
        fireTableCellUpdated(rowIndex, columnIndex);
      } catch (DateTimeParseException ignored) {
        displayInvalidDateMessage(input);
      } catch (NumberFormatException ignored) {
        displayInvalidAmountMessage(input);
      } catch (NoSuchElementException ignored) {}
    }

    private static DefaultTableColumnModel createColumnModel() {
      var dateColumn = new TableColumn(0);
      dateColumn.setHeaderValue("Date");
      dateColumn.setPreferredWidth(20);
      dateColumn.setCellEditor(new DefaultCellEditor(new JTextField()));

      var categoryColumn = new TableColumn(1);
      categoryColumn.setHeaderValue("Category");
      categoryColumn.setPreferredWidth(100);
      categoryColumn.setCellEditor(new DefaultCellEditor(new JTextField()));

      var amountColumn = new TableColumn(2);
      amountColumn.setHeaderValue("Amount");
      amountColumn.setPreferredWidth(10);
      amountColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
      amountColumn.setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        protected void setValue(Object value) {
          BigDecimal amount = (BigDecimal) value;
          super.setValue(NumberFormat.getCurrencyInstance(Locale.CANADA).format(amount));
        }
      });

      var descriptionColumn = new TableColumn(3);
      descriptionColumn.setHeaderValue("Description");
      descriptionColumn.setPreferredWidth(180 + 90);

      var columnModel = new DefaultTableColumnModel();
      columnModel.addColumn(dateColumn);
      columnModel.addColumn(categoryColumn);
      columnModel.addColumn(amountColumn);
      columnModel.addColumn(descriptionColumn);
      return columnModel;
    }
  }

  private void displayInvalidDateMessage(String input) {
    JOptionPane.showMessageDialog(
      SwingUI.this,
      "Invalid date format, expected YYYY-MM-DD, got \"" + input + "\"",
      "Error: Invalid Date",
      JOptionPane.ERROR_MESSAGE
    );
  }

  private void displayInvalidAmountMessage(String input) {
    JOptionPane.showMessageDialog(
      SwingUI.this,
      "Invalid amount format, expected $12.34, got \"" + input + "\"",
      "Error: Invalid Amount",
      JOptionPane.ERROR_MESSAGE
    );
  }

  private void displayEmptyCategoryMessage() {
    JOptionPane.showMessageDialog(
      SwingUI.this,
      "Expense cannot have an empty category.",
      "Error: Empty Category",
      JOptionPane.ERROR_MESSAGE
    );
  }

}
