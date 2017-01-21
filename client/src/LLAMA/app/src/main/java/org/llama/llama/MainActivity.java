package org.llama.llama;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.llama.llama.auth.SignInActivity;
import org.llama.llama.chat.ChatActivity;
import org.llama.llama.map.MapsActivity;
import org.llama.llama.model.Chat;
import org.llama.llama.model.User;
import org.llama.llama.services.IChatService;
import org.llama.llama.services.IUserService;
import org.llama.llama.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener {

    @Inject
    IChatService chatService;

    @Inject
    IUserService userService;

    private List<Chat> chats;

    private ListView chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApp) getApplication()).getServiceComponent().inject(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabDialog = (FloatingActionButton) findViewById(R.id.fabDialog);
        fabDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                UserDialogFragment userDialog = new UserDialogFragment();
                userDialog.show(ft, "userdialog");
            }
        });

        FloatingActionButton fabGroup = (FloatingActionButton) findViewById(R.id.fabGroup);
        fabGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatService.createChat();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ImageView profilePicture = (ImageView) header.findViewById(R.id.profilePicture);
        profilePicture.setOnClickListener(this);

        chatList = (ListView) findViewById(R.id.chat_list);
        chatList.setClickable(true);
        chatList.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
//            chatService.read();
        } else if (id == R.id.nav_slideshow) {
            userService.updateFirebaseInstanceIdToken(FirebaseInstanceId.getInstance().getToken());
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        } else if (id == R.id.nav_share) {
//            this.chatService.read();
            //this.chatService.createChat();
        } else if (id == R.id.nav_send) {
            this.updateChatList();
        } else if (id == R.id.nav_change_user) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        } else {
            updateChatList();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Chat chat = (Chat) chatList.getItemAtPosition(position);

        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("chatId", chat.getId());
        b.putString("chatTitle", chat.getTitle());
        b.putBoolean("isGroup", chat.getType().equals("group"));

        intent.putExtras(b);
        startActivity(intent);
    }

    public void updateChatList() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final String userId = this.userService.getCurrentUserId();

        DatabaseReference ref = database.getReference()
                .child("users")
                .child(userId)
                .child("chats");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                onChildAddedOrChanged(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAddedOrChanged(dataSnapshot, s);
            }

            private void onChildAddedOrChanged(DataSnapshot dataSnapshot, String s) {
                final String chatId = dataSnapshot.getKey();
                final Boolean isInChat = dataSnapshot.getValue(Boolean.class);

                if(!isInChat){
                    return;
                }

                DatabaseReference chatRef = database.getReference().child("chats").child(chatId);
                chatRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Chat c = dataSnapshot.getValue(Chat.class);
                        if(c == null){
                            return;
                        }
                        c.setId(chatId);
                        addChat(c);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // TODO remove from chatlist
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    Map<String, Chat> chatMap = new HashMap<>();

    public synchronized void addChat(Chat c) {
        chatMap.put(c.getId(), c);

        List<Chat> sortedChats = new ArrayList<>(chatMap.values());
        Collections.sort(sortedChats, new Comparator<Chat>() {
            @Override
            public int compare(Chat c1, Chat c2) {
                return (int) (c2.getTimestamp() - c1.getTimestamp());
            }
        });

        ArrayAdapter chatsAdapter = new ChatsAdapter(MainActivity.this,
                R.layout.chat_item,
                sortedChats,
                this.userService.getCurrentUserId(),
                this.userService);
        chatList.setAdapter(chatsAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRequests.LOCATION_FINE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    public void createChat(String userName) {
        chatService.createDialogChat(userName);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}
