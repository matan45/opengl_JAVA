package app.renderer.terrain.sculpting;

import java.util.ArrayDeque;
import java.util.Deque;

public class TerrainActionManager {
    private final Deque<TerrainEditAction> undoStack;
    private final Deque<TerrainEditAction> redoStack;
    private static final int MAX_UNDO_ACTIONS = 100;
    private static final long MAX_MEMORY_BYTES = 10 * 1024 * 1024;

    public TerrainActionManager() {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    public void addAction(TerrainEditAction action) {
        while (getTotalMemoryUsage() > MAX_MEMORY_BYTES || undoStack.size() >= MAX_UNDO_ACTIONS) {
            if (!undoStack.isEmpty()) {
                undoStack.removeLast();
            } else {
                break;
            }
        }

        undoStack.addFirst(action);
        redoStack.clear();
    }

    public TerrainEditAction undo() {
        if (!undoStack.isEmpty()) {
            TerrainEditAction action = undoStack.removeFirst();
            redoStack.addFirst(action);
            return action;
        }
        return null;
    }

    public TerrainEditAction redo() {
        if (!redoStack.isEmpty()) {
            TerrainEditAction action = redoStack.removeFirst();
            undoStack.addFirst(action);
            return action;
        }
        return null;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    public int getUndoCount() {
        return undoStack.size();
    }

    public int getRedoCount() {
        return redoStack.size();
    }

    private long getTotalMemoryUsage() {
        long total = 0;
        for (TerrainEditAction action : undoStack) {
            total += action.getMemoryFootprint();
        }
        for (TerrainEditAction action : redoStack) {
            total += action.getMemoryFootprint();
        }
        return total;
    }

    public long getMemoryUsageMB() {
        return getTotalMemoryUsage() / (1024 * 1024);
    }
}