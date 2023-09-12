package com.example.expensemanagerapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expensemanagerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    //Firebase datsabase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recyclerview..
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    //Text view..
    private TextView incomeTotalSum;


    /**   // TODO: Rename parameter arguments, choose names that match
     // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
     private static final String ARG_PARAM1 = "param1";
     private static final String ARG_PARAM2 = "param2";

     // TODO: Rename and change types of parameters
     private String mParam1;
     private String mParam2;

     public IncomeFragment() {
     // Required empty public constructor
     }

     /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters

    /**
     * public static IncomeFragment newInstance(String param1, String param2) {
     * IncomeFragment fragment = new IncomeFragment();
     * Bundle args = new Bundle();
     * args.putString(ARG_PARAM1, param1);
     * args.putString(ARG_PARAM2, param2);
     * fragment.setArguments(args);
     * return fragment;
     * }
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**if (getArguments() != null) {
         //mParam1 = getArguments().getString(ARG_PARAM1);
         //mParam2 = getArguments().getString(ARG_PARAM2);
         }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        incomeTotalSum=myview.findViewById(R.id.income_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalvalue = 0;

                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    totalvalue+=data.getAmount();

                    String stTotavalue=String.valueOf(totalvalue);

                    incomeTotalSum.setText(stTotavalue);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false));
            }

            protected void onBindViewHolder(MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
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

        void setType(String type) {
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        void setAmount(int amount){
            TextView mAmount=mView.findViewById(R.id.amount_txt_income);
            String stamount=String.valueOf(amount);
            mAmount.setText(stamount);
        }

    }
}



