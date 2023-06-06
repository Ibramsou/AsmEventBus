package fr.bramsou.event;

import java.lang.reflect.Method;
import java.util.*;

public class EventBus {

    private final EventPostGenerator postGenerator = new EventPostGenerator();
    private final Map<Class<?>, List<EventExecutor>> executorMap = new HashMap<>();
    private final Map<Object, List<EventExecutor>> handlerPosts = new HashMap<>();

    /**
     * Register a new event listener handler
     * @param handler the handler
     */
    public void register(Object handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            Subscriber subscriber = method.getAnnotation(Subscriber.class);
            if (subscriber == null) continue;
            if (method.getParameterCount() != 1) continue;
            Class<?> eventClass = method.getParameterTypes()[0];
            
            EventPost post = this.postGenerator.generate(handler, method, eventClass);
            EventExecutor executor = new EventExecutor(subscriber.priority(), post);
            this.executorMap.computeIfAbsent(eventClass, clazz -> Collections.synchronizedList(new ArrayList<>())).add(executor);
            this.handlerPosts.computeIfAbsent(handler, h -> new ArrayList<>()).add(executor);
        }

        Comparator<EventExecutor> comparator = Comparator.comparingInt(value -> value.getPriority().ordinal());
        comparator = comparator.reversed();

        for (List<EventExecutor> list : this.executorMap.values()) {
            list.sort(comparator);
        }
    }

    /**
     * Unregister an handler
     * @param handler the handler
     */
    public void unregister(Object handler) {
        List<EventExecutor> posts = this.handlerPosts.remove(handler);
        if (posts == null) return;
        for (List<EventExecutor> value : this.executorMap.values()) {
            value.removeAll(posts);
        }
    }

    /**
     * Post an event
     * @param event event object
     */
    public void post(Object event) {
        List<EventExecutor> posts = this.executorMap.get(event.getClass());
        if (posts == null) return;
        for (EventExecutor post : posts) {
            post.execute(event);
        }
    }
}
