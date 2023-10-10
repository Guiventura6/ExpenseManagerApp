package com.example.expensemanagerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanagerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

    //Recycler view
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    private String post_key;

    // Data item
    private String title;
    private String dateTarget;
    private int amount;


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

        //Recycler
        recyclerView=myview.findViewById(R.id.recycler_id_target);

        fab_target_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetDataInsert();
            }
        });

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


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

    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mTargetDatabase, Data.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options){

            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.target_recycler_data, parent, false));
            }

            protected void onBindViewHolder(MyViewHolder holder, int position, @NonNull Data model) {
                // Criar um novo modelo de dados para dados de objetivos.
                holder.setAmount(model.getAmount());
                holder.setTarget(model.getType());
                holder.setDateTarget(model.getNote());
                holder.setDateInclude(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int pos=holder.getBindingAdapterPosition();
                        post_key=getRef(pos).getKey();

                        title=model.getType();
                        dateTarget=model.getNote();
                        amount=model.getAmount();

                        // updateDataItem();
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        void setTarget(String target) {
            TextView mType=mView.findViewById(R.id.title_txt_target);
            mType.setText(target);
        }

        void setDateTarget(String dateTarget){
            TextView mNote=mView.findViewById(R.id.date_txt_target);
            mNote.setText(dateTarget);
        }

        void setDateInclude(String date_server){
            TextView mDateInclude=mView.findViewById(R.id.date_server_txt_target);
            mDateInclude.setText(date_server);
        }

        void setAmount(int amount){
            TextView mAmount=mView.findViewById(R.id.amount_txt_target);
            String strAmount=String.valueOf(amount);
            mAmount.setText(strAmount);
        }

    }


}