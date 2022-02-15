package com.example.project;

import java.util.Objects;

public interface FireBaseListener {

    void onSuccess(Object object);
    void onFailed(String errorMessage);

}
