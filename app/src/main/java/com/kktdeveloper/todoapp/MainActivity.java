package com.kktdeveloper.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kktdeveloper.todoapp.adapter.TaskAdapter;
import com.kktdeveloper.todoapp.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_TASK_TITLE = "extra_task_title";
    public static final String EXTRA_TASK_DESCRIPTION = "extra_task_description";
    public static final String EXTRA_TASK_DATETIME = "extra_task_datetime";

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private TaskAdapter adapter;
    private final List<Task> taskList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference tasksRef;
    private ValueEventListener tasksListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            // Safety net: no logged-in user, send back to login
            goToLogin();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditTaskActivity.class)));

        String uid = mAuth.getCurrentUser().getUid();
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks").child(uid);

        listenForTasks();
    }

    private void listenForTasks() {
        tasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Task task = child.getValue(Task.class);
                    if (task != null) {
                        task.setId(child.getKey());
                        taskList.add(task);
                    }
                }
                // Most recently added tasks on top
                Collections.sort(taskList, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(taskList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        tasksRef.addValueEventListener(tasksListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tasksRef != null && tasksListener != null) {
            tasksRef.removeEventListener(tasksListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            goToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, task.getId());
        intent.putExtra(EXTRA_TASK_TITLE, task.getTitle());
        intent.putExtra(EXTRA_TASK_DESCRIPTION, task.getDescription());
        intent.putExtra(EXTRA_TASK_DATETIME, task.getDateTime());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Task task) {
        // Permanently removes the task node from Firebase
        tasksRef.child(task.getId()).removeValue()
                .addOnSuccessListener(unused ->
                        Toast.makeText(MainActivity.this, R.string.task_deleted, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
