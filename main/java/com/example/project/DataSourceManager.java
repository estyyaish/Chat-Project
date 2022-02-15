package com.example.project;

import java.util.ArrayList;
import java.util.Objects;

public class DataSourceManager {

    private static DataSourceManager instance;

    public static DataSourceManager getInstance() {
        if(instance == null){
            instance = new DataSourceManager();
        }

        return instance;
    }
    public interface OnDataSourceChange{
        void onMessagesChanges();
        void onLoadingStatusChange();
    }
    private OnDataSourceChange listener;
    public ArrayList<Message> messages;
    public boolean loadingData;
    public boolean isMessagesLoaded;

    private DataSourceManager(){
        messages = new ArrayList<>();
        loadMessages();
    }

    public void setOnDataSourceChangeListener(OnDataSourceChange listener){
        this.listener = listener;
    }

    public void loadMessages(){
        loadingData = true;
        FireBaseManager.getInstance().loadMessages(new FireBaseListener() {
            @Override
            public void onSuccess(Object object) {
                loadingData = false;
                isMessagesLoaded = true;
                messages = (ArrayList<Message>) object;
                if(listener != null){
                    listener.onMessagesChanges();
                    listener.onLoadingStatusChange();
                }

                listenToMessagesChange();
            }

            @Override
            public void onFailed(String errorMessage) {
                loadingData = false;
                if(listener != null){
                    listener.onLoadingStatusChange();
                }
            }
        });
    }

    private void listenToMessagesChange() {
        FireBaseManager.getInstance().listenToMessagesChange(new FireBaseListener() {
            @Override
            public void onSuccess(Object object) {
                Message newMessage = (Message)object;
                addNewMessage(newMessage);
            }

            @Override
            public void onFailed(String errorMessage) {

            }
        });

    }

    private void addNewMessage(Message message) {
        Message oldMessage = getMessageByUid(message.uid);
        boolean isNewMessage=false;
        if(oldMessage != null){
            messages.remove(oldMessage);
        }else{
            isNewMessage = true;
        }
        messages.add(message);
        if(listener != null){
            listener.onMessagesChanges();
            if(isNewMessage){
                if(!message.userUid.equals(Message.USER_UI)) {
                    Utils.playSound(R.raw.floop2_x);
                }
            }
        }
    }

    private Message getMessageByUid(String uid) {
        for (Message m:messages){
            if(m.uid.equals(uid)){
                return m;
            }

        }
        return null;
    }
}
