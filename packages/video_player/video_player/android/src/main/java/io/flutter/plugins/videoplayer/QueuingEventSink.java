// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.videoplayer;

import java.util.ArrayList;

import io.flutter.plugin.common.EventChannel;

/**
 * And implementation of {@link EventChannel.EventSink} which can wrap an underlying sink.
 *
 * <p>It delivers messages immediately when downstream is available, but it queues messages before
 * the delegate event sink is set with setDelegate.
 *
 * <p>This class is not thread-safe. All calls must be done on the same thread or synchronized
 * externally.
 */
final class QueuingEventSink implements EventChannel.EventSink {
  private EventChannel.EventSink delegate;
  private ArrayList<Object> eventQueue = new ArrayList<>();
  private boolean done = false;

  public void setDelegate(EventChannel.EventSink delegate) {
    this.delegate = delegate;
    maybeFlush();
  }

  @Override
  public void endOfStream() {
    enqueue(new EndOfStreamEvent());
    maybeFlush();
    done = true;
  }

  @Override
  public void error(String code, String message, Object details) {
    enqueue(new ErrorEvents(code, message, details));
    maybeFlush();
  }

  @Override
  public void success(Object event) {
    enqueue(event);
    maybeFlush();
  }

  private void enqueue(Object event) {
    if (done) {
      return;
    }
    eventQueue.add(event);
  }

  private void maybeFlush() {
    if (delegate == null) {
      return;
    }
    for (Object event : eventQueue) {
      if (event instanceof EndOfStreamEvent) {
        delegate.endOfStream();
      } else if (event instanceof ErrorEvents) {
        ErrorEvents errorEvent = (ErrorEvents) event;
        delegate.error(errorEvent.code, errorEvent.message, errorEvent.details);
      } else {
        delegate.success(event);
      }
    }
    eventQueue.clear();
  }

  private static class EndOfStreamEvent {}

  private static class ErrorEvents {
    String code;
    String message;
    Object details;

    ErrorEvents(String code, String message, Object details) {
      this.code = code;
      this.message = message;
      this.details = details;
    }
  }
}
