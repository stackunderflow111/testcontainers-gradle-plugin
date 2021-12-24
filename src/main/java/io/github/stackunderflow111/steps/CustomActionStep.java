package io.github.stackunderflow111.steps;

import io.github.stackunderflow111.ExecutionContext;
import io.github.stackunderflow111.extenstion.config.CustomActionConfig;
import org.gradle.api.Action;

public class CustomActionStep implements Step {
  private final CustomActionConfig customActionConfig;

  public CustomActionStep(CustomActionConfig customActionConfig) {
    this.customActionConfig = customActionConfig;
  }

  @Override
  public void execute(ExecutionContext executionContext) {
    Action<? super ExecutionContext> customAction = customActionConfig.getCustomAction();
    customAction.execute(executionContext);
  }
}
