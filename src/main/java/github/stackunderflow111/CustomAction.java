package github.stackunderflow111;

import org.gradle.api.HasImplicitReceiver;

@HasImplicitReceiver
public interface CustomAction<T, S> {
    void execute(T t, S s);
}