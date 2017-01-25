#Reddit Moderator Slack Bot

This slackbot can help in basic moderation of reddit subreddits. 
The backend is hosted on firebase and no sensitive information is stored. 
The bot can also be self deployed to a local team slack.

##Requirements
1. Slack Team
2. Subreddit in which you are a mod
3. Reddit "Bot" user that also moderates the subreddit

##Setup
1. Proceed to http://dwak.io/reddit-slack-bot/ and press the Add To Slack button
2. Authorize the slack bot with your team and a channel
3. You'll now be forwarded to the reddit authorization, make sure you use you *BOT ACCOUNT* for this
4. You'll now be forwarded to a subreddit form, this is used to customize parts of the bot, it's currently required
5. You're done with setup

##Features

1. 5 minute polling of your subreddit and posting into the channel of your choice
2. Customizable ruleset for removals (more on this in a bit)

##Rules
The following commands are available for setting up rules and canned responses

`/add-rule "rule_id" "rule_title" "rule_message"`

* `rule_id` is a unique id for storage
* `rule_title` is used for the interactive button text, preferred to be unique
* `rule_message` this is used by the reddit bot as it's comment on a removed post

`/get-rules`

Returns rules stored in the db 

`/remove-rule "rule_id"`

Removes the rule with the given id