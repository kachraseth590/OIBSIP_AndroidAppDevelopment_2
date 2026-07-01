package com.kktdeveloper.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEditTaskActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etDateTime;
    private MaterialButton btnSave;

    private DatabaseReference tasksRef;
    private String taskId; // null when adding a new task
    private final Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        TextView tvHeader = findViewById(R.id.tvHeader);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDateTime = findViewById(R.id.etDateTime);
        btnSave = findViewById(R.id.btnSave);

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks").child(uid);

        etDateTime.setOnClickListener(v -> showDatePicker());

        taskId = getIntent().getStringExtra(MainActivity.EXTRA_TASK_ID);
        if (taskId != null) {
            // Editing an existing task: pre-fill fields
            tvHeader.setText(R.string.edit_task);
            etTitle.setText(getIntent().getStringExtra(MainActivity.EXTRA_TASK_TITLE));
            etDescription.setText(getIntent().getStringExtra(MainActivity.EXTRA_TASK_DESCRIPTION));
            etDateTime.setText(getIntent().getStringExtra(MainActivity.EXTRA_TASK_DATETIME));
        } else {
            tvHeader.setText(R.string.add_task);
        }

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            showTimePicker();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);

            String formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d  %02d:%02d",
                    selectedDateTime.get(Calendar.DAY_OF_MONTH),
                    selectedDateTime.get(Calendar.MONTH) + 1,
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE));
            etDateTime.setText(formatted);
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
    }

    private void saveTask() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String dateTime = etDateTime.getText() != null ? etDateTime.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("title", title);
        taskMap.put("description", description);
        taskMap.put("dateTime", dateTime);
        taskMap.put("timestamp", selectedDateTime.getTimeInMillis());

        if (taskId == null) {
            // New task: let Firebase generate a unique key
            String newId = tasksRef.push().getKey();
            if (newId != null) {
                tasksRef.child(newId).setValue(taskMap)
                        .addOnSuccessListener(unused -> finish())
                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
        } else {
            // Update existing task in place
            tasksRef.child(taskId).updateChildren(taskMap)
                    .addOnSuccessListener(unused -> finish())
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}
