package com.sanislo.lostandfound.utils;

/**
 * Created by root on 19.01.17.
 */

public class Utils {
    //Original
    public static final String getCropedImageUrl(String url) {
        String cropedUrl = "";
        int last = -1;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == '/') {
                last = i;
            }
        }
        if (last != -1) {
            for (int i = 0; i <= last; i++) {
                cropedUrl += url.charAt(i);
            }
            cropedUrl += "crop_";
            for (int i = last + 1; i < url.length(); i++) {
                cropedUrl += url.charAt(i);
            }
        } else {
            cropedUrl = url;
        }
        return cropedUrl;
    }

    //Optimized
    public static final String getCroppedImageUrlOptimized(String originalUrl) {
        String croppedUrl;
        int lastSlashIndex = originalUrl.lastIndexOf("/");
        if (lastSlashIndex != -1) {
            croppedUrl = getCroppedString(lastSlashIndex, originalUrl);
        } else {
            croppedUrl = originalUrl;
        }
        return croppedUrl;
    }

    private static String getCroppedString(int lastSlashIndex, String originalUrl) {
        String tempString;
        StringBuilder sb = new StringBuilder();
        tempString = originalUrl.substring(0, lastSlashIndex + 1);
        sb.append(tempString);
        sb.append("crop_");
        tempString = originalUrl.substring(lastSlashIndex + 1, originalUrl.length());
        sb.append(tempString);
        return sb.toString();
    }

    /** why is notifyDataSetChanged() called?*/
    /*public RecyclerPostsAdapter(Context context, List<ShortPost> postList) {
        mContext = context;
        mPostList = postList;
        notifyDataSetChanged();
        configImageLoader();
    }*/

    /** Why is this so long??? code style?
     * just create methods to update view elements in the view holder's class...*/
    /*@Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bind(position);

        final ShortUser user = mUsers.get(position);

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ImageLoader imageLoader = ImageLoader.getInstance();
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        imageLoader.displayImage(user.getIconUrl(), holder.profileImageView, options);

        holder.usernameTextView.setText(user.getUserName());
        if (user.getFullName() != null) {
            holder.fullnameTextView.setVisibility(View.VISIBLE);
            holder.fullnameTextView.setText(user.getFullName());
        } else {
            holder.fullnameTextView.setVisibility(View.GONE);
        }
        if (!SPManager.loadUserLoginData(mContext, Constants.PARAM_USER_ID).equals(user.getId())) {
            holder.followButton.setVisibility(View.VISIBLE);
            if (user.getFollowedByMe()) {
                holder.followButton.setBackground(mContext.getDrawable(R.drawable.following_button_bg));
                holder.followButton.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                holder.followButton.setText("following");
            } else {
                holder.followButton.setBackground(mContext.getDrawable(R.drawable.follow_button));
                holder.followButton.setTextColor(mContext.getResources().getColor(R.color.colorGrayText));
                holder.followButton.setText("follow");
            }

            holder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnUserClickListener.onFollowClick(user.getId(), user.getFollowedByMe(), position);
                    Log.d(TAG, "onClick: " + user.getId());
                }
            });
        } else {
            holder.followButton.setVisibility(View.INVISIBLE);
        }

        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnUserClickListener.onUserClick(user.getId(), position);
            }
        });

        holder.userInfoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnUserClickListener.onUserClick(user.getId(), position);
            }
        });

    }*/


    /** If butterknife is imported, then what is this? */
    /*dependencies {
        ...
        compile 'com.jakewharton:butterknife:7.0.1'
        ...
    */

    /** And then: */
    /*public ViewHolder(View itemView) {
        super(itemView);
        mProfileImageView = (ImageView) itemView.findViewById(R.id.iv_profile_image);
        mUsernameTextView = (TextView) itemView.findViewById(R.id.tv_username);
        mPhotoPostImageView = (SquareImageView) itemView.findViewById(R.id.iv_photo_post);
        mLikesTextView = (TextView) itemView.findViewById(R.id.tv_count_likes);
        mCommentsTextView = (TextView) itemView.findViewById(R.id.tv_count_comments);
        mLikePostButton = (Button) itemView.findViewById(R.id.btn_like_post);
        mCommentPostButton = (ImageButton) itemView.findViewById(R.id.btn_comment_post);
        mTimeTextView = (TextView) itemView.findViewById(R.id.tv_time);
        mDescriptionTextView = (TextView) itemView.findViewById(R.id.tv_description);
        mDoubleTapLikeView = (DoubleTapLikeView) itemView.findViewById(R.id.double_tap_like);
        mMoreButton = (ImageButton) itemView.findViewById(R.id.btn_more);
        mVideoPostView = (TextureExoVideoView) itemView.findViewById(R.id.vv_video_post);
        volumeButton = (ImageButton) itemView.findViewById(R.id.btn_volume);
        videoTimeTextView = (TextView) itemView.findViewById(R.id.tv_video_time);
        videoProgressBar = (ProgressBar) itemView.findViewById(R.id.pb_video);
    }*/

    /*
    holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnUserClickListener.onUserClick(user.getId(), position);
            }
        });
     */

}