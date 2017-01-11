package org.llama.llama.chat.chatinfo;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import org.llama.llama.MainActivity;
import org.llama.llama.R;

/**
 * Created by woernsn on 11.01.17.
 */

public class AddMemberDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.contact_dialog, null))
                .setTitle(R.string.enter_your_friends_username)
                .setPositiveButton(getString(R.string.add_member), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String username = ((EditText) getDialog().findViewById(R.id.username)).getText().toString();
                        ((ChatInfoActivity) getActivity()).addMember(username);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddMemberDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
