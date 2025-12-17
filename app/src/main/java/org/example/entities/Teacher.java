package org.example.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.example.game.DeadEndFillingPathfinder;
import org.example.game.SchoolLayout;
import org.example.game.WallAwarenessSystem;

import java.util.List;
import java.util.Random;

public class Teacher extends Entity {
    private static final float DEFAULT_SPEED = 120f; // pixels per second
    private static final float GUARD_RADIUS = 150f; // Radius around friend to patrol
    private static final float DETECTION_RADIUS = 250f; // Distance to detect player
    private static final float CHASE_SPEED_MULTIPLIER = 0.9f; // Speed multiplier when chasing

    private Paint paint;
    private Random random;
    private float directionChangeTimer = 0f;
    private float directionChangeInterval = 2f; // Change direction every 2 seconds
    private float currentDirection = 0f; // Angle in radians
    private float lastX, lastY;
    private int stuckCounter = 0;
    private float lastDistanceToTarget = Float.MAX_VALUE; // For stuck detection when going to target
    private int stuckAtTargetCounter = 0; // Counter for when stuck going to target

    // Stuck to wall detection and recovery
    private boolean isUnstucking = false; // Flag to indicate we're in random movement mode
    private float unstuckTimer = 0f; // Timer for random movement duration
    private static final float UNSTUCK_DURATION = 2f; // Try random movement for 2 seconds
    private int wallCollisionCounter = 0; // Counter for consecutive wall collisions
    private static final int WALL_COLLISION_THRESHOLD = 3; // Enter unstuck mode after 3 consecutive collisions
    private float lastSuccessfulMoveX = 0f; // Last position where we successfully moved
    private float lastSuccessfulMoveY = 0f;
    private float unstuckDirectionChangeTimer = 0f; // Timer for changing direction during unstuck
    private static final float UNSTUCK_DIRECTION_CHANGE_INTERVAL = 0.3f; // Change direction every 0.3 seconds

    // Guard behavior
    private Friend guardedFriend; // The friend this teacher is guarding
    private float guardCenterX, guardCenterY; // Center point to patrol around
    private float patrolAngle = 0f; // Current angle in patrol circle
    private float patrolRadius = GUARD_RADIUS;
    private boolean isChasing = false;

    // Freeze behavior (when player answers correctly)
    private boolean isFrozen = false;
    private float freezeTimer = 0f;
    private static final float FREEZE_DURATION = 3f; // Freeze for 3 seconds

    // Bonus behavior (going away for principal/parent/teacher)
    private boolean isAway = false;
    private float awayTargetX = 0f;
    private float awayTargetY = 0f;
    private float awayTimer = 0f;
    private float awayDuration = 0f;
    private float originalGuardCenterX = 0f;
    private float originalGuardCenterY = 0f;
    private boolean returningToGuard = false;
    private Teacher talkingWithTeacher = null; // For AnotherTeacherBonus
    private boolean isGoingToPrincipalOffice = false;
    private boolean hasReachedPrincipalOffice = false;
    private boolean isIgnoredForPrincipalBonus = false; // True if stuck and should be ignored
    private float principalOfficeStartTime = 0f; // Time when started going to principal's office
    private float lastPrincipalOfficeProgressTime = 0f; // Last time we made progress
    private float lastPrincipalOfficeDistance = Float.MAX_VALUE; // Last distance to office
    private static final float PRINCIPAL_OFFICE_STUCK_TIMEOUT = 5f; // Ignore teacher if stuck for 5 seconds
    private static final float PRINCIPAL_OFFICE_PROGRESS_THRESHOLD = 10f; // Consider making progress if moved this much
                                                                          // closer

    // Dead-End Filling pathfinder
    private static DeadEndFillingPathfinder pathfinder = null;
    private static SchoolLayout cachedSchoolLayout = null;
    private static float cachedWorldWidth = 0f;
    private static float cachedWorldHeight = 0f;

    // Wall Awareness System (shared across all teachers)
    private static WallAwarenessSystem wallAwareness = null;
    private static final float WALL_CHECK_DISTANCE = 80f; // Distance to check for walls ahead

    public Teacher(float x, float y) {
        super(x, y, 40, 40);
        this.speed = DEFAULT_SPEED;
        this.random = new Random();
        this.currentDirection = (float) (random.nextDouble() * Math.PI * 2); // Random initial direction
        this.lastX = x;
        this.lastY = y;
        this.lastSuccessfulMoveX = x;
        this.lastSuccessfulMoveY = y;
        this.paint = new Paint();
        this.paint.setColor(Color.RED);
        this.paint.setStyle(Paint.Style.FILL);
    }

    public void setGuardedFriend(Friend friend) {
        this.guardedFriend = friend;
        if (friend != null) {
            this.guardCenterX = friend.getCenterX();
            this.guardCenterY = friend.getCenterY();
            // Start patrolling around the friend
            this.patrolAngle = (float) (random.nextDouble() * Math.PI * 2);
        }
    }

    public Friend getGuardedFriend() {
        return guardedFriend;
    }

    @Override
    public void update(float deltaTime) {
        // This method is called by the overloaded update method
        // The actual update logic is in update(float deltaTime, SchoolLayout, Player,
        // List<Friend>)
    }

    public void update(float deltaTime, SchoolLayout schoolLayout, Player player, java.util.List<Friend> allFriends) {
        // Initialize pathfinder if needed (lazy initialization)
        initializePathfinder(schoolLayout, schoolLayout.getWorldWidth(), schoolLayout.getWorldHeight());

        // Update away timer (bonus behavior)
        if (isAway) {
            awayTimer += deltaTime;

            // Check if reached target location
            float dx = awayTargetX - getCenterX();
            float dy = awayTargetY - getCenterY();
            float distanceToTarget = (float) Math.sqrt(dx * dx + dy * dy);

            // Initialize principal office tracking
            if (isGoingToPrincipalOffice && principalOfficeStartTime == 0f) {
                principalOfficeStartTime = awayTimer;
                lastPrincipalOfficeProgressTime = awayTimer;
                lastPrincipalOfficeDistance = distanceToTarget;
            }

            if (!returningToGuard) {
                // Going to target location
                boolean reachedTarget = distanceToTarget < 30f;

                // Special handling for principal's office - wait until all teachers arrive
                if (isGoingToPrincipalOffice && reachedTarget) {
                    hasReachedPrincipalOffice = true;
                    isIgnoredForPrincipalBonus = false; // Reset ignored flag when reached
                    // Don't start returning yet - wait for signal from GameEngine
                    velocityX = 0;
                    velocityY = 0;
                    lastDistanceToTarget = Float.MAX_VALUE;
                    stuckAtTargetCounter = 0;
                    return; // Stay at office
                }

                // Check if stuck while going to principal's office
                if (isGoingToPrincipalOffice && !hasReachedPrincipalOffice && !isIgnoredForPrincipalBonus) {
                    // Check if making progress
                    boolean makingProgress = distanceToTarget < lastPrincipalOfficeDistance
                            - PRINCIPAL_OFFICE_PROGRESS_THRESHOLD;

                    if (makingProgress) {
                        // Making progress, update last progress time and distance
                        lastPrincipalOfficeProgressTime = awayTimer;
                        lastPrincipalOfficeDistance = distanceToTarget;
                    } else {
                        // Not making progress, check if stuck for too long
                        float timeSinceLastProgress = awayTimer - lastPrincipalOfficeProgressTime;
                        if (timeSinceLastProgress >= PRINCIPAL_OFFICE_STUCK_TIMEOUT) {
                            // Stuck for too long, ignore this teacher
                            isIgnoredForPrincipalBonus = true;
                        }
                    }
                }

                if (reachedTarget || (!isGoingToPrincipalOffice && awayTimer >= awayDuration)) {
                    // Reached target or time expired, start returning
                    returningToGuard = true;
                    awayTimer = 0f;
                    // Reset talking partner
                    talkingWithTeacher = null;
                    isGoingToPrincipalOffice = false;
                    hasReachedPrincipalOffice = false;
                    isIgnoredForPrincipalBonus = false;
                    principalOfficeStartTime = 0f;
                    lastPrincipalOfficeProgressTime = 0f;
                    lastPrincipalOfficeDistance = Float.MAX_VALUE;
                    lastDistanceToTarget = Float.MAX_VALUE;
                    stuckAtTargetCounter = 0;
                } else {
                    // Move towards target
                    if (talkingWithTeacher != null) {
                        // Update target to follow the teacher we're talking with
                        awayTargetX = talkingWithTeacher.getCenterX();
                        awayTargetY = talkingWithTeacher.getCenterY();
                        dx = awayTargetX - getCenterX();
                        dy = awayTargetY - getCenterY();
                        distanceToTarget = (float) Math.sqrt(dx * dx + dy * dy);
                    }

                    // Check if stuck (not making progress towards target)
                    if (distanceToTarget >= lastDistanceToTarget - 5f) {
                        // Not getting closer (or barely getting closer)
                        stuckAtTargetCounter++;
                    } else {
                        // Making progress, reset counter
                        stuckAtTargetCounter = 0;
                    }
                    lastDistanceToTarget = distanceToTarget;

                    // If stuck for too long, try alternative directions
                    if (stuckAtTargetCounter > 30) { // Stuck for ~0.5 seconds at 60fps
                        // Try moving perpendicular to the target direction
                        float perpAngle = (float) Math.atan2(dy, dx) + (float) (Math.PI / 2);
                        if (random.nextBoolean()) {
                            perpAngle += Math.PI; // Try other perpendicular direction
                        }
                        currentDirection = perpAngle;
                        stuckAtTargetCounter = 0; // Reset counter
                    } else {
                        // Normal movement towards target using Dead-End Filling and Wall Awareness
                        float desiredDirection = (float) Math.atan2(dy, dx);

                        // Use Dead-End Filling for initial direction
                        if (pathfinder != null) {
                            desiredDirection = pathfinder.getDirectionToTarget(
                                    getCenterX(), getCenterY(), awayTargetX, awayTargetY);
                        }

                        // Use Wall Awareness to avoid walls and problematic areas
                        if (wallAwareness != null) {
                            currentDirection = wallAwareness.findBestDirection(
                                    getCenterX(), getCenterY(), desiredDirection, width, height);
                        } else {
                            currentDirection = desiredDirection;
                        }
                    }

                    // If unstucking, use random movement
                    if (isUnstucking) {
                        moveRandomly(deltaTime);
                        return;
                    }

                    // Proactive wall checking: avoid walls before hitting them
                    if (wallAwareness != null) {
                        if (wallAwareness.hasWallAhead(getCenterX(), getCenterY(), currentDirection,
                                WALL_CHECK_DISTANCE, width, height)) {
                            // Wall detected ahead, find alternative direction
                            currentDirection = wallAwareness.findBestDirection(
                                    getCenterX(), getCenterY(), currentDirection, width, height);
                        }
                    }

                    // Apply movement
                    velocityX = (float) (Math.cos(currentDirection) * speed);
                    velocityY = (float) (Math.sin(currentDirection) * speed);

                    lastX = x;
                    lastY = y;
                    x += velocityX * deltaTime;
                    y += velocityY * deltaTime;

                    // Track successful movement (if we moved significantly)
                    float movedDistance = (float) Math.sqrt(
                            (x - lastSuccessfulMoveX) * (x - lastSuccessfulMoveX) +
                                    (y - lastSuccessfulMoveY) * (y - lastSuccessfulMoveY));
                    if (movedDistance > 10f) {
                        lastSuccessfulMoveX = x;
                        lastSuccessfulMoveY = y;
                        // Reset wall collision counter on successful movement
                        wallCollisionCounter = 0;
                    }
                    return; // Don't do normal behavior while going to target
                }
            }

            // Returning to guard position
            if (returningToGuard) {
                float returnTargetX, returnTargetY;
                if (guardedFriend != null && !guardedFriend.isRescued()) {
                    returnTargetX = guardedFriend.getCenterX();
                    returnTargetY = guardedFriend.getCenterY();
                } else {
                    returnTargetX = originalGuardCenterX;
                    returnTargetY = originalGuardCenterY;
                }

                dx = returnTargetX - getCenterX();
                dy = returnTargetY - getCenterY();
                float distanceToReturn = (float) Math.sqrt(dx * dx + dy * dy);

                if (distanceToReturn < 30f || awayTimer >= awayDuration * 2) {
                    // Returned to guard position
                    isAway = false;
                    returningToGuard = false;
                    awayTimer = 0f;
                    awayDuration = 0f;
                    talkingWithTeacher = null;
                    isGoingToPrincipalOffice = false;
                    hasReachedPrincipalOffice = false;
                    isIgnoredForPrincipalBonus = false;
                    principalOfficeStartTime = 0f;
                    lastPrincipalOfficeProgressTime = 0f;
                    lastPrincipalOfficeDistance = Float.MAX_VALUE;
                    // Restore guard center
                    if (guardedFriend != null && !guardedFriend.isRescued()) {
                        guardCenterX = guardedFriend.getCenterX();
                        guardCenterY = guardedFriend.getCenterY();
                    }
                } else {
                    // If unstucking, use random movement
                    if (isUnstucking) {
                        moveRandomly(deltaTime);
                        return;
                    }

                    // Move back to guard position using Dead-End Filling and Wall Awareness
                    float desiredDirection = (float) Math.atan2(dy, dx);

                    // Use Dead-End Filling for initial direction
                    if (pathfinder != null) {
                        desiredDirection = pathfinder.getDirectionToTarget(
                                getCenterX(), getCenterY(), returnTargetX, returnTargetY);
                    }

                    // Use Wall Awareness to avoid walls
                    if (wallAwareness != null) {
                        currentDirection = wallAwareness.findBestDirection(
                                getCenterX(), getCenterY(), desiredDirection, width, height);
                    } else {
                        currentDirection = desiredDirection;
                    }
                    velocityX = (float) (Math.cos(currentDirection) * speed);
                    velocityY = (float) (Math.sin(currentDirection) * speed);

                    lastX = x;
                    lastY = y;
                    x += velocityX * deltaTime;
                    y += velocityY * deltaTime;

                    // Track successful movement (if we moved significantly)
                    float movedDistance = (float) Math.sqrt(
                            (x - lastSuccessfulMoveX) * (x - lastSuccessfulMoveX) +
                                    (y - lastSuccessfulMoveY) * (y - lastSuccessfulMoveY));
                    if (movedDistance > 10f) {
                        lastSuccessfulMoveX = x;
                        lastSuccessfulMoveY = y;
                        // Reset wall collision counter on successful movement
                        wallCollisionCounter = 0;
                    }
                    return; // Don't do normal behavior while returning
                }
            }
        }

        // Update freeze timer
        if (isFrozen) {
            freezeTimer += deltaTime;
            if (freezeTimer >= FREEZE_DURATION) {
                // Unfreeze the teacher
                isFrozen = false;
                freezeTimer = 0f;
                isChasing = false; // Reset chasing state
            } else {
                // Teacher is frozen, don't move or detect player
                velocityX = 0;
                velocityY = 0;
                return;
            }
        }

        // Update guard center if friend still exists and is not rescued
        if (guardedFriend != null && !guardedFriend.isRescued()) {
            guardCenterX = guardedFriend.getCenterX();
            guardCenterY = guardedFriend.getCenterY();
        } else if (guardedFriend != null && guardedFriend.isRescued()) {
            // Friend was rescued, find a new one to guard
            guardedFriend = findNearestUnguardedFriend(allFriends);
            if (guardedFriend != null) {
                guardCenterX = guardedFriend.getCenterX();
                guardCenterY = guardedFriend.getCenterY();
                patrolAngle = (float) (random.nextDouble() * Math.PI * 2);
            }
        }

        // If unstucking, use random movement
        if (isUnstucking) {
            moveRandomly(deltaTime);
            return;
        }

        // Check if player is nearby (only if not frozen)
        if (player != null && !isFrozen) {
            float dx = player.getCenterX() - getCenterX();
            float dy = player.getCenterY() - getCenterY();
            float distanceToPlayer = (float) Math.sqrt(dx * dx + dy * dy);

            if (distanceToPlayer <= DETECTION_RADIUS) {
                // Player detected! Chase the player
                isChasing = true;
                chasePlayer(player, deltaTime);
                return;
            } else {
                isChasing = false;
            }
        }

        // If not chasing, guard the friend
        if (guardedFriend != null && !guardedFriend.isRescued()) {
            guardFriend(deltaTime);
        } else {
            // No friend to guard, wander around
            wander(deltaTime);
        }
    }

    private void guardFriend(float deltaTime) {
        // If unstucking, use random movement
        if (isUnstucking) {
            moveRandomly(deltaTime);
            return;
        }

        // Patrol around the friend in a circular pattern
        float targetX = guardCenterX + (float) Math.cos(patrolAngle) * patrolRadius;
        float targetY = guardCenterY + (float) Math.sin(patrolAngle) * patrolRadius;

        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 20f) {
            // Reached patrol point, move to next point on circle
            patrolAngle += (float) (Math.PI / 4); // Move 45 degrees
            if (patrolAngle >= Math.PI * 2) {
                patrolAngle -= Math.PI * 2;
            }
            // Add some randomness
            patrolAngle += (random.nextFloat() - 0.5f) * 0.5f;
        } else {
            // Move towards patrol point using Dead-End Filling and Wall Awareness
            float desiredDirection = (float) Math.atan2(dy, dx);

            // Use Dead-End Filling for initial direction
            if (pathfinder != null) {
                desiredDirection = pathfinder.getDirectionToTarget(
                        getCenterX(), getCenterY(), targetX, targetY);
            }

            // Use Wall Awareness to avoid walls
            if (wallAwareness != null) {
                currentDirection = wallAwareness.findBestDirection(
                        getCenterX(), getCenterY(), desiredDirection, width, height);
            } else {
                currentDirection = desiredDirection;
            }

            velocityX = (float) (Math.cos(currentDirection) * speed);
            velocityY = (float) (Math.sin(currentDirection) * speed);
        }

        // Store previous position
        lastX = x;
        lastY = y;

        // Move teacher
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;

        // Track successful movement (if we moved significantly)
        float movedDistance = (float) Math.sqrt(
                (x - lastSuccessfulMoveX) * (x - lastSuccessfulMoveX) +
                        (y - lastSuccessfulMoveY) * (y - lastSuccessfulMoveY));
        if (movedDistance > 10f) {
            lastSuccessfulMoveX = x;
            lastSuccessfulMoveY = y;
            // Reset wall collision counter on successful movement
            wallCollisionCounter = 0;
        }
    }

    private void chasePlayer(Player player, float deltaTime) {
        // If unstucking, use random movement
        if (isUnstucking) {
            moveRandomly(deltaTime);
            return;
        }

        float dx = player.getCenterX() - getCenterX();
        float dy = player.getCenterY() - getCenterY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            // Update direction to chase player using Dead-End Filling and Wall Awareness
            float desiredDirection = (float) Math.atan2(dy, dx);

            // Use Dead-End Filling for initial direction
            if (pathfinder != null) {
                desiredDirection = pathfinder.getDirectionToTarget(
                        getCenterX(), getCenterY(), player.getCenterX(), player.getCenterY());
            }

            // Use Wall Awareness to avoid walls
            if (wallAwareness != null) {
                currentDirection = wallAwareness.findBestDirection(
                        getCenterX(), getCenterY(), desiredDirection, width, height);
            } else {
                currentDirection = desiredDirection;
            }

            float chaseSpeed = speed * CHASE_SPEED_MULTIPLIER;
            velocityX = (float) (Math.cos(currentDirection) * chaseSpeed);
            velocityY = (float) (Math.sin(currentDirection) * chaseSpeed);

            // Store previous position
            lastX = x;
            lastY = y;

            // Move teacher
            x += velocityX * deltaTime;
            y += velocityY * deltaTime;

            // Track successful movement (if we moved significantly)
            float movedDistance = (float) Math.sqrt(
                    (x - lastSuccessfulMoveX) * (x - lastSuccessfulMoveX) +
                            (y - lastSuccessfulMoveY) * (y - lastSuccessfulMoveY));
            if (movedDistance > 10f) {
                lastSuccessfulMoveX = x;
                lastSuccessfulMoveY = y;
                // Reset wall collision counter on successful movement
                wallCollisionCounter = 0;
            }
        }
    }

    private void wander(float deltaTime) {
        // If unstucking, use random movement
        if (isUnstucking) {
            moveRandomly(deltaTime);
            return;
        }

        directionChangeTimer += deltaTime;

        // Check if teacher is stuck (not moving)
        float movedDistance = (float) Math.sqrt(
                (x - lastX) * (x - lastX) + (y - lastY) * (y - lastY));

        if (movedDistance < 5f) {
            stuckCounter++;
            if (stuckCounter > 10) {
                // Change direction if stuck
                currentDirection = (float) (random.nextDouble() * Math.PI * 2);
                stuckCounter = 0;
            }
        } else {
            stuckCounter = 0;
        }

        // Change direction periodically or if stuck
        if (directionChangeTimer >= directionChangeInterval || stuckCounter > 5) {
            // Use Dead-End Filling to find valid directions
            if (pathfinder != null) {
                List<Float> validDirections = pathfinder.getValidDirections(getCenterX(), getCenterY());
                if (!validDirections.isEmpty()) {
                    // Choose a random valid direction
                    currentDirection = validDirections.get(random.nextInt(validDirections.size()));
                } else {
                    // No valid directions, use random
                    float directionChange = (float) ((random.nextDouble() - 0.5) * Math.PI / 2);
                    currentDirection += directionChange;
                }
            } else {
                // Fallback: randomly change direction
                float directionChange = (float) ((random.nextDouble() - 0.5) * Math.PI / 2); // ±90 degrees
                currentDirection += directionChange;
            }
            directionChangeTimer = 0f;
        }

        // Update velocity based on current direction
        velocityX = (float) (Math.cos(currentDirection) * speed);
        velocityY = (float) (Math.sin(currentDirection) * speed);

        // Store previous position
        lastX = x;
        lastY = y;

        // Move teacher
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;

        // Track successful movement (if we moved significantly)
        float totalMovedDistance = (float) Math.sqrt(
                (x - lastSuccessfulMoveX) * (x - lastSuccessfulMoveX) +
                        (y - lastSuccessfulMoveY) * (y - lastSuccessfulMoveY));
        if (totalMovedDistance > 10f) {
            lastSuccessfulMoveX = x;
            lastSuccessfulMoveY = y;
            // Reset wall collision counter on successful movement
            wallCollisionCounter = 0;
        }
    }

    private Friend findNearestUnguardedFriend(java.util.List<Friend> allFriends) {
        Friend nearest = null;
        float minDistance = Float.MAX_VALUE;

        for (Friend friend : allFriends) {
            if (!friend.isRescued()) {
                float dx = friend.getCenterX() - getCenterX();
                float dy = friend.getCenterY() - getCenterY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = friend;
                }
            }
        }

        return nearest;
    }

    /**
     * Moves randomly to get unstuck from a wall
     */
    private void moveRandomly(float deltaTime) {
        unstuckTimer += deltaTime;
        unstuckDirectionChangeTimer += deltaTime;

        // Change direction frequently during random movement
        if (unstuckDirectionChangeTimer >= UNSTUCK_DIRECTION_CHANGE_INTERVAL) {
            // Use wall awareness to find a good direction
            if (wallAwareness != null) {
                List<Float> alternatives = wallAwareness.getAlternativeDirections(
                        getCenterX(), getCenterY(), width, height);
                if (!alternatives.isEmpty()) {
                    // Choose a random alternative direction
                    currentDirection = alternatives.get(random.nextInt(alternatives.size()));
                } else {
                    // No good alternatives, use random
                    currentDirection = (float) (random.nextDouble() * Math.PI * 2);
                }
            } else {
                // Fallback: random direction
                currentDirection = (float) (random.nextDouble() * Math.PI * 2);
            }
            unstuckDirectionChangeTimer = 0f;
        }

        // Check for walls ahead before moving
        if (wallAwareness != null) {
            if (wallAwareness.hasWallAhead(getCenterX(), getCenterY(), currentDirection,
                    WALL_CHECK_DISTANCE, width, height)) {
                // Wall ahead, find alternative
                List<Float> alternatives = wallAwareness.getAlternativeDirections(
                        getCenterX(), getCenterY(), width, height);
                if (!alternatives.isEmpty()) {
                    currentDirection = alternatives.get(random.nextInt(alternatives.size()));
                }
            }
        }

        // Update velocity based on current direction
        velocityX = (float) (Math.cos(currentDirection) * speed);
        velocityY = (float) (Math.sin(currentDirection) * speed);

        // Store previous position
        lastX = x;
        lastY = y;

        // Move teacher
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;

        // Check if we've successfully moved away (moved more than threshold)
        float movedDistance = (float) Math.sqrt(
                (x - lastSuccessfulMoveX) * (x - lastSuccessfulMoveX) +
                        (y - lastSuccessfulMoveY) * (y - lastSuccessfulMoveY));

        // If we've moved significantly, we're unstuck
        if (movedDistance > 30f) {
            isUnstucking = false;
            unstuckTimer = 0f;
            unstuckDirectionChangeTimer = 0f;
            wallCollisionCounter = 0;
            lastSuccessfulMoveX = x;
            lastSuccessfulMoveY = y;
        } else if (unstuckTimer >= UNSTUCK_DURATION) {
            // Time limit reached, exit unstuck mode anyway
            isUnstucking = false;
            unstuckTimer = 0f;
            unstuckDirectionChangeTimer = 0f;
            wallCollisionCounter = 0;
            lastSuccessfulMoveX = x;
            lastSuccessfulMoveY = y;
        }
    }

    public void onWallCollision() {
        // Mark this area as problematic
        if (wallAwareness != null) {
            wallAwareness.markProblematicArea(getCenterX(), getCenterY());
        }

        // Increment wall collision counter
        wallCollisionCounter++;

        // Check if we're stuck (multiple consecutive wall collisions)
        if (wallCollisionCounter >= WALL_COLLISION_THRESHOLD && !isUnstucking) {
            // Enter unstuck mode - use wall awareness to find alternative directions
            isUnstucking = true;
            unstuckTimer = 0f;
            unstuckDirectionChangeTimer = 0f;

            // Use wall awareness to find a good direction
            if (wallAwareness != null) {
                List<Float> alternatives = wallAwareness.getAlternativeDirections(
                        getCenterX(), getCenterY(), width, height);
                if (!alternatives.isEmpty()) {
                    // Choose a random alternative direction
                    currentDirection = alternatives.get(random.nextInt(alternatives.size()));
                } else {
                    // No good alternatives, use random
                    currentDirection = (float) (random.nextDouble() * Math.PI * 2);
                }
            } else {
                // Fallback: random direction
                currentDirection = (float) (random.nextDouble() * Math.PI * 2);
            }
        }

        // If already unstucking, don't change behavior here (let moveRandomly handle
        // it)
        if (isUnstucking) {
            return;
        }

        // Change direction when hitting a wall using Wall Awareness
        if (wallAwareness != null) {
            float desiredDirection = currentDirection;

            if (isAway && !returningToGuard) {
                // When going to a target, calculate direction to target
                float dx = awayTargetX - getCenterX();
                float dy = awayTargetY - getCenterY();
                desiredDirection = (float) Math.atan2(dy, dx);
            }

            // Use wall awareness to find best direction that avoids walls
            currentDirection = wallAwareness.findBestDirection(
                    getCenterX(), getCenterY(), desiredDirection, width, height);

            // Reset stuck counter
            if (isAway && !returningToGuard) {
                stuckAtTargetCounter = 0;
                lastDistanceToTarget = Float.MAX_VALUE;
            }
        } else {
            // Fallback: old behavior
            if (isAway && !returningToGuard) {
                float dx = awayTargetX - getCenterX();
                float dy = awayTargetY - getCenterY();
                float targetAngle = (float) Math.atan2(dy, dx);
                float perpAngle1 = targetAngle + (float) (Math.PI / 2);
                float perpAngle2 = targetAngle - (float) (Math.PI / 2);
                currentDirection = random.nextBoolean() ? perpAngle1 : perpAngle2;
                stuckAtTargetCounter = 0;
                lastDistanceToTarget = Float.MAX_VALUE;
            } else if (isChasing) {
                currentDirection += Math.PI + (random.nextDouble() - 0.5) * Math.PI / 3;
            } else if (guardedFriend != null && !guardedFriend.isRescued()) {
                patrolAngle += (float) (Math.PI / 2);
                if (patrolAngle >= Math.PI * 2) {
                    patrolAngle -= Math.PI * 2;
                }
                currentDirection += Math.PI + (random.nextDouble() - 0.5) * Math.PI / 2;
            } else {
                currentDirection += Math.PI + (random.nextDouble() - 0.5) * Math.PI / 2;
            }
        }

        // Normalize direction to 0-2π
        while (currentDirection < 0)
            currentDirection += Math.PI * 2;
        while (currentDirection >= Math.PI * 2)
            currentDirection -= Math.PI * 2;
    }

    @Override
    public void draw(Canvas canvas, float cameraX, float cameraY) {
        // Draw at world coordinates - camera transform is already applied to canvas
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public boolean isChasing() {
        return isChasing;
    }

    public void freeze() {
        isFrozen = true;
        freezeTimer = 0f;
        velocityX = 0;
        velocityY = 0;
        isChasing = false;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * Makes the teacher go to the principal's office
     */
    public void goToPrincipalOffice(float officeX, float officeY, float duration) {
        isAway = true;
        awayTargetX = officeX;
        awayTargetY = officeY;
        awayDuration = duration;
        awayTimer = 0f;
        returningToGuard = false;
        originalGuardCenterX = guardCenterX;
        originalGuardCenterY = guardCenterY;
        isChasing = false; // Stop chasing player
        isGoingToPrincipalOffice = true;
        hasReachedPrincipalOffice = false;
        isIgnoredForPrincipalBonus = false; // Reset ignored flag
        principalOfficeStartTime = 0f; // Will be set in update
        lastPrincipalOfficeProgressTime = 0f;
        lastPrincipalOfficeDistance = Float.MAX_VALUE;
        lastDistanceToTarget = Float.MAX_VALUE; // Reset stuck detection
        stuckAtTargetCounter = 0;
    }

    /**
     * Signals the teacher to return from principal's office
     */
    public void returnFromPrincipalOffice() {
        if (isGoingToPrincipalOffice && hasReachedPrincipalOffice) {
            returningToGuard = true;
            awayTimer = 0f;
            isGoingToPrincipalOffice = false;
            hasReachedPrincipalOffice = false;
        }
    }

    /**
     * Checks if teacher has reached the principal's office
     */
    public boolean hasReachedPrincipalOffice() {
        return hasReachedPrincipalOffice;
    }

    /**
     * Checks if teacher is going to principal's office
     */
    public boolean isGoingToPrincipalOffice() {
        return isGoingToPrincipalOffice;
    }

    /**
     * Checks if teacher is ignored for principal bonus (stuck and timeout)
     */
    public boolean isIgnoredForPrincipalBonus() {
        return isIgnoredForPrincipalBonus;
    }

    /**
     * Makes the teacher go talk with a parent
     */
    public void goTalkWithParent(float parentX, float parentY, float duration) {
        isAway = true;
        awayTargetX = parentX;
        awayTargetY = parentY;
        awayDuration = duration;
        awayTimer = 0f;
        returningToGuard = false;
        originalGuardCenterX = guardCenterX;
        originalGuardCenterY = guardCenterY;
        isChasing = false; // Stop chasing player
        lastDistanceToTarget = Float.MAX_VALUE; // Reset stuck detection
        stuckAtTargetCounter = 0;
    }

    /**
     * Makes the teacher go talk with another teacher
     */
    public void goTalkWithTeacher(Teacher otherTeacher, float duration) {
        isAway = true;
        talkingWithTeacher = otherTeacher;
        awayTargetX = otherTeacher.getCenterX();
        awayTargetY = otherTeacher.getCenterY();
        awayDuration = duration;
        awayTimer = 0f;
        returningToGuard = false;
        originalGuardCenterX = guardCenterX;
        originalGuardCenterY = guardCenterY;
        isChasing = false; // Stop chasing player
        lastDistanceToTarget = Float.MAX_VALUE; // Reset stuck detection
        stuckAtTargetCounter = 0;
    }

    /**
     * Checks if the teacher is currently away (bonus behavior)
     */
    public boolean isAway() {
        return isAway;
    }

    /**
     * Initializes the Dead-End Filling pathfinder and Wall Awareness System
     * (lazy initialization, shared across all teachers)
     */
    private void initializePathfinder(SchoolLayout schoolLayout, float worldWidth, float worldHeight) {
        // Only create if not already created or if layout changed
        if (pathfinder == null || cachedSchoolLayout != schoolLayout ||
                cachedWorldWidth != worldWidth || cachedWorldHeight != worldHeight) {
            pathfinder = new DeadEndFillingPathfinder(schoolLayout, worldWidth, worldHeight);
            wallAwareness = new WallAwarenessSystem(schoolLayout, worldWidth, worldHeight);
            cachedSchoolLayout = schoolLayout;
            cachedWorldWidth = worldWidth;
            cachedWorldHeight = worldHeight;
        }
    }
}
