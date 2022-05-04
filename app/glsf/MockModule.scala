package glsf

import com.google.inject.AbstractModule

class MockModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[MockUserRepository])
    bind(classOf[TeamTokenRepository]).to(classOf[MockTeamTokenRepository])
    bind(classOf[DebugDataSaver]).to(classOf[MockDebugDataSaver])
  }
}
