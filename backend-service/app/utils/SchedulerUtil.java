/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package utils;

import actors.ActorRegistry;
import akka.actor.Cancellable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import play.Logger;
import play.Play;
import scala.concurrent.duration.Duration;


/**
 * Created by zechen on 9/3/15.
 */
public class SchedulerUtil {

  public static Cancellable schedulerRef;

  public static synchronized void start() {
    start(Play.application().configuration().getLong("scheduler.check.interval", 10L));
  }

  /**
   * Start system's scheduler
   * @param mins
   */
  public static synchronized void start(Long mins) {
    if (schedulerRef != null) {
      schedulerRef.cancel();
    }

    schedulerRef = ActorRegistry.scheduler
      .schedule(Duration.create(0, TimeUnit.MILLISECONDS), Duration.create(mins, TimeUnit.MINUTES),
        ActorRegistry.schedulerActor, "checking", ActorRegistry.dispatcher, null);
  }

  @Nonnull
  public static Set<Integer> getJobIdsFromConfig(@Nonnull String configKey) {
    String jobIdsConf = Play.application().configuration().getString(configKey, "");
    if (jobIdsConf.length() > 0) {
      try {
        return Arrays.stream(jobIdsConf.split("\\s*,\\s*")).mapToInt(Integer::parseInt).boxed()
            .collect(Collectors.toSet());
      } catch (NumberFormatException e) {
        Logger.error(configKey + " must be set to a comma-separated list of integers in the config file");
      }
    }
    return new HashSet<Integer>();
  }

  /**
   * Cancel system's scheduler
   */
  public static synchronized void cancel() {
    schedulerRef.cancel();
  }
}
