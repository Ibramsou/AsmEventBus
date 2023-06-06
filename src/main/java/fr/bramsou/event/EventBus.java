package fr.bramsou.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {

    private final EventPostGenerator postGenerator = new EventPostGenerator();
    private final Map<Class<?>, List<EventPost>> executorMap;
    private final Map<Object, List<EventPost>> handlerPosts = new HashMap<>();

    public EventBus() {
        this(false);
    }

    public EventBus(boolean async) {
        this.executorMap = async ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    public void register(Object handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Subscriber.class) == null) continue;
            if (method.getParameterCount() != 1) continue;
            Class<?> eventClass = method.getParameterTypes()[0];
            
            EventPost post = this.postGenerator.generate(handler, method, eventClass);
            this.executorMap.computeIfAbsent(eventClass, clazz -> new ArrayList<>()).add(post);
            this.handlerPosts.computeIfAbsent(handler, h -> new ArrayList<>()).add(post);
        }
    }

    public void unregister(Object handler) {
        List<EventPost> posts = this.handlerPosts.remove(handler);
        if (posts == null) return;
        for (List<EventPost> value : this.executorMap.values()) {
            value.removeAll(posts);
        }
    }

    public void post(Object event) {
        List<EventPost> posts = this.executorMap.get(event.getClass());
        if (posts == null) return;
        for (EventPost post : posts) {
            post.post(event);
        }
    }
}
