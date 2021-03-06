package com.tereshkoff.passwordmanager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.*;
import android.widget.*;
import com.tereshkoff.passwordmanager.R;
import com.tereshkoff.passwordmanager.adapters.IconAdapter;
import com.tereshkoff.passwordmanager.models.Group;
import com.tereshkoff.passwordmanager.models.GroupsList;
import com.tereshkoff.passwordmanager.models.Password;
import com.tereshkoff.passwordmanager.utils.Clipboard;
import com.tereshkoff.passwordmanager.utils.StringUtils;

import java.util.List;

public class PasswordActivity extends Activity {

    private EditText usernameAddEdit;
    private EditText passwordAddEdit;
    private EditText notesAddEdit;
    private EditText emailAddEdit;
    private EditText siteAddEdit;
    private Spinner spinner;
    private TextView notesTextView;
    private TextView emailTextView;
    private TextView siteTextView;
    private ImageView emailImageView;
    private ImageView siteImageView;
    private Spinner iconSpinner2;

    private String username;
    private String password;
    private String selectedGroup;
    private String notes;
    private String email;
    private String site;
    private int iconID;

    private Password editPassword;

    private ImageView showPasswordState;
    private Boolean showPassword;

    List<String> groupNames;
    private GroupsList groupsList;

    // TODO: ADD OPEN PASSWORD URL IN BROWSER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_activity);

        usernameAddEdit = (EditText) findViewById(R.id.usernameAddEdit);
        passwordAddEdit = (EditText) findViewById(R.id.passwordAddEdit);
        spinner = (Spinner) findViewById(R.id.spinner);
        notesAddEdit = (EditText) findViewById(R.id.notesAddEdit);
        siteAddEdit = (EditText) findViewById(R.id.siteAddEdit);
        emailAddEdit = (EditText) findViewById(R.id.emailAddEdit);
        notesTextView = (TextView) findViewById(R.id.notesTextView2);
        siteTextView = (TextView) findViewById(R.id.siteTextView2);
        emailTextView = (TextView) findViewById(R.id.emailTextView2);
        emailImageView = (ImageView) findViewById(R.id.emailImageView);
        siteImageView = (ImageView) findViewById(R.id.siteImageView);
        iconSpinner2 = (Spinner) findViewById(R.id.iconSpinner2);
        showPasswordState = (ImageView) findViewById(R.id.showPasswordState);
        showPassword = false;

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

                hideElements(selectedItemPosition);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        showPasswordState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmpty(passwordAddEdit))
                {
                    showPassword = !showPassword;
                    if (showPassword) {
                        passwordAddEdit.setTransformationMethod(null);
                        showPasswordState.setImageResource(R.drawable.noeye);
                    } else {
                        passwordAddEdit.setTransformationMethod(new PasswordTransformationMethod());
                        showPasswordState.setImageResource(R.drawable.eye);
                    }
                    passwordAddEdit.setSelection(passwordAddEdit.getText().length());
                }
            }
        });


        usernameAddEdit.setText(editPassword.getUsername());
        passwordAddEdit.setText(editPassword.getPassword());
        usernameAddEdit.setSelection(usernameAddEdit.getText().length());
        notesAddEdit.setText(editPassword.getNotes());
        emailAddEdit.setText(editPassword.getEmail());
        siteAddEdit.setText(editPassword.getSite());

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                Clipboard.setClipboard(getApplicationContext(), StringUtils.removeWhitespaces(passwordAddEdit.getText().toString()));
                Toast.makeText(getApplicationContext(), "Пароль скопирован в буфер обмена..", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        passwordAddEdit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        final GestureDetector gestureDetector2 = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(siteAddEdit.getText().toString()));
                startActivity(intent);
                return true;
            }
        });

        siteAddEdit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector2.onTouchEvent(event);
            }
        });

        // icon spinner:

        String[] strings = {"CoderzHeaven","Google",
                "Microsoft"};

        iconSpinner2.setAdapter(new IconAdapter(this, R.layout.icon_row, strings)); // need to add iconNumber in pw model

        iconSpinner2.setSelection(editPassword.getIconID());

        iconSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                iconID = selectedItemPosition;

                //hideElements(selectedItemPosition);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
                site, email, notes, iconID);

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

    public void copyPasswordToClipboard(MenuItem item)
    {
        Clipboard.setClipboard(this, passwordAddEdit.getText().toString());
    }

    public void copyUsernameToClipboard(MenuItem item)
    {
        Clipboard.setClipboard(this, usernameAddEdit.getText().toString());
    }

    private void hideElements(int selectedItemPosition)
    {
        switch(selectedItemPosition)
        {
            case 0: // Social
                siteAddEdit.setVisibility(View.VISIBLE);
                emailAddEdit.setVisibility(View.VISIBLE);
                siteTextView.setVisibility(View.VISIBLE);
                emailTextView.setVisibility(View.VISIBLE);
                emailImageView.setVisibility(View.VISIBLE);
                siteImageView.setVisibility(View.VISIBLE);
                break;
            case 1: // Email
                siteAddEdit.setVisibility(View.VISIBLE);
                emailAddEdit.setVisibility(View.VISIBLE);
                siteTextView.setVisibility(View.VISIBLE);
                emailTextView.setVisibility(View.VISIBLE);
                emailImageView.setVisibility(View.VISIBLE);
                siteImageView.setVisibility(View.VISIBLE);
                break;
            case 2: // WebSites
                siteAddEdit.setVisibility(View.VISIBLE);
                emailAddEdit.setVisibility(View.VISIBLE);
                siteTextView.setVisibility(View.VISIBLE);
                emailTextView.setVisibility(View.VISIBLE);
                emailImageView.setVisibility(View.VISIBLE);
                siteImageView.setVisibility(View.VISIBLE);
                break;
            case 3: // PC
                siteAddEdit.setVisibility(View.GONE);
                emailAddEdit.setVisibility(View.GONE);
                siteTextView.setVisibility(View.GONE);
                emailTextView.setVisibility(View.GONE);
                emailImageView.setVisibility(View.GONE);
                siteImageView.setVisibility(View.GONE);
                break;
            case 4: // PIN-CODE
                siteAddEdit.setVisibility(View.GONE);
                emailAddEdit.setVisibility(View.GONE);
                siteTextView.setVisibility(View.GONE);
                emailTextView.setVisibility(View.GONE);
                emailImageView.setVisibility(View.GONE);
                siteImageView.setVisibility(View.GONE);
                break;
            case 5: // Wi-Fi
                siteAddEdit.setVisibility(View.GONE);
                emailAddEdit.setVisibility(View.GONE);
                siteTextView.setVisibility(View.GONE);
                emailTextView.setVisibility(View.GONE);
                emailImageView.setVisibility(View.GONE);
                siteImageView.setVisibility(View.GONE);
                break;
            case 6: // Other
                siteAddEdit.setVisibility(View.VISIBLE);
                emailAddEdit.setVisibility(View.VISIBLE);
                siteTextView.setVisibility(View.VISIBLE);
                emailTextView.setVisibility(View.VISIBLE);
                emailImageView.setVisibility(View.VISIBLE);
                siteImageView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }


}
