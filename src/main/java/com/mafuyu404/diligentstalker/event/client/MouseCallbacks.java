package com.mafuyu404.diligentstalker.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MouseCallbacks {
    Event<MouseButtonListener> MOUSE_BUTTON_EVENT =
            EventFactory.createArrayBacked(MouseButtonListener.class, listeners -> (button, action) -> {
                boolean cancel = false;
                for (MouseButtonListener l : listeners) {
                    cancel |= l.onMouseButton(button, action);
                }
                return cancel;
            });

    Event<MouseScrollListener> MOUSE_SCROLL_EVENT =
            EventFactory.createArrayBacked(MouseScrollListener.class, listeners -> (xOffset, yOffset) -> {
                boolean cancel = false;
                for (MouseScrollListener l : listeners) {
                    cancel |= l.onMouseScroll(xOffset, yOffset);
                }
                return cancel;
            });

    @FunctionalInterface
    interface MouseButtonListener {
        boolean onMouseButton(int button, int action);
    }

    @FunctionalInterface
    interface MouseScrollListener {
        boolean onMouseScroll(double xOffset, double yOffset);
    }
}