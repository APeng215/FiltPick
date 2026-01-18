package com.apeng.filtpick.test;

import com.google.common.base.CaseFormat;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class TestFunctionCollector {

    /**
     * TestFunctionEntry includes resource location and function
     * @param resourceLocation
     * @param function
     */
    public record TestFunctionEntry(
            Identifier resourceLocation,
            Consumer<GameTestHelper> function
    ) {}

    public static List<TestFunctionEntry> collect(String modId, Class<?> testClass) {
        List<TestFunctionEntry> result = new ArrayList<>();

        for (Method method : testClass.getDeclaredMethods()) {

            if (method.isSynthetic()) continue;
            if (!Modifier.isStatic(method.getModifiers())) continue;
            if (method.getParameterCount() != 1) continue;
            if (method.getParameterTypes()[0] != GameTestHelper.class) continue;

            String name = CaseFormat.LOWER_CAMEL.to(
                    CaseFormat.LOWER_UNDERSCORE,
                    method.getName()
            );

            Identifier id = Identifier.fromNamespaceAndPath(modId, name);

            Consumer<GameTestHelper> consumer = helper -> {
                try {
                    method.invoke(null, helper);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getTargetException();
                    if (cause instanceof RuntimeException runtime) {
                        throw runtime;
                    }
                    throw new RuntimeException("GameTest method threw an exception: " + method, cause);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access GameTest method: " + method, e);
                }
            };

            result.add(new TestFunctionEntry(id, consumer));
        }

        return result;
    }

    private TestFunctionCollector() {}
}
