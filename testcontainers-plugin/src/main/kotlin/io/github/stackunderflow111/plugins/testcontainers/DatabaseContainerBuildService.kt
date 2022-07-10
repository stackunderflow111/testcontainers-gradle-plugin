package io.github.stackunderflow111.plugins.testcontainers

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class DatabaseContainerBuildService :
    BuildService<DatabaseContainerBuildService.Parameters>, AutoCloseable {
  interface Parameters : BuildServiceParameters {
    val imageName: Property<String>
    val containerClass: Property<String>
  }

  private val databaseContainer: DatabaseContainer

  init {
    val imageName = parameters.imageName.get()
    val containerClassName = parameters.containerClass.get()
    databaseContainer =
        DatabaseContainer(imageName = imageName, containerClassName = containerClassName)
    databaseContainer.start()
  }

  override fun close() {
    databaseContainer.stop()
  }

  val jdbcUrl: String
    get() = databaseContainer.jdbcUrl
  val username: String
    get() = databaseContainer.username
  val password: String
    get() = databaseContainer.password
}
