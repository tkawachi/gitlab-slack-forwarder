package glsf

// Identifying Slack user by (teamId, userId) pair.
case class User(teamId: String, userId: String, mail: String)
