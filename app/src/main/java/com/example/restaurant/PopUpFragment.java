package com.example.restaurant;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PopUpFragment extends DialogFragment {
    private static final String ARG_MESSAGE = "message";

    public static PopUpFragment newInstance(String message) {
        PopUpFragment fragment = new PopUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up, container, false);

        TextView messageTextView = view.findViewById(R.id.popup_message);
        Button okButton = view.findViewById(R.id.ok_button);

        if (getArguments() != null) {
            String message = getArguments().getString(ARG_MESSAGE);
            messageTextView.setText(message);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                String message = getArguments().getString(ARG_MESSAGE);

                if (message != null && message.equals("Berhasil membuat akun. Silakan login!")) {
                    Intent intent = new Intent(requireContext(), Login.class);
                    startActivity(intent);
                } else if (message != null && message.equals("Logout berhasil. Bye bye!")) {
                    Intent intent = new Intent(requireContext(), Login.class);
                    startActivity(intent);
                } else if (message != null && message.equals("Berhasil membuat pesanan")) {
//                    Intent intent = new Intent(requireContext(), MainActivity.class);
//                    startActivity(intent);
                } else if (message != null && message.equals("Berhasil mengupdate akun")) {
//                    Intent intent = new Intent(requireContext(), MainActivity.class);
//                    startActivity(intent);
                }
            }
        });

        return view;
    }
}