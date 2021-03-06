package com.tereshkoff.passwordmanager.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.tereshkoff.passwordmanager.utils.Constants;
import com.tereshkoff.passwordmanager.json.JsonFilesWorker;
import com.tereshkoff.passwordmanager.json.JsonParser;
import com.tereshkoff.passwordmanager.R;
import com.tereshkoff.passwordmanager.adapters.GroupAdapter;
import com.tereshkoff.passwordmanager.models.GroupsList;

public class TwoTabActivity extends Fragment {

    private ListView listView1;
    private ImageButton floatButton;
    GroupsList groupsList;

    GroupAdapter groupAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.tab_two, container, false);

        //floatButton = (ImageButton) V.findViewById(R.id.imageButton);
        listView1 = (ListView) V.findViewById(R.id.listView1);

        // TODO: GROUP ADD IMPLEMENTATION
        /*floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                startActivity(intent);
            }
        });*/

        //JsonFilesWorker.createDefaultBase("database.json");

        groupsList = JsonParser.getGroupsList(JsonFilesWorker.readFile(Constants.PWDIRECTORY, Constants.DAFAULT_DBFILE_NAME));

        groupAdapter = new GroupAdapter(getActivity(), android.R.layout.simple_list_item_1, groupsList.getGroups());
        listView1.setAdapter(groupAdapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getActivity(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), GroupPasswordsActivity.class);
                intent.putExtra("groupsList", groupsList);
                intent.putExtra("selectedGroup", groupsList.getGroupByName(parent.getItemAtPosition(position).toString()));
                startActivity(intent);

            }
        });

        return V;
    }

    @Override
    public void onResume() {
        super.onResume();

        groupsList = JsonParser.getGroupsList(JsonFilesWorker.readFile(Constants.PWDIRECTORY, Constants.DAFAULT_DBFILE_NAME));
        groupAdapter.refreshEvents(groupsList.getGroups());

    }
}
