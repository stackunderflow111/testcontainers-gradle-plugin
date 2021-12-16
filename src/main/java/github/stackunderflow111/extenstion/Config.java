package github.stackunderflow111.extenstion;

import github.stackunderflow111.steps.Step;

/** Marker interface for all config classes. */
public interface Config {
  /** create the corresponding step */
  Step createStep();
}
