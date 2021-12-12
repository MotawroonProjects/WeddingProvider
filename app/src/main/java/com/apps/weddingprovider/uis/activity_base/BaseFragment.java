package com.apps.weddingprovider.uis.activity_base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.apps.weddingprovider.model.UserModel;
import com.apps.weddingprovider.model.UserSettingsModel;
import com.apps.weddingprovider.preferences.Preferences;

import io.paperdb.Paper;

public class BaseFragment extends Fragment {
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    protected String getLang() {
        Paper.init(context);
        String lang = Paper.book().read("lang", "ar");
        return lang;
    }

    protected UserModel getUserModel() {
        Preferences preferences = Preferences.getInstance();
        return preferences.getUserData(context);

    }

    protected void clearUserModel(Context context){
        Preferences preferences = Preferences.getInstance();
        preferences.clearUserData(context);

    }
    protected void setUserModel(UserModel userModel) {
        Preferences preferences = Preferences.getInstance();
        preferences.createUpdateUserData(context, userModel);
    }


    public void setUserSettings(UserSettingsModel userSettingsModel) {
        Preferences preferences = Preferences.getInstance();
        preferences.create_update_user_settings(context, userSettingsModel);
    }

    public UserSettingsModel getUserSettings() {
        Preferences preferences = Preferences.getInstance();
        return preferences.getUserSettings(context);
    }
}
