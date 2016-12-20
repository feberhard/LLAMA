package org.llama.llama.chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import org.llama.llama.AppConstant;
import org.llama.llama.R;
import org.llama.llama.models.Message;

public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder<Message>> {
    private static final String TAG = ChatAdapter.class.getSimpleName();

    private LayoutInflater inflater = null;
    //    private List<Message> messages;
    private FirebaseArray mSnapshots;

//    public ChatAdapter(Context context, List<Message> messages) {
//        this.messages = messages;
//        inflater = LayoutInflater.from(context);
//    }

    public ChatAdapter(FirebaseArray snapshots) {
//        inflater = LayoutInflater.from(context);
        mSnapshots = snapshots;

        mSnapshots.setOnChangedListener(new FirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                switch (type) {
                    case ADDED:
                        notifyItemInserted(index);
                        break;
                    case CHANGED:
                        notifyItemChanged(index);
                        break;
                    case REMOVED:
                        notifyItemRemoved(index);
                        break;
                    case MOVED:
                        notifyItemMoved(oldIndex, index);
                        break;
                    default:
                        throw new IllegalStateException("Incomplete case statement");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ChatAdapter.this.onCancelled(databaseError);
            }
        });
    }

    public ChatAdapter(Query ref) {
        this(new FirebaseArray(ref));
    }

    @Override
    public BaseViewHolder<Message> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case AppConstant.MESSAGE_ITEM_ME:
                return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_me, parent, false));
            case AppConstant.MESSAGE_ITEM_THEM:
                return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_them, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Message> holder, int position) {
        Message msg = getItem(position);
        holder.setDataOnView(msg);
    }

    @Override
    public int getItemCount() {
        return mSnapshots.getCount();
//        return (messages != null && messages.size() > 0) ? messages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = getItem(position);

        if (msg != null)
            return getMessageType(msg);

        return super.getItemViewType(position);
    }


    private int getMessageType(Message msg) {
        if (msg.getViewType() == -1) {
            //TODO gets called and calculated pretty often
            if (Math.random() < 0.5) {
                msg.setViewType(AppConstant.MESSAGE_ITEM_ME);
            } else {
                msg.setViewType(AppConstant.MESSAGE_ITEM_THEM);
            }
        }
        return msg.getViewType();
    }

    protected void onCancelled(DatabaseError error) {
        Log.w(TAG, error.toException());
    }

    public void cleanup() {
        mSnapshots.cleanup();
    }

    public Message getItem(int position) {
        return parseSnapshot(mSnapshots.getItem(position));
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected Message parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(Message.class);
    }

    public DatabaseReference getRef(int position) {
        return mSnapshots.getItem(position).getRef();
    }

    @Override
    public long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(position).getKey().hashCode();
    }
}