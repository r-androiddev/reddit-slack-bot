package io.dwak.redditslackbot.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;
import io.dwak.redditslackbot.database.firebase.InstantAdapter;
import me.mattlogan.auto.value.firebase.adapter.FirebaseAdapter;
import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@AutoValue
@FirebaseValue
public abstract class RedditInfo {
    @Nullable
    public abstract String subreddit();
    public abstract String accessToken();
    public abstract String refreshToken();
    public abstract Long expiresIn();
    @FirebaseAdapter(InstantAdapter.class)
    public abstract Instant lastTokenRefresh();
    public abstract String scope();
    public abstract String tokenType();

    public abstract RedditInfo withSubreddit(String subreddit);

    public static Builder builder() {
        return new AutoValue_RedditInfo.Builder();
    }

    public Builder toBuilder() {
        return new AutoValue_RedditInfo.Builder(this);
    }

    public static RedditInfo create(DataSnapshot dataSnapshot){
        return dataSnapshot.getValue(AutoValue_RedditInfo.FirebaseValue.class).toAutoValue();
    }

    public Object toFirebaseValue() {
        return new AutoValue_RedditInfo.FirebaseValue(this);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder subreddit(String subreddit);
        public abstract Builder accessToken(String accessToken);
        public abstract Builder refreshToken(String refreshToken);
        public abstract Builder expiresIn(Long expiresIn);
        public abstract Builder lastTokenRefresh(Instant lastTokenRefresh);
        public abstract Builder scope(String scope);
        public abstract Builder tokenType(String tokenType);
        public abstract RedditInfo build();
    }
}
