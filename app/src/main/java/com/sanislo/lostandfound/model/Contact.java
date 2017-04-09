package com.sanislo.lostandfound.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.sanislo.lostandfound.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 09.04.17.
 */

public class Contact extends AbstractItem<Contact, Contact.ViewHolder> {
    private String contact;

    public Contact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_contact_removable;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.bind(contact);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_contact)
        TextView tvContact;

        @BindView(R.id.iv_remove)
        ImageView ivRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String contact) {
            tvContact.setText(contact);
            if (getAdapterPosition() == 0) {
                ivRemove.setVisibility(View.GONE);
            }
        }

        public ImageView getIvRemove() {
            return ivRemove;
        }
    }
}
