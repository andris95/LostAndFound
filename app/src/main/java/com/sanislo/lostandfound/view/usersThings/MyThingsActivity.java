package com.sanislo.lostandfound.view.usersThings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanislo.lostandfound.R;
import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.utils.ImageUtils;
import com.sanislo.lostandfound.view.BaseActivity;
import com.sanislo.lostandfound.view.addThing.AddThingEditableActivity;
import com.sanislo.lostandfound.view.thingDetails.ThingDetailsActivity;
import com.sanislo.lostandfound.view.things.ThingsContract;
import com.sanislo.lostandfound.view.things.ThingsPresenter;
import com.sanislo.lostandfound.view.things.adapter.ThingAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 10.06.17.
 */

public class MyThingsActivity extends BaseActivity implements ThingsContract.View {
    public static final String TAG = MyThingsActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_things)
    RecyclerView rvThings;

    private ThingsPresenter mThingsPresenter;
    private ThingAdapter mThingAdapter;

    private LinearLayoutManager mLinearLayoutManager;
    private Bitmap mLeftBitmap;
    private Bitmap mRightBitmap;

    private ThingAdapter.OnClickListener mThingClickListener = new ThingAdapter.OnClickListener() {
        @Override
        public void onClickRootView(View view, Thing thing) {
            Intent intent = AddThingEditableActivity.buildLaunchIntent(MyThingsActivity.this, thing);
            View ivThingPhoto = ButterKnife.findById(view, R.id.iv_thing_photo);
            View ivAvatar = ButterKnife.findById(view, R.id.iv_thing_author_avatar);
            Pair<View, String> p2 = Pair.create(ivThingPhoto, getString(R.string.transition_description_photo));
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(MyThingsActivity.this, p2);
            intent.putExtra(ThingDetailsActivity.EXTRA_THING, thing);
            startActivity(intent, options.toBundle());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_my_things);
        ButterKnife.bind(this);
        initBitmapsForSwipe();
        initToolbar();
        initThingsAdapter();
        mThingsPresenter = new ThingsPresenter(this);
        mThingsPresenter.loadMyThings(getAuthenticatedUserUID());
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.my_things);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initBitmapsForSwipe() {
        Drawable drawable = ContextCompat.getDrawable(MyThingsActivity.this, R.drawable.bookmark_check);
        mLeftBitmap = ImageUtils.drawableToBitmap(drawable);

        drawable = ContextCompat.getDrawable(MyThingsActivity.this, R.drawable.delete);
        mRightBitmap = ImageUtils.drawableToBitmap(drawable);
    }

    private void initThingsAdapter() {
        mThingAdapter = new ThingAdapter(MyThingsActivity.this,
                mThingClickListener,
                mThingList);
        mThingAdapter.setEditableLayout(true);
        mThingAdapter.setOnEditListener(new ThingAdapter.OnEditListener() {
            @Override
            public void onClickEdit(Thing thing, int position) {
                showActionDialog(thing, position);
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvThings.setLayoutManager(mLinearLayoutManager);
        rvThings.setAdapter(mThingAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        //TODO attach this or not?
        //itemTouchHelper.attachToRecyclerView(rvThings);
    }

    private void showActionDialog(final Thing thing, final int position) {
        new MaterialDialog.Builder(this)
                .items(R.array.my_things_actions)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                Intent intent = AddThingEditableActivity.buildLaunchIntent(MyThingsActivity.this, thing);
                                intent.putExtra(ThingDetailsActivity.EXTRA_THING, thing);
                                startActivity(intent);
                                break;
                            case 1:
                                deleteThing(thing, position);
                                break;
                        }
                    }
                })
                .show();
    }

    private void deleteThing(Thing thing, int position) {
        int id = thing.getId();
        mThingsPresenter.removeThing(id);
        mThingAdapter.removeItem(position);
    }

    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();

                    if (direction == ItemTouchHelper.LEFT){
                        //mThingAdapter.removeItem(position);
                        int id = mThingAdapter.getItem(position).getId();
                        mThingsPresenter.removeThing(id);
                        mThingAdapter.removeItem(position);
                        makeToast("DELETE pos" + position + " / " + id);
                    } else {
                        Thing thing = mThingAdapter.getItem(position);
                        mThingsPresenter.updateThing(thing.getId(), true);
                    }
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    MyThingsActivity.this.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };

    private void onChildDraw(Canvas c,
                             RecyclerView recyclerView,
                             RecyclerView.ViewHolder viewHolder,
                             float dX,
                             float dY,
                             int actionState,
                             boolean isCurrentlyActive) {
        Paint p = new Paint();
        //right positive, left negative
        Log.d(TAG, "onChildDraw: dX: " + dX);
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            //float width = height / 3;
            float width = mRightBitmap.getWidth();
            Log.d(TAG, "onChildDraw: width: " + width);

            if(dX > 0){
                p.setColor(Color.parseColor("#388E3C"));
                RectF background = new RectF((float) itemView.getLeft(),
                        (float) itemView.getTop(),
                        dX,
                        (float) itemView.getBottom());
                c.drawRect(background,p);
                RectF iconDest = new RectF(
                        (float) itemView.getLeft() + width,
                        (float) itemView.getTop() + width,
                        (float) itemView.getLeft()+ 2*width,
                        (float)itemView.getBottom() - width);
                c.drawBitmap(mLeftBitmap, null, iconDest, p);
            } else {
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF(
                        (float) itemView.getRight() + dX,
                        (float) itemView.getTop(),
                        (float) itemView.getRight(),
                        (float) itemView.getBottom());
                c.drawRect(background, p);
                RectF iconDest = new RectF(
                        (float) itemView.getRight() - 2*width,
                        (float) itemView.getTop() + width,
                        (float) itemView.getRight() - width,
                        (float)itemView.getBottom() - width);
                c.drawBitmap(mRightBitmap, null, iconDest, p);
            }
        }
    }

    @Override
    public void setPresenter(ThingsContract.Presenter presenter) {

    }

    private List<Thing> mThingList = new ArrayList<>();
    @Override
    public void showThings(List<Thing> thingList) {
        mThingList = thingList;
        mThingAdapter.setThingList(mThingList);
        mThingAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        makeToast("Error");
    }
}
