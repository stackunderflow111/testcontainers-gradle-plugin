package io.github.stackunderflow111.extension.config;

import io.github.stackunderflow111.ExecutionContext;
import io.github.stackunderflow111.steps.CustomActionStep;
import org.gradle.api.Action;

public class CustomActionConfig implements Config {
  private Action<? super ExecutionContext> customAction = context -> {};

  public Action<? super ExecutionContext> getCustomAction() {
    return customAction;
  }

  /**
   * Run a custom action for this step
   *
   * @param customAction the action to run. It takes the execution context as the argument
   */
  public void run(Action<? super ExecutionContext> customAction) {
    this.customAction = customAction;
  }

  @Override
  public CustomActionStep createStep() {
    return new CustomActionStep(this);
  }
}
