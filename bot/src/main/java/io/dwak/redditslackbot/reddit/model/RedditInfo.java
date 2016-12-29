package io.dwak.redditslackbot.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;
import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;

@AutoValue
@FirebaseValue
public abstract class RedditInfo {
    public abstract String subreddit();
    public abstract String botUsername();
    public abstract String accessToken();
    public abstract Long expiresIn();
    public abstract String scope();
    public abstract String tokenType();

    public static Builder builder() {
        return new AutoValue_RedditInfo.Builder();
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
        public abstract Builder botUsername(String botUsername);
        public abstract Builder accessToken(String accessToken);
        public abstract Builder expiresIn(Long expiresIn);
        public abstract Builder scope(String scope);
        public abstract Builder tokenType(String tokenType);
        public abstract RedditInfo build();
    }
}
