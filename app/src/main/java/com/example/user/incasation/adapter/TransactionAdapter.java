package com.example.user.incasation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.incasation.R;
import com.example.user.incasation.domain.Transaction;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactionList;
    private Context context;

    public TransactionAdapter(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.date.setText(convertLocalDate(transaction.getTransactionDate()));
        holder.amount.setText(transaction.getAmount());
        holder.currency.setText(transaction.getCurrency());
        holder.bank.setText(transaction.getDestinationBank());
        holder.bankBranch.setText(transaction.getDestinationBankBranch());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    private String convertLocalDate(String date){
        LocalDateTime localDateTime = LocalDateTime.parse(date);
        return localDateTime.toString(DateTimeFormat.forPattern("dd/MM/yyyy HH:mm"));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView date;
        public TextView amount;
        public TextView currency;
        public TextView bank;
        public TextView bankBranch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            currency = itemView.findViewById(R.id.currency);
            bank = itemView.findViewById(R.id.destinationBank);
            bankBranch = itemView.findViewById(R.id.destinationBankBranch);
        }
    }
}
