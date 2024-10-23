package com.paycraft.base;


import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment implements BaseView {
    protected BaseView mBaseView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseView = (BaseView) context;
    }

    @Override
    public void showToast(@NonNull String message) {
        mBaseView.showToast(message);
    }

    @Override
    public void showProgress() {
        mBaseView.showProgress();
    }

    @Override
    public void dismissProgress() {
        mBaseView.dismissProgress();
    }

    @Override
    public void sessionExpired() {
        mBaseView.sessionExpired();
    }

    @Override
    public void dateTimeDialog(@NonNull EditText targetView, boolean isDateEnabled, boolean isTimeEnabled) {
        mBaseView.dateTimeDialog(targetView, isDateEnabled, isTimeEnabled);
    }

    @Override
    public void showStaticError(@NonNull String message) {
    }
}
