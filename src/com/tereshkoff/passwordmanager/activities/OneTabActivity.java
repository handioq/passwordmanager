package com.tereshkoff.passwordmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import com.tereshkoff.passwordmanager.models.Password;
import com.tereshkoff.passwordmanager.utils.Constants;
import com.tereshkoff.passwordmanager.json.JsonFilesWorker;
import com.tereshkoff.passwordmanager.json.JsonParser;
import com.tereshkoff.passwordmanager.R;
import com.tereshkoff.passwordmanager.models.GroupsList;
import com.tereshkoff.passwordmanager.adapters.PasswordsAdapter;

import java.io.IOException;

public class OneTabActivity extends Fragment {

    private ListView listView1;
    private ImageButton floatButton;
    GroupsList groupsList;

    PasswordsAdapter groupAdapter;

    ActionMode actionMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.tab_one, container, false);

        floatButton = (ImageButton) V.findViewById(R.id.imageButton);
        listView1 = (ListView) V.findViewById(R.id.listView1);

        //JsonFilesWorker.createDefaultBase(Constants.DAFAULT_DBFILE_NAME);  // TODO: DELETE! THIS IS JUST FOR TEST

        groupsList = JsonParser.getGroupsList(JsonFilesWorker.readFile(Constants.PWDIRECTORY, Constants.DAFAULT_DBFILE_NAME));

        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent addPasswordIntent = new Intent(getActivity(), AddPasswordActivity.class);
                addPasswordIntent.putExtra("groupsList", groupsList);
                startActivityForResult(addPasswordIntent, 0);

            }
        });

        groupAdapter = new PasswordsAdapter(getActivity(), android.R.layout.simple_list_item_1,
                groupsList.getAllPasswords().getPasswordList());
        listView1.setAdapter(groupAdapter);

        listView1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView1.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.actionmode, menu);
                return true;
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                SparseBooleanArray sbArray = listView1.getCheckedItemPositions();
                for (int i = 0; i < sbArray.size(); i++) {
                    int key = sbArray.keyAt(i);
                    if (sbArray.get(key))
                        Log.d("KEK", "");
                        // if checked, do something
                }

                mode.finish();
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                Log.d("LOG", "position = " + position + ", checked = "
                        + checked);
            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getActivity(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                //Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                //intent.putExtra("passwordList", groupsList.getGroupByName(parent.getItemAtPosition(position).toString()));
                //startActivity(intent);

                Intent passwordInformIntent = new Intent(getActivity(), PasswordActivity.class);
                passwordInformIntent.putExtra("password",
                        groupsList.getAllPasswords().getPasswordById(parent.getItemAtPosition(position).toString()));
                passwordInformIntent.putExtra("groupsList", groupsList);
                startActivityForResult(passwordInformIntent, 1);

            }
        });

        return V;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case 0:
                if (data == null) {return;}

                groupsList = (GroupsList) data.getExtras().getSerializable("groupsList");

                if (data != null)
                {
                    Toast.makeText(getActivity(), "Пароль успешно добавлен!", Toast.LENGTH_SHORT).show();

                    try
                    {
                        JsonFilesWorker.saveToFile(Constants.DAFAULT_DBFILE_NAME, groupsList);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    groupsList = JsonParser.getGroupsList(JsonFilesWorker.readFile(Constants.PWDIRECTORY, Constants.DAFAULT_DBFILE_NAME));
                    groupAdapter.refreshEvents(groupsList.getAllPasswords().getPasswordList());
                }

                break;

            case 1:
                if (data == null) {return;}
                Password editedPassword = (Password) data.getExtras().getSerializable("password");
                //groupsList.getGroupByName(editedPassword.getGroupName()).getPasswordList().edit(editedPassword);

                // TODO: refactoring for password

                Toast.makeText(getActivity(), "Пароль успешно изменен!", Toast.LENGTH_LONG).show();

                try
                {
                    JsonFilesWorker.saveToFile(Constants.DAFAULT_DBFILE_NAME, groupsList);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                groupsList = JsonParser.getGroupsList(JsonFilesWorker.readFile(Constants.PWDIRECTORY, Constants.DAFAULT_DBFILE_NAME));
                groupAdapter.refreshEvents(groupsList.getAllPasswords().getPasswordList());

                break;


        }

    }


}
