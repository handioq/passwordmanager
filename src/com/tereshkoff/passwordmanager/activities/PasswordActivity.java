package com.tereshkoff.passwordmanager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.tereshkoff.passwordmanager.R;
import com.tereshkoff.passwordmanager.models.Group;
import com.tereshkoff.passwordmanager.models.GroupsList;
import com.tereshkoff.passwordmanager.models.Password;
import com.tereshkoff.passwordmanager.utils.Clipboard;

import java.util.List;

public class PasswordActivity extends Activity {

    private EditText usernameAddEdit;
    private EditText passwordAddEdit;
    private EditText notesAddEdit;
    private EditText emailAddEdit;
    private EditText siteAddEdit;
    private Spinner spinner;

    private String username;
    private String password;
    private String selectedGroup;
    private String notes;
    private String email;
    private String site;

    private Password editPassword;
    private CheckBox showPasswordCheckBox;

    List<String> groupNames;
    private GroupsList groupsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_activity);

        usernameAddEdit = (EditText) findViewById(R.id.usernameAddEdit);
        passwordAddEdit = (EditText) findViewById(R.id.passwordAddEdit);
        showPasswordCheckBox = (CheckBox) findViewById(R.id.showPasswordCheckBox);
        spinner = (Spinner) findViewById(R.id.spinner);
        notesAddEdit = (EditText) findViewById(R.id.notesAddEdit);
        siteAddEdit = (EditText) findViewById(R.id.siteAddEdit);
        emailAddEdit = (EditText) findViewById(R.id.emailAddEdit);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        editPassword = (Password) i.getSerializableExtra("password");
        groupsList = (GroupsList) i.getSerializableExtra("groupsList");

        groupNames = groupsList.getGroupsNames();

        ArrayAdapter<?> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        selectedGroup = editPassword.getGroupName();
        int selectGroupPosition = groupNames.indexOf(editPassword.getGroupName());
        spinner.setSelection(selectGroupPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                selectedGroup = groupNames.get(selectedItemPosition);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        showPasswordCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showPasswordCheckBox.isChecked())
                {
                    passwordAddEdit.setTransformationMethod(null);
                }
                else
                {
                    passwordAddEdit.setTransformationMethod(new PasswordTransformationMethod());
                }
                passwordAddEdit.setSelection(passwordAddEdit.getText().length());
            }
        });

        usernameAddEdit.setText(editPassword.getUsername());
        passwordAddEdit.setText(editPassword.getPassword());
        usernameAddEdit.setSelection(usernameAddEdit.getText().length());
        notesAddEdit.setText(editPassword.getNotes());
        emailAddEdit.setText(editPassword.getEmail());
        siteAddEdit.setText(editPassword.getSite());

    }

    public void editPassword()
    {
        password = passwordAddEdit.getText().toString();
        username = usernameAddEdit.getText().toString();
        notes = notesAddEdit.getText().toString();
        site = siteAddEdit.getText().toString();
        email = emailAddEdit.getText().toString();

        int oldID = editPassword.getId();
        String oldGroup = editPassword.getGroupName();

        editPassword = new Password(username, password, selectedGroup, oldID,
                site, email, notes);

        Intent intent = new Intent();
        intent.putExtra("password", editPassword);
        intent.putExtra("oldGroup", oldGroup);

        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  // TODO: PASSWORD ACTIVITY MENU CHANGED WITH COPY TO CLIPBOARD IMPL
        getMenuInflater().inflate(R.menu.password_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                editPassword();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        editPassword();
    }

    public void onRemoveMenuClick(MenuItem item)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Подтвердите удаление");
        alertDialog.setMessage("Вы уверены, что хотите удалить пароль?");
        alertDialog.setIcon(R.drawable.remove);

        alertDialog.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "НЕТ", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                //Toast.makeText(getApplicationContext(), "ДА", Toast.LENGTH_SHORT).show();
                removePassword();
            }
        });

        alertDialog.show();
    }

    public void removePassword()
    {
        Intent intent = new Intent();
        intent.putExtra("isToRemove", true);
        intent.putExtra("password", editPassword);

        setResult(RESULT_OK, intent);

        finish();
    }

    public void copyPasswordToClipboard(View view)
    {
        Clipboard.setClipboard(this, passwordAddEdit.getText().toString());
    }

}
