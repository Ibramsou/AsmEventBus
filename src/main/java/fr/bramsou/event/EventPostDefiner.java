package fr.bramsou.event;

public class EventPostDefiner extends ClassLoader {

    public Class<?> defineClass(String name, byte[] bytecode) {
        return defineClass(name, bytecode, 0, bytecode.length);
    }
}
