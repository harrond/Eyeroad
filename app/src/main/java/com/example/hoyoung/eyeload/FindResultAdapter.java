package com.example.hoyoung.eyeload;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by YoungHoonKim on 2016-11-19.
 */

public class FindResultAdapter extends RecyclerView.Adapter<FindResultAdapter.DerpHolder> {

    private List<FindResultListItem> listData;
    private LayoutInflater inflater;

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int p);

        void onSecondaryIconClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public FindResultAdapter(List<FindResultListItem> listData, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.listData = listData;

    }

    @Override //create view holder object
    public DerpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.place_list_item, parent, false);

        return new DerpHolder(view);
    }

    @Override
    public void onBindViewHolder(DerpHolder holder, int position) {
        FindResultListItem item = listData.get(position);
        holder.title.setText(item.getName());
        holder.writer.setText(item.getAddress());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class DerpHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView writer;
        //private ImageView icon2;
        private View container;

        public DerpHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.lbl_Ritem_book);
            writer = (TextView) itemView.findViewById(R.id.lbl_Ritem_writer);
            container = itemView.findViewById(R.id.cont_Ritem_root);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.cont_Ritem_root) {
                itemClickCallback.onItemClick(getAdapterPosition());
            } else {
                //itemClickCallback.onSecondaryIconClick(getAdapterPosition());
            }
        }
    }
}

