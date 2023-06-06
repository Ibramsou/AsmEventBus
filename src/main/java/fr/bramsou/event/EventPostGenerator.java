package fr.bramsou.event;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EventPostGenerator {

    private static Integer OP_CODE;
    private static final AtomicInteger GENERATED_IDS = new AtomicInteger();
    private static final EventPostDefiner DEFINER = new EventPostDefiner();

    static {
        String javaVersion = System.getProperty("java.version");
        int majorVersion = Integer.parseInt(javaVersion.split("\\.")[0]);
        if (majorVersion == 8) {
            OP_CODE = Opcodes.V1_8;
        } else {
            String codeId = String.format("V_%s", majorVersion);
            for (Field field : Opcodes.class.getFields()) {
                if (field.getType() == Integer.class && field.getName().equals(codeId)) {
                    try {
                        OP_CODE = (int) field.get(null);
                    } catch (IllegalAccessException ignored) {}
                }
            }
        }

        if (OP_CODE == null) {
            OP_CODE = Opcodes.V1_8;
        }
    }

    public EventPost generate(Object handler, Method method, Class<?> event) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String generatedName = this.generateName();
        String parsedPostName = this.parseClassName(generatedName);
        writer.visit(OP_CODE, Opcodes.ACC_PUBLIC, parsedPostName, null, "java/lang/Object", new String[]{this.parseClassName(EventPost.class.getName())});

        String handlerClass = this.parseClassName(handler.getClass().getName());

        writer.visitField(Opcodes.ACC_PRIVATE, "handler", "L" + handlerClass + ";", null, null)
                .visitEnd();

        MethodVisitor constructor = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(L" + handlerClass + ";)V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitVarInsn(Opcodes.ALOAD, 1);
        constructor.visitFieldInsn(Opcodes.PUTFIELD, parsedPostName, "handler", "L" + handlerClass + ";");
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(2, 2);
        constructor.visitEnd();

        String eventClass = this.parseClassName(event.getName());

        MethodVisitor executeMv = writer.visitMethod(Opcodes.ACC_PUBLIC, "post", "(Ljava/lang/Object;)V", null, null);
        executeMv.visitCode();
        executeMv.visitVarInsn(Opcodes.ALOAD, 0);
        executeMv.visitFieldInsn(Opcodes.GETFIELD, parsedPostName, "handler", "L" + handlerClass + ";");
        executeMv.visitVarInsn(Opcodes.ALOAD, 1);
        executeMv.visitTypeInsn(Opcodes.CHECKCAST, eventClass);
        executeMv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, handlerClass, method.getName(), "(L" + eventClass + ";)V", false);
        executeMv.visitInsn(Opcodes.RETURN);
        executeMv.visitMaxs(2, 2);
        executeMv.visitEnd();

        writer.visitEnd();

        Class<?> generated = DEFINER.defineClass(generatedName, writer.toByteArray());
        try {
            return generated.asSubclass(EventPost.class).getDeclaredConstructor(handler.getClass()).newInstance(handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String parseClassName(String name) {
        return name.replace(".", "/");
    }

    private String generateName() {
        return "fr.bramsou.event.generated.GeneratedEventPost" + GENERATED_IDS.incrementAndGet();
    }
}
