package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private Button btnChangePassword, btnLogout;
    private Switch switchDarkMode, switchNotifications;

    public SettingsFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        tvUserName = v.findViewById(R.id.tvUserName);
        tvUserEmail = v.findViewById(R.id.tvUserEmail);
        /*btnChangePassword = v.findViewById(R.id.btnChangePassword);*/
        btnLogout = v.findViewById(R.id.btnLogout);
        /*switchDarkMode = v.findViewById(R.id.switchDarkMode);
        switchNotifications = v.findViewById(R.id.switchNotifications);*/

        // Load user info
        SharedPreferences sp = getContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sp.getString("user_email", "user@example.com");
        String name = sp.getString("user_name", "User Name");

        tvUserName.setText(name);
        tvUserEmail.setText(email);

        /*btnChangePassword.setOnClickListener(view ->
                Toast.makeText(getContext(), "Change Password clicked", Toast.LENGTH_SHORT).show());*/

        btnLogout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        /*switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(getContext(), "Dark mode: " + isChecked, Toast.LENGTH_SHORT).show());

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(getContext(), "Notifications: " + isChecked, Toast.LENGTH_SHORT).show());*/

        return v;
    }
}
