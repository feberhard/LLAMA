package org.llama.llama.chat;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.jdeferred.DoneCallback;
import org.llama.llama.R;
import org.llama.llama.model.Message;
import org.llama.llama.model.User;
import org.llama.llama.services.IUserService;

import java.util.Map;

/**
 * Created by Felix on 6.12.2016.
 */

public class MessageViewHolder extends BaseViewHolder<Message, IUserService> {
    public final TextView txtUsername;
    public final TextView txtMessage;
    public final TextView txtTimestamp;
    public RadioGroup radioGroup;
    private Message msg;
    private String preferedLanguage;

    public MessageViewHolder(View view) {
        this(view, null);
    }

    public MessageViewHolder(View view, final String preferedLanguage) {
        super(view);
        this.preferedLanguage = preferedLanguage;

        if (preferedLanguage != null) {
            RadioButton languageRadioButton = (RadioButton) view.findViewById(R.id.message_btnLanguage);
            languageRadioButton.setText(preferedLanguage);

            radioGroup = (RadioGroup) view.findViewById(R.id.message_radioGroup);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.message_btnLanguage) { // show translated
                        Map<String, String> translations = msg.getTranslations();
                        if (translations != null && translations.containsKey(preferedLanguage)) {
                            txtMessage.setText(translations.get(preferedLanguage));
                        }
                    } else { // show original
                        txtMessage.setText(msg.getMessage());
                    }
                }
            });
        }

        txtUsername = (TextView) view.findViewById(R.id.message_username);
        txtMessage = (TextView) view.findViewById(R.id.message_text);
        txtTimestamp = (TextView) view.findViewById(R.id.message_timestamp);
    }

    @Override
    public void setDataOnView(Message msg, IUserService userService) {
        this.msg = msg;
        if (this.preferedLanguage != null && !msg.getLanguage().equals(this.preferedLanguage)) {
            this.radioGroup.setVisibility(View.VISIBLE);
        }

        Map<String, String> translations = msg.getTranslations();
        if (translations != null && translations.containsKey(preferedLanguage)) {
            txtMessage.setText(translations.get(preferedLanguage));
            this.radioGroup.check(R.id.message_btnLanguage);
        } else {
            txtMessage.setText(msg.getMessage());
        }

        userService.getUserInfo(msg.getUser()).done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                txtUsername.setText(((User) result).getName());
            }
        });

        txtTimestamp.setText(msg.getTimeString());
    }
}
