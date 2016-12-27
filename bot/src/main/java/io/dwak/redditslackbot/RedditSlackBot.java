package io.dwak.redditslackbot;

import com.spotify.apollo.httpservice.LoadingException;
import io.dwak.redditslackbot.inject.component.DaggerMainComponent;
import io.dwak.redditslackbot.inject.component.MainComponent;

public class RedditSlackBot {
    public static final boolean DEBUG = true;
    public static void main(String[] args) throws LoadingException{
        MainComponent mainComponent = DaggerMainComponent.create();
//        HttpService.boot(mainComponent.bot(), "reddit-slack-bot", args);

        if(DEBUG){
            System.out.println(mainComponent.redditConfig());
            System.out.println(mainComponent.slackConfig());
            System.out.println(mainComponent.firebaseConfig());
        }
    }
}
