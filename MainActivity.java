package com.example.studenttaskmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {
    private TaskDatabaseHelper db;
    private LinearLayout taskContainer;
    private TextView summary;
    private String selectedDate = "";

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TaskDatabaseHelper(this);
        buildScreen();
        refreshTasks();
    }

    private TextView text(String value, float size, int style) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTypeface(Typeface.DEFAULT, style);
        t.setTextColor(0xFF222222);
        return t;
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(12));
        root.setBackgroundColor(0xFFF8F6FC);

        TextView title = text("Student Task Manager", 26, Typeface.BOLD);
        title.setTextColor(0xFF6750A4);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(42)));

        TextView subtitle = text("Organize your assignments and deadlines", 14, Typeface.NORMAL);
        subtitle.setTextColor(0xFF666666);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(30)));

        summary = text("", 14, Typeface.BOLD);
        summary.setPadding(0, dp(8), 0, dp(8));
        root.addView(summary);

        Button add = new Button(this);
        add.setText("+  ADD NEW TASK");
        add.setOnClickListener(v -> showAddTaskDialog());
        root.addView(add, new LinearLayout.LayoutParams(-1, dp(52)));

        taskContainer = new LinearLayout(this);
        taskContainer.setOrientation(LinearLayout.VERTICAL);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(taskContainer);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
    }

    private void showAddTaskDialog() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(22), 0, dp(22), 0);

        EditText title = new EditText(this);
        title.setHint("Task title");
        box.addView(title);

        EditText desc = new EditText(this);
        desc.setHint("Description (optional)");
        box.addView(desc);

        Button date = new Button(this);
        date.setText("Choose due date");
        box.addView(date);
        date.setOnClickListener(v -> pickDate(date));

        new AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(box)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (d, which) -> {
                    String taskTitle = title.getText().toString().trim();
                    if (taskTitle.isEmpty()) {
                        Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.addTask(taskTitle, desc.getText().toString().trim(), selectedDate);
                    selectedDate = "";
                    refreshTasks();
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void pickDate(Button button) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDate = String.format("%02d-%02d-%04d", day, month + 1, year);
                    button.setText("Due: " + selectedDate);
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void refreshTasks() {
        taskContainer.removeAllViews();
        List<Task> tasks = db.getAllTasks();
        int completed = 0;
        for (Task t : tasks) if (t.completed) completed++;
        summary.setText("Total: " + tasks.size() + "   •   Completed: " + completed +
                "   •   Pending: " + (tasks.size() - completed));

        if (tasks.isEmpty()) {
            TextView empty = text("No tasks yet. Tap “ADD NEW TASK” to begin.", 16, Typeface.NORMAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(60), 0, 0);
            taskContainer.addView(empty);
            return;
        }

        for (Task task : tasks) addTaskCard(task);
    }

    private void addTaskCard(Task task) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(10), dp(10));
        card.setBackgroundColor(task.completed ? 0xFFE8F5E9 : 0xFFFFFFFF);

        TextView title = text(task.title, 18, Typeface.BOLD);
        if (task.completed) title.setText("✓ " + task.title);
        card.addView(title);

        if (!task.description.isEmpty()) {
            TextView desc = text(task.description, 14, Typeface.NORMAL);
            desc.setTextColor(0xFF666666);
            card.addView(desc);
        }

        TextView due = text(task.dueDate.isEmpty() ? "No due date" : "Due: " + task.dueDate, 13, Typeface.NORMAL);
        due.setTextColor(0xFF6750A4);
        card.addView(due);

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.END);

        Button complete = new Button(this);
        complete.setText(task.completed ? "UNDO" : "DONE");
        complete.setOnClickListener(v -> {
            db.setCompleted(task.id, !task.completed);
            refreshTasks();
        });

        Button delete = new Button(this);
        delete.setText("DELETE");
        delete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete task?")
                .setMessage("This task will be permanently removed.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteTask(task.id);
                    refreshTasks();
                }).show());

        actions.addView(complete, new LinearLayout.LayoutParams(dp(100), dp(50)));
        actions.addView(delete, new LinearLayout.LayoutParams(dp(110), dp(50)));
        card.addView(actions);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(10), 0, 0);
        taskContainer.addView(card, params);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
