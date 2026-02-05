package com.example.player;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class VideoPlayerManager_Factory implements Factory<VideoPlayerManager> {
  private final Provider<Context> contextProvider;

  public VideoPlayerManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public VideoPlayerManager get() {
    return newInstance(contextProvider.get());
  }

  public static VideoPlayerManager_Factory create(Provider<Context> contextProvider) {
    return new VideoPlayerManager_Factory(contextProvider);
  }

  public static VideoPlayerManager newInstance(Context context) {
    return new VideoPlayerManager(context);
  }
}
