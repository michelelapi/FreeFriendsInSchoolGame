package org.example.bonuses;

import org.example.game.GameEngine;

/**
 * Base interface for bonuses that can be activated when a friend is rescued.
 * This allows for easy extension with new bonus types in the future.
 */
public interface Bonus {
    /**
     * Gets the name of the bonus
     */
    String getName();
    
    /**
     * Activates the bonus effect
     * @param gameEngine The game engine to apply the bonus to
     */
    void activate(GameEngine gameEngine);
    
    /**
     * Gets the priority of the bonus (higher priority bonuses are more powerful)
     * Principal = 3, Parent = 2, AnotherTeacher = 1
     */
    int getPriority();
}

