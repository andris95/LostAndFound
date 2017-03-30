package com.sanislo.lostandfound.presenter;

import com.sanislo.lostandfound.model.Thing;
import com.sanislo.lostandfound.view.thingDetails.ThingDetailsView;

/**
 * Created by root on 15.01.17.
 */

public class ThingDetailsPresenterImpl implements ThingDetailsPresenter {
    public final String TAG = ThingDetailsPresenterImpl.class.getSimpleName();

    private ThingDetailsView mView;

    public ThingDetailsPresenterImpl(ThingDetailsView view) {
        super();
        mView = view;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void addComment(Thing thing, String text) {
        /*if (TextUtils.isEmpty(text)) {
            text = "Keys found on Aigburth Road, Liverpool, " +
                    "between the Maccies and the garage. " +
                    "It’s got two keyrings on as well but I’ve covered them up for the owner to identify.";
            mView.onError("Text can't be blank!");
            //return;
        }
        DatabaseReference commentRef = mDatabaseReference.child(FirebaseConstants.THINGS_COMMENTS)
                .child(thing.getKey())
                .push();
        Log.d(TAG, "addComment: commentRef: " + commentRef.toString());
        String commentKey = commentRef.getKey();
        long timestamp = new Date().getTime();
        Comment comment = new Comment(commentKey,
                thing.getKey(),
                mUID,
                text,
                timestamp);
        commentRef.setValue(comment, 0 - timestamp);*/
    }
}
