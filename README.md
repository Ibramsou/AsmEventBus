# AsmEventBus
Like event bus of guava, AsmEventBus is a lightweight library that permit you to post events.

The only different is that Reflection isn't used to post an Event.

Unlike guava, AsmEventBus generate an implementation of "EventPost" with ASM and copy the registered method in that implementation.

And when we need to post the event, we simply call EventPost#post(Object) instead of Method#invoke() from reflection

# How to use ?
```java
// Create a new instance of Event Bus
public class Main {
  public static void main(String[] args) {
    EventBus bus = new EventBus();
    // Register your event listener handler
    bus.register(new EventListener());
    // Post your events
    bus.post(new MessageEvent("Hello world !"));
  }
}

// Create an event listener handler
public class EventListener {
  @Subscribe
  public void onEvent(MessageEvent event) {
    System.out.println(event.getMessage());
  }
  
  @Subscribe(priority = EventPriority.HIGH)
  public void onFirstPost(MessageEvent event) {
    event.setMessage("[Here is my prefix] " + event.getMessage());
  }
}

// Create an event
public class MessageEvent {
  private String message;
  
  public MessageEvent(String message) {
    this.message = message;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
}
```
