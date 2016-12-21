

package dheeraj.com.trafficsolution;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dheeraj.com.trafficsolution.Models.Person;
import dheeraj.com.trafficsolution.Models.Post;


public class UserDetailActivity extends AppCompatActivity {
    private final String TAG = "UserDetailActivity";
    public static final String USER_ID_EXTRA_NAME = "user_name";
    private RecyclerView mRecyclerGrid;
    private GridAdapter mGridAdapter;
    private ValueEventListener mFollowingListener;
    private ValueEventListener mPersonInfoListener;
    private String mUserId;
    private DatabaseReference mPeopleRef;
    private DatabaseReference mPersonRef;
    private static final int GRID_NUM_COLUMNS = 4;
    private DatabaseReference mFollowersRef;
    private ValueEventListener mFollowersListener;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra(USER_ID_EXTRA_NAME);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // TODO: Investigate why initial toolbar title is activity name instead of blank.

        mPeopleRef = FirebaseUtil.getPeopleRef();
        final String currentUserId = FirebaseUtil.getCurrentUserId();

        final FloatingActionButton followUserFab = (FloatingActionButton) findViewById(R.id
                .follow_user_fab);
        mFollowingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followUserFab.setImageDrawable(ContextCompat.getDrawable(
                            UserDetailActivity.this, R.drawable.ic_done_24dp));
                } else {
                    followUserFab.setImageDrawable(ContextCompat.getDrawable(
                            UserDetailActivity.this, R.drawable.ic_person_add_24dp));
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };
        if (currentUserId != null) {
            mPeopleRef.child(currentUserId).child("following").child(mUserId)
                    .addValueEventListener(mFollowingListener);
        }
        followUserFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUserId == null) {
                    Toast.makeText(UserDetailActivity.this, "You need to sign in to follow someone.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: Convert these to actually not be single value, for live updating when
                // current user follows.
                mPeopleRef.child(currentUserId).child("following").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> updatedUserData = new HashMap<>();
                        if (dataSnapshot.exists()) {
                            // Already following, need to unfollow
                            updatedUserData.put("people/" + currentUserId + "/following/" + mUserId, null);
                            updatedUserData.put("followers/" + mUserId + "/" + currentUserId, null);
                        } else {
                            updatedUserData.put("people/" + currentUserId + "/following/" + mUserId, true);
                            updatedUserData.put("followers/" + mUserId + "/" + currentUserId, true);
                        }
                        FirebaseUtil.getBaseRef().updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                                if (firebaseError != null) {
                                    Toast.makeText(UserDetailActivity.this, R.string
                                            .follow_user_error, Toast.LENGTH_LONG).show();
                                    Log.d(TAG, getString(R.string.follow_user_error) + "\n" +
                                            firebaseError.getMessage());
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
            }
        });

        mRecyclerGrid = (RecyclerView) findViewById(R.id.user_posts_grid);
        mGridAdapter = new GridAdapter();
        mRecyclerGrid.setAdapter(mGridAdapter);
        mRecyclerGrid.setLayoutManager(new GridLayoutManager(this, GRID_NUM_COLUMNS));

        mPersonRef = FirebaseUtil.getPeopleRef().child(mUserId);
        mPersonInfoListener = mPersonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Person person = dataSnapshot.getValue(Person.class);
                final CircularImageView userPhoto = (CircularImageView) findViewById(R.id.user_detail_photo);

               name = person.getFull_name();
                if (name == null) {
                    name = getString(R.string.user_info_no_name);
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("people").child(mUserId);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name = String.valueOf(dataSnapshot.child("displayNames").getValue());
                            GlideUtil.loadProfileIcon(String.valueOf(dataSnapshot.child("photoUrls").getValue()), userPhoto);
                            Log.e("Final name", name);
                            collapsingToolbar.setTitle(name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
                if (person.getFollowing() != null) {
                    int numFollowing = person.getFollowing().size();
                    ((TextView) findViewById(R.id.user_num_following))
                            .setText(numFollowing + " following");
                }
                List<String> paths = new ArrayList<String>(person.getPosts().keySet());
                mGridAdapter.addPaths(paths);
                String firstPostKey = paths.get(0);

                FirebaseUtil.getPostsRef().child(firstPostKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);

                        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
                        GlideUtil.loadImage(post.getFull_url(), imageView);
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
        mFollowersRef = FirebaseUtil.getFollowersRef().child(mUserId);
        mFollowersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numFollowers = dataSnapshot.getChildrenCount();
                ((TextView) findViewById(R.id.user_num_followers))
                        .setText(numFollowers + " follower" + (numFollowers == 1 ? "" : "s"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mFollowersRef.addValueEventListener(mFollowersListener);





    }

    @Override
    protected void onDestroy() {
        if (FirebaseUtil.getCurrentUserId() != null) {
            mPeopleRef.child(FirebaseUtil.getCurrentUserId()).child("following").child(mUserId)
                    .removeEventListener(mFollowingListener);
        }

        if (getApplicationContext() == null || ((UserDetailActivity.this)).isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && UserDetailActivity.this.isDestroyed())){

        }
        mPersonRef.child(mUserId).removeEventListener(mPersonInfoListener);
        mFollowersRef.removeEventListener(mFollowersListener);
        super.onDestroy();
    }

    class GridAdapter extends RecyclerView.Adapter<GridImageHolder> {
        private List<String> mPostPaths;

        public GridAdapter() {
            mPostPaths = new ArrayList<String>();
        }

        @Override
        public GridImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(UserDetailActivity.this);
            int tileDimPx = getPixelsFromDps(100);
            imageView.setLayoutParams(new GridView.LayoutParams(tileDimPx, tileDimPx));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);

            return new GridImageHolder(imageView);
        }

        @Override
        public void onBindViewHolder(final GridImageHolder holder, int position) {
            DatabaseReference ref = FirebaseUtil.getPostsRef().child(mPostPaths.get(position));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    GlideUtil.loadImage(post.getFull_url(), holder.imageView);
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: Implement go to post view.
                            Toast.makeText(UserDetailActivity.this, "Selected: " + holder
                                            .getAdapterPosition(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e(TAG, "Unable to load grid image: " + firebaseError.getMessage());
                }
            });
        }

        public void addPath(String path) {
            mPostPaths.add(path);
            notifyItemInserted(mPostPaths.size());
        }

        public void addPaths(List<String> paths) {
            int startIndex = mPostPaths.size();
            mPostPaths.addAll(paths);
            notifyItemRangeInserted(startIndex, mPostPaths.size());
        }

        @Override
        public int getItemCount() {
            return mPostPaths.size();
        }

        private int getPixelsFromDps(int dps) {
            final float scale = UserDetailActivity.this.getResources().getDisplayMetrics().density;
            return (int) (dps * scale + 0.5f);
        }
    }

    private class GridImageHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public GridImageHolder(ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
