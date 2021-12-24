package io.github.stackunderflow111.extenstion.config;

import io.github.stackunderflow111.steps.Step;

/** Interface for all config classes. */
public interface Config {
  /** create the corresponding step */
  Step createStep();
}
