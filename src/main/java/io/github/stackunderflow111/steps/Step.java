package io.github.stackunderflow111.steps;

import io.github.stackunderflow111.ExecutionContext;

public interface Step {
  /**
   * Execute the step with the given execution context
   *
   * @param executionContext an object holding the container, the task and the classloader
   */
  void execute(ExecutionContext executionContext);
}
