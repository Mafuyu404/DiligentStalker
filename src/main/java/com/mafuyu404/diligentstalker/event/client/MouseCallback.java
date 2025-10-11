package com.mafuyu404.diligentstalker.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MouseCallback {

    /**
     * 鼠标按键事件
     */
    Event<MouseButtonListener> MOUSE_BUTTON_EVENT =
            EventFactory.createArrayBacked(MouseButtonListener.class, listeners -> (button, action, mouseX, mouseY, modifiers) -> {
                boolean cancel = false;
                for (MouseButtonListener l : listeners) {
                    cancel |= l.onMouseButton(button, action, mouseX, mouseY, modifiers);
                }
                return cancel;
            });

    /**
     * 鼠标滚轮事件
     */
    Event<MouseScrollListener> MOUSE_SCROLL_EVENT =
            EventFactory.createArrayBacked(MouseScrollListener.class, listeners -> (scrollDelta, mouseX, mouseY, leftDown, middleDown, rightDown) -> {
                boolean cancel = false;
                for (MouseScrollListener l : listeners) {
                    cancel |= l.onMouseScroll(scrollDelta, mouseX, mouseY, leftDown, middleDown, rightDown);
                }
                return cancel;
            });

    @FunctionalInterface
    interface MouseButtonListener {
        /**
         * @param button    鼠标按键 (GLFW constants)
         * @param action    按下或释放 (GLFW_PRESS / GLFW_RELEASE)
         * @param mouseX    当前鼠标 X
         * @param mouseY    当前鼠标 Y
         * @param modifiers 修饰键 (shift, ctrl...)
         * @return true 取消后续处理
         */
        boolean onMouseButton(int button, int action, double mouseX, double mouseY, int modifiers);
    }

    @FunctionalInterface
    interface MouseScrollListener {
        /**
         * Forge 完整参数版滚轮事件
         *
         * @param scrollDelta 滚轮变化量（Forge 的 getScrollDelta）
         * @param mouseX      当前鼠标 X 坐标
         * @param mouseY      当前鼠标 Y 坐标
         * @param leftDown    左键是否按下
         * @param middleDown  中键是否按下
         * @param rightDown   右键是否按下
         * @return true 取消后续处理（相当于 Forge 的 cancel）
         */
        boolean onMouseScroll(double scrollDelta, double mouseX, double mouseY,
                              boolean leftDown, boolean middleDown, boolean rightDown);
    }
}
