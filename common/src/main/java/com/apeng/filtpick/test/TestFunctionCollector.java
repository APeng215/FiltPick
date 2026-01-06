package com.apeng.filtpick.test;

import com.google.common.base.CaseFormat;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class TestFunctionCollector {

    /**
     * TestFunctionEntry includes resource location and function
     * @param id
     * @param function
     */
    public record TestFunctionEntry(
            ResourceLocation id,
            Consumer<GameTestHelper> function
    ) {}

    public static List<TestFunctionEntry> collect(String modId, Class<?> testClass) {
        List<TestFunctionEntry> result = new ArrayList<>();

        for (Method method : testClass.getDeclaredMethods()) {

            if (!Modifier.isStatic(method.getModifiers())) continue;
            if (method.getParameterCount() != 1) continue;
            if (method.getParameterTypes()[0] != GameTestHelper.class) continue;

            String name = CaseFormat.LOWER_CAMEL.to(
                    CaseFormat.LOWER_UNDERSCORE,
                    method.getName()
            );

            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);

            Consumer<GameTestHelper> consumer = helper -> {
                try {
                    method.invoke(null, helper);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(
                            "Cannot invoke GameTest method: " + method, e
                    );
                }
            };

            result.add(new TestFunctionEntry(id, consumer));
        }

        return result;
    }

    private TestFunctionCollector() {}
}
