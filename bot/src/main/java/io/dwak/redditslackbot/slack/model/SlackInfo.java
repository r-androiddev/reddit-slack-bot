package io.dwak.redditslackbot.slack.model;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;
import io.dwak.redditslackbot.slack.network.SlackWebhookUrlComponentAdapter;
import me.mattlogan.auto.value.firebase.adapter.FirebaseAdapter;
import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;

@AutoValue
@FirebaseValue
public abstract class SlackInfo {
    public abstract String teamId();
    public abstract String teamName();
    public abstract String channel();
    public abstract String channelId();
    @FirebaseAdapter(SlackWebhookUrlComponentAdapter.class)
    public abstract SlackWebhookUrlComponents webHookUrl();
    public abstract String accessToken();

    public static Builder builder() {
        return new AutoValue_SlackInfo.Builder();
    }

    public static SlackInfo create(DataSnapshot dataSnapshot){
        return dataSnapshot.getValue(AutoValue_SlackInfo.FirebaseValue.class).toAutoValue();
    }

    public Object toFirebaseValue() {
        return new AutoValue_SlackInfo.FirebaseValue(this);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder teamName(String teamName);
        public abstract Builder teamId(String teamId);
        public abstract Builder channel(String channel);
        public abstract Builder channelId(String channelId);
        public abstract Builder webHookUrl(SlackWebhookUrlComponents webHookurl);
        public abstract Builder accessToken(String accessToken);
        public abstract SlackInfo build();
    }
}
