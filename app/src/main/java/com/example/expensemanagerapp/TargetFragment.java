package com.example.expensemanagerapp;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensemanagerapp.Model.Data;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class TargetFragment extends Fragment {

    // Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference mTargetDatabase;

    private FloatingActionButton fab_target_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview=inflater.inflate(R.layout.fragment_target, container, false);

        // Conecting Firebase
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser= mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mTargetDatabase= FirebaseDatabase.getInstance().getReference().child("TargetData").child(uid);

        //Connect floating button to layout
        fab_target_btn=myview.findViewById(R.id.fb_target_plus_btn);

        fab_target_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetDataInsert();
            }
        });


        return myview;
    }

    private void targetDataInsert() {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.insert_target_item,null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);


        final EditText edtTarget=myview.findViewById(R.id.target_edt);
        final EditText edtDate=myview.findViewById(R.id.date_edt);
        final EditText edtAmount=myview.findViewById(R.id.amount_edt);

        Button btnSave=myview.findViewById(R.id.btn_save);
        Button btnCancel=myview.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String target=edtTarget.getText().toString().trim();
                String date=edtDate.getText().toString().trim();
                String amount=edtAmount.getText().toString().trim();

                if (TextUtils.isEmpty(target)){
                    edtTarget.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(date)){
                    edtDate.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field..");
                    return;
                }

                int amountInt=Integer.parseInt(amount);

                String id= mTargetDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(amountInt,date,target,id,mDate);
                mTargetDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Dados adicionados", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
}