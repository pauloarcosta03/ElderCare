package com.eldercare.eldercare.activity.ui.definicoes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.eldercare.eldercare.R;


public class DefinicoesFragment extends Fragment {

    private Button ButtonAddPaciente;
    private Button ButtonRemConta;

    public DefinicoesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_definicoes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButtonAddPaciente = view.findViewById(R.id.ButtonAddPaciente);
        ButtonRemConta = view.findViewById(R.id.ButtonRemConta);

    }
}