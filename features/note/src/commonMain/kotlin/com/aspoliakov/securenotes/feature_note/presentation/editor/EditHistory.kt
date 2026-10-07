package com.aspoliakov.securenotes.feature_note.presentation.editor

/**
 * Project SecureNotes
 *
 * Linear undo/redo history of values. Knows nothing about what is edited: callers decide
 * when a change starts a new step ([push]) and when it extends the current one ([replaceCurrent]).
 */

internal class EditHistory<T>(
        initial: T,
        private val maxSteps: Int,
) {

    private val undoStack = ArrayDeque<T>()
    private val redoStack = ArrayDeque<T>()

    var current: T = initial
        private set

    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    /** Makes [value] a new step on top of the current one and drops the steps that could be redone. */
    fun push(value: T) {
        undoStack.addLast(current)
        if (undoStack.size > maxSteps) undoStack.removeFirst()
        current = value
        redoStack.clear()
    }

    /** Replaces the current step, e.g. to extend it with continued typing or to remember a moved cursor. */
    fun replaceCurrent(value: T) {
        current = value
    }

    /** Steps back and returns the value to restore, or null if there is nothing to undo. */
    fun undo(): T? {
        val previous = undoStack.removeLastOrNull() ?: return null
        redoStack.addLast(current)
        current = previous
        return previous
    }

    /** Steps forward and returns the value to restore, or null if there is nothing to redo. */
    fun redo(): T? {
        val next = redoStack.removeLastOrNull() ?: return null
        undoStack.addLast(current)
        current = next
        return next
    }
}
