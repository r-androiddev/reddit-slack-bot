package io.dwak.redditslackbot.reddit.model

import com.squareup.moshi.Json

data class RedditFlairResponse(val choices : List<RedditFlairChoice>,
                               val currentFlair : RedditFlairChoice)

data class RedditFlairChoice(@Json(name = "flair_css_class") val flairCssClass : String,
                             @Json(name = "flair_position") val flairPosition : String,
                             @Json(name = "flair_template_id") val flairTemplateId : String,
                             @Json(name = "flair_text") val flairText : String,
                             @Json(name = "flair_text_editable") val flairTextEditable : Boolean)

