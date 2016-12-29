package io.dwak.redditslackbot.inject.module.config

sealed class ConfigValues {
  abstract val PATH: String

  object Firebase : ConfigValues() {
    override val PATH = "firebase"
    const val ACCOUNT_KEY_PATH = "account_key_path"
    const val PROJECT_ID = "project_id"
  }

  object Slack : ConfigValues() {
    override val PATH = "slack"
    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
    const val VERIFICATION_TOKEN = "verification_token"
  }
}
