package com.sanislo.lostandfound.adapter;

/**
 * Created by root on 16.03.17.
 */

public class SelectableAdapter {
}

/*package com.uae.ewallet.view.adapters;

        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.util.SparseBooleanArray;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.CheckBox;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.uae.ewallet.R;
        import com.uae.ewallet.model.data.DocFile;
        import com.makeramen.roundedimageview.RoundedImageView;
        import com.nostra13.universalimageloader.core.DisplayImageOptions;
        import com.nostra13.universalimageloader.core.ImageLoader;

        import java.util.ArrayList;
        import java.util.List;

        import butterknife.BindView;
        import butterknife.ButterKnife;
        import butterknife.OnClick;

public class DocumentsSelectableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = TicketsAdapter.class.getSimpleName();
    private static final int TYPE_DOCUMENT = R.layout.item_document_selectable_simple;
    private static final int TYPE_ADD_DOCUMENT = R.layout.item_add_document;

    private Context mContext;
    private List<DocFile> mDocFiles;
    private LayoutInflater mLayoutInflater;
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoader mImageLoader;
    private DocumentsSelectableAdapter.OnClickListener mOnClickListener;
    private DocumentsSelectableAdapter.OnLongClickListener mOnLongClickListener;
    private DocumentsSelectableAdapter.OnCheckedChangeListener mCheckChangeListener;
    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
    private RecyclerView mRecyclerView;

    public DocumentsSelectableAdapter(Context context, List<DocFile> ticketList) {
        mContext = context;
        mDocFiles = ticketList;
        mLayoutInflater = LayoutInflater.from(mContext);

        mImageLoader = ImageLoader.getInstance();
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.ic_passport_category)
                .showImageOnFail(R.drawable.ic_passport_category)
                .build();
    }

    public void setOnClickListener(DocumentsSelectableAdapter.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setCheckChangeListener(DocumentsSelectableAdapter.OnCheckedChangeListener checkChangeListener) {
        mCheckChangeListener = checkChangeListener;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    private boolean isSelected(int pos) {
        return mSelectedPositions.get(pos, false);
    }

    public boolean isAnyItemSelected() {
        for (int i = 0; i < mSelectedPositions.size(); i++) {
            int keyAt = mSelectedPositions.keyAt(i);
            if (mSelectedPositions.get(keyAt)) {
                return true;
            }
        }
        return false;
    }

    public void toggleSelection(int pos) {
        if (mSelectedPositions.get(pos, false)) {
            mSelectedPositions.delete(pos);
        } else {
            mSelectedPositions.put(pos, true);
        }
        for (int i = 0; i < mDocFiles.size(); i++) {
            if (mSelectedPositions.get(i)) Log.d(TAG, "setItemChecked: pos " + i + " is selected");
        }
    }

    public List<DocFile> getSelectedItems() {
        List<DocFile> items = new ArrayList<DocFile>(mSelectedPositions.size());
        for (int i = 0; i < mSelectedPositions.size(); i++) {
            int keyAt = mSelectedPositions.keyAt(i);
            DocFile docFile = mDocFiles.get(keyAt);
            items.add(docFile);
        }
        return items;
    }

    public int getSelectedItemsCount() {
        for (int i = 0; i < mSelectedPositions.size(); i++) {
            Log.d(TAG, "getSelectedItemsCount: " + mSelectedPositions.keyAt(i));
        }
        return mSelectedPositions.size();
    }

    *//** PLAN B :)
     private void setViewHolderSelected(int position) {
     ((SelectableViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position)).setSelected(isSelected(position));
     }*//*

    public void clearSelections() {
        mSelectedPositions.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = mLayoutInflater.inflate(R.layout.item_document_selectable_cardview, parent, false);
        View view;
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_DOCUMENT:
                view = mLayoutInflater.inflate(R.layout.item_document_selectable_simple, parent, false);
                viewHolder = new DocumentViewHolder(view);
                break;
            case TYPE_ADD_DOCUMENT:
                view = mLayoutInflater.inflate(R.layout.item_add_document, parent, false);
                viewHolder = new AddDocumentViewHolder(view);
                break;
            default:
                view = mLayoutInflater.inflate(R.layout.item_document_selectable_simple, parent, false);
                viewHolder = new DocumentViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: type: " + holder.getItemViewType());
        switch (holder.getItemViewType()) {
            case TYPE_DOCUMENT:
                DocFile docFile = mDocFiles.get(position);
                DocumentViewHolder documentViewHolder = (DocumentViewHolder) holder;
                documentViewHolder.bind(docFile);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDocFiles == null ? 0 : mDocFiles.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDocFiles.size()) {
            return TYPE_ADD_DOCUMENT;
        } else {
            return TYPE_DOCUMENT;
        }
    }

    public interface OnClickListener {
        void onClick(View view, int position);
        void onBarcodeClick();
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange();
    }

    public interface OnLongClickListener {
        void onLongClick(View view, int position);
    }

    class DocumentViewHolder extends RecyclerView.ViewHolder {
        private View mView;

        @BindView(R.id.rv_document_header)
        RelativeLayout rvDocumentHeader;

        @BindView(R.id.rv_document_body)
        RelativeLayout rvDocumentBody;

        @BindView(R.id.iv_document_header_title)
        TextView tvTicketHeader;

        @BindView(R.id.iv_document)
        RoundedImageView ivDocument;

        @BindView(R.id.cb_document_selector)
        CheckBox cbDocumentSelector;


        public DocumentViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, mView);
            mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isAnyItemSelected()) {
                        toggleSelectionFromViewHolder();
                        mCheckChangeListener.onCheckedChange();
                        return true;
                    }
                    return false;
                }
            });
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAnyItemSelected()) {
                        toggleSelectionFromViewHolder();
                        mCheckChangeListener.onCheckedChange();
                    }
                }
            });
            cbDocumentSelector.setClickable(false);
        }

        private void toggleSelectionFromViewHolder() {
            int position = getAdapterPosition();
            toggleSelection(position);
            //notifyItemChanged(position);
            notifyItemRangeChanged(0, mDocFiles.size());
        }

        public void bind(DocFile docFile) {
            tvTicketHeader.setText(docFile.getName());
            displayTicketImage(docFile);
            setSelected();
            setCheckBox();
        }

        private void setCheckBox() {
            boolean isSelected = mSelectedPositions.get(getAdapterPosition(), false);
            cbDocumentSelector.setChecked(isSelected);
        }

        private void setSelected() {
            boolean isAnyItemSelected = isAnyItemSelected();
            boolean isSelected = isSelected(getAdapterPosition());
            if (isAnyItemSelected) {
                rvDocumentHeader.setSelected(!isSelected);
                rvDocumentBody.setSelected(!isSelected);
                ivDocument.setAlpha(isSelected ? 1 : 0.33f);
            } else {
                rvDocumentHeader.setSelected(false);
                rvDocumentBody.setSelected(false);
                ivDocument.setAlpha(1f);
            }
        }

        @OnClick(R.id.btn_barcode)
        public void onBarcodeClick(){
            mOnClickListener.onBarcodeClick();
        }

        private void displayTicketImage(DocFile docFile) {
            mImageLoader.displayImage(docFile.getImageURL(), ivDocument, mDisplayImageOptions);
        }
    }

    class AddDocumentViewHolder extends RecyclerView.ViewHolder {

        public AddDocumentViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Add document", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}*/
