package glsf

case class SlackConfig(clientId: String,
                       clientSecret: String,
                       signInRedirectUri: String,
                       addRedirectUri: String,
                       accessUrl: String)
