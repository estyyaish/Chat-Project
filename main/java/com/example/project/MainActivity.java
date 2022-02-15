package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView lv_messages;
    ImageView iv_send;
    EditText et_message;
    ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_messages = findViewById(R.id.lv_messages);
        iv_send = findViewById(R.id.iv_send);
        et_message = findViewById(R.id.et_message);

        iv_send.setOnClickListener(this);
        messages = new ArrayList<>();

        et_message.addTextChangedListener(getMessageChange());

    }

    @Override
    protected void onResume() {
        super.onResume();

        iv_send.setEnabled(et_message.length() > 0);

        DataSourceManager.getInstance().setOnDataSourceChangeListener(onDataSourceChange());
        DataSourceManager.getInstance().loadMessages();
    }

    private DataSourceManager.OnDataSourceChange onDataSourceChange() {
        return new DataSourceManager.OnDataSourceChange() {
            @Override
            public void onMessagesChanges() {
                messages = DataSourceManager.getInstance().messages;
                initAdapter();
            }

            @Override
            public void onLoadingStatusChange() {

            }
        };
    }

    private TextWatcher getMessageChange() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                iv_send.setEnabled(text.length() > 0);
            }
        };

    }


    @Override
    public void onClick(View v) {
        Utils.playSound(R.raw.blip);

        String text = et_message.getText().toString();
        Message message = new Message();
        message.date = new Date();
        message.text = text;

        message.userName = "אסתי יעיש" ;
        message.userUid = Message.USER_UI;

        messages.add(message);
        initAdapter();

        FireBaseManager.getInstance().addMessage(message, new FireBaseListener() {
            @Override
            public void onSuccess(Object object){
                et_message.setText("");
                et_message.setFocusable(true);
                et_message.requestFocus();
            }

            @Override
            public void onFailed(String errorMessage) {
                Utils.showToast(errorMessage);
            }
        });
    }

    private void initAdapter() {
        MessageAdapter adapter = new MessageAdapter();
        lv_messages.setAdapter(adapter);
        lv_messages.setSelection(messages.size()-1);
    }

    class MessageAdapter extends ArrayAdapter<Message> {
        SimpleDateFormat format_HH_mm_ss;

        public MessageAdapter() {
            super(getApplicationContext(), R.layout.message_item_out, messages);
            format_HH_mm_ss = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            Message item = getItem(position);
            boolean isOutMessage = item.userUid.equals(Message.USER_UI);

            if (isOutMessage) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.message_item_out, parent, false);
            }
            else {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.message_item_in, parent, false);
            }

            TextView tv_text = convertView.findViewById(R.id.tv_text);
            TextView tv_time = convertView.findViewById(R.id.tv_time);
            tv_time.setText(format_HH_mm_ss.format(item.date));
            tv_text.setText(item.text);

            if(!isOutMessage){
                TextView tv_name = convertView.findViewById(R.id.tv_name);
                tv_name.setText(item.userName);
            }

            return convertView;

        }

    }
}