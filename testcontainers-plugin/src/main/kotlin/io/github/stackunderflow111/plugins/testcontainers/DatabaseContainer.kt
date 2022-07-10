package io.github.stackunderflow111.plugins.testcontainers

import org.apache.commons.lang3.reflect.ConstructorUtils
import org.gradle.api.InvalidUserDataException
import org.slf4j.LoggerFactory
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.utility.DockerImageName

class DatabaseContainer(imageName: String, containerClassName: String) {
  private val container: JdbcDatabaseContainer<*> =
      constructContainer(findContainerClass(containerClassName), imageName)

  val jdbcUrl: String
    get() = container.jdbcUrl
  val username: String
    get() = container.username
  val password: String
    get() = container.password

  fun start() {
    logger.info("Starting a container with image '{}'", container.dockerImageName)
    container.start()
    logger.info("Container started successfully with URL '{}'", container.jdbcUrl)
  }

  fun stop() = container.stop()

  companion object {
    private val logger = LoggerFactory.getLogger(DatabaseContainer::class.java)
  }
}

private fun findContainerClass(containerClassName: String): Class<out JdbcDatabaseContainer<*>> =
    try {
      Class.forName(containerClassName).asSubclass(JdbcDatabaseContainer::class.java)
          as Class<out JdbcDatabaseContainer<*>>
    } catch (e: ClassNotFoundException) {
      throw InvalidUserDataException("Could find the class $containerClassName", e)
    }

private fun constructContainer(
    clazz: Class<out JdbcDatabaseContainer<*>>,
    imageName: String
): JdbcDatabaseContainer<*> =
    try {
      ConstructorUtils.invokeConstructor(clazz, DockerImageName.parse(imageName))
    } catch (e: Exception) {
      throw InvalidUserDataException(
          "Could not create a new instance with for class ${clazz.name} with image $imageName", e)
    }
