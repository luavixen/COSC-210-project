package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Expense tracker - represents a mutable list of unique expenses
 */
public final class ExpenseTracker implements Persistent {

  private final ArrayList<Expense> expenses = new ArrayList<>();

  public ExpenseTracker() {
    // empty constructor
  }

  // MODIFIES: NOTHING
  // EFFECTS: creates a **copy** of the expense tracker's expense list and returns a view into that state
  public ExpenseTrackerView getExpenses() {
    return new ExpenseTrackerView(expenses);
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: tries to add an expense to the expense tracker, returns true on success, false on duplicate
  public boolean addExpense(Expense expense) {
    if (expenses.contains(expense)) {
      return false;
    } else {
      expenses.add(expense);
      // NOTE: always sort expenses after mutation
      Collections.sort(expenses);
      return true;
    }
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: tries to remove an expense from the expense tracker, returns true on success, false on not present
  public boolean deleteExpense(Expense expense) {
    return expenses.remove(expense);
  }

  private static final class LocalDateAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
      jsonWriter.value(localDate.toString());
    }
    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
      return LocalDate.parse(jsonReader.nextString());
    }
  }

  private static final Gson GSON = new GsonBuilder()
    .setStrictness(Strictness.LENIENT)
    .setPrettyPrinting()
    .serializeNulls()
    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
    .create();

  private static final Type GSON_TYPE = new TypeToken<List<Expense>>() {}.getType();

  // MODIFIES: NOTHING
  // EFFECTS: saves the expense tracker's expense list to a file with the provided path
  @Override
  public void save(Path path) throws IOException {
    String json = GSON.toJson(expenses, GSON_TYPE);
    Files.writeString(path, json);
  }

  // MODIFIES: the expense tracker's expense list
  // EFFECTS: reads the file at the provided path as an expense list and calls addExpense for each expense in the file
  @Override
  public void load(Path path) throws IOException {
    String json = Files.readString(path);
    List<Expense> list = GSON.fromJson(json, GSON_TYPE);
    for (Expense expense : list) {
      addExpense(expense);
    }
  }

}
