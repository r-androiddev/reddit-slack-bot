package io.dwak.redditslackbot.inject.module.config

sealed class ConfigValues {
  abstract val PATH: String

  object Application: ConfigValues() {
    override val PATH: String = "application"
    const val HOST_URL = "host_url"
  }

  object Firebase : ConfigValues() {
    override val PATH = "firebase"
    const val ACCOUNT_KEY_PATH = "account_key_path"
    const val PROJECT_ID = "project_id"
  }

  object Reddit : ConfigValues() {
    override val PATH = "reddit"
    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
  }

  object Slack : ConfigValues() {
    override val PATH = "slack"
    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
    const val VERIFICATION_TOKEN = "verification_token"
  }
}
