package org.sputnik.context;

import org.springframework.context.SmartLifecycle;

public abstract class SmartLifecycleBean implements SmartLifecycle {
    private boolean running = false;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        doStop();
        running = false;
        callback.run();
    }

    @Override
    public void start() {
        doStart();
        running = true;
    }

    @Override
    public void stop() {
        doStop();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    protected abstract void doStart();

    protected abstract void doStop();
}
