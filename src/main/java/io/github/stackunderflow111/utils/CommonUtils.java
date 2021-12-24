package io.github.stackunderflow111.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.gradle.api.GradleException;

public class CommonUtils {
  public static URL fileToURL(File file) {
    try {
      return file.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new GradleException("Internal error, file " + file + " cannot be converted to URL", e);
    }
  }
}
