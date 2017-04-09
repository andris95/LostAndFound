package com.sanislo.lostandfound.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanislo.lostandfound.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 06.04.17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<String> mContactsList;

    public ContactsAdapter(List<String> contactsList) {
        mContactsList = contactsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return mContactsList == null ? 0 : mContactsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_contact)
        TextView tvContact;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind() {
            displayContact();
        }

        private void displayContact() {
            tvContact.setText(mContactsList.get(getAdapterPosition()));
        }
    }
}
