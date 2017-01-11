package org.llama.llama.chat.chatinfo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jdeferred.DoneCallback;
import org.llama.llama.MyApp;
import org.llama.llama.R;
import org.llama.llama.model.User;
import org.llama.llama.services.IChatService;
import org.llama.llama.services.IUserService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class ChatInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private String chatId;
    private String chatTitle;
    private boolean isGroup;
    private ActionBar actionBar;

    @Inject
    IUserService userService;
    @Inject
    IChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);
        ((MyApp) getApplication()).getServiceComponent().inject(this);


        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        chatId = null;
        if (b != null) {
            chatId = b.getString("chatId");
            chatTitle = b.getString("chatTitle");
            isGroup = b.getBoolean("isGroup");

            actionBar.setTitle(chatTitle);
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference()
                .child("members")
                .child(chatId);

        BaseAdapter adapter = new FirebaseListAdapter<Object>(this, Object.class, android.R.layout.activity_list_item, ref) {
            @Override
            protected void populateView(final View view, Object omodel, int position) {
                Pair<String, Long> model = (Pair<String, Long>) omodel;

                userService.getUserInfo(model.first).done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        User user = (User) result;
                        ((TextView) view.findViewById(android.R.id.text1)).setText(user.getName());
                        ((ImageView) view.findViewById(android.R.id.icon)).setImageResource(R.mipmap.ic_launcher);
                    }
                });
            }

            protected Pair<String, Long> parseSnapshot(DataSnapshot snapshot) {
                return new Pair<>(snapshot.getKey(), snapshot.getValue(Long.class));
            }
        };
//        BaseAdapter adapter = new FirebaseListAdapter<Object>(this, Object.class, android.R.layout.two_line_list_item, ref) {
//            @Override
//            protected void populateView(final View view, Object omodel, int position) {
//                Pair<String, Long> model = (Pair<String, Long>) omodel;
//
//                userService.getUserInfo(model.first).done(new DoneCallback() {
//                    @Override
//                    public void onDone(Object result) {
//                        User user = (User) result;
//                        ((TextView) view.findViewById(android.R.id.text1)).setText(user.getName());
//                    }
//                });
//
//
//                Date date = new Date(model.second);
//
//                SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
//                // TODO use current timezone
//                //sdf.setTimeZone(TimeZone.getDefault());
//                DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(ChatInfoActivity.this);
//                DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(ChatInfoActivity.this);
//                char[] formatOrder = android.text.format.DateFormat.getDateFormatOrder(ChatInfoActivity.this);
//                String dateString = dateFormat.format(date);
//                String timeString = timeFormat.format(date);
//                ((TextView) view.findViewById(android.R.id.text2)).setText(String.format("%s %s %s", getString(R.string.joined), dateString, timeString));
//            }
//
//            protected Pair<String, Long> parseSnapshot(DataSnapshot snapshot) {
//                return new Pair<>(snapshot.getKey(), snapshot.getValue(Long.class));
//            }
//        };

        ListView chatMembers = (ListView) findViewById(R.id.lvChatMembers);
        chatMembers.setAdapter(adapter);

        EditText editTextChatTitle = (EditText) findViewById(R.id.txtChatTitle);
        editTextChatTitle.setText(chatTitle);

        findViewById(R.id.btnUpdateChatTitle).setOnClickListener(this);
        if (isGroup) {
            findViewById(R.id.btnAddMember).setOnClickListener(this);
        } else {
            findViewById(R.id.btnAddMember).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnUpdateChatTitle) {
            String chatTitle = ((EditText) findViewById(R.id.txtChatTitle)).getText().toString();
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("chats")
                    .child(chatId)
                    .child("title")
                    .setValue(chatTitle);
            actionBar.setTitle(chatTitle);
        } else if (i == R.id.btnAddMember) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            AddMemberDialogFragment amdf = new AddMemberDialogFragment();
            amdf.show(ft, "userdialog");
        }
    }

    public void addMember(String username) {
        chatService.addMember(chatId, username);
    }
}
