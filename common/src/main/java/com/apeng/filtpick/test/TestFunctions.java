package com.apeng.filtpick.test;

import net.minecraft.gametest.framework.GameTestHelper;

/**
 * All the static functions here that consume only GameTestHelper will be registered. The resource location will be the
 * snake case of the original function name. For example, exampleTest -> example_test.
 */
public class TestFunctions {

    public static void exampleTest(GameTestHelper helper) {
        helper.succeed();
    }
}