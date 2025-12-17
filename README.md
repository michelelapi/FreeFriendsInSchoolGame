---
name: School Escape Game Plan
overview: Build a side-scrolling Android game where a free kid rescues friends from teachers, avoiding capture and answering questions when caught. Includes level progression through school grades, score management, and standard game features.
todos:
  - id: setup-game-engine
    content: Create GameView, GameEngine, and GameManager classes with basic game loop and state management
    status: pending
  - id: create-entities
    content: Implement Entity base class, Player, Teacher, and Friend classes with basic rendering and movement
    status: pending
  - id: implement-collision
    content: Add collision detection system for player-teacher and player-friend interactions
    status: pending
  - id: build-level-system
    content: Create Level, LevelManager, and LevelData classes with grade-based progression (elementary → PhD)
    status: pending
  - id: create-question-system
    content: Implement Question model, QuestionDatabase, and QuestionManager with difficulty-based question selection
    status: pending
  - id: add-question-dialog
    content: Create QuestionDialog UI for displaying questions when player is caught by teacher
    status: pending
  - id: implement-rescue-mechanics
    content: Add friend rescue logic, score calculation, and win condition (rescue all friends)
    status: pending
  - id: create-ui-screens
    content: Build IntroActivity, HelpActivity, SettingsActivity, and GameOverActivity with proper navigation
    status: pending
  - id: add-settings-persistence
    content: Implement PreferencesManager for storing sound settings and other preferences
    status: pending
  - id: implement-score-system
    content: Create ScoreManager for persisting high scores and tracking progress across sessions
    status: pending
  - id: add-visual-assets
    content: Create simple sprites for player, teachers, friends, and school background elements
    status: pending
  - id: update-main-activity
    content: Transform MainActivity into main menu hub with navigation to all game screens
    status: pending
---

# School Escape Game - Implementation Plan

## Overview

A top-down Android game built with native Canvas/View where a free kid navigates a school environment to rescue friends from teachers. The game progresses through educational levels (elementary → high school → college → PhD) with difficulty-appropriate questions.

## Architecture

### Core Components

- **Game Engine**: Custom game loop using Canvas and View with frame-based updates
- **Game State Management**: State machine for menu, playing, paused, question, game over states
- **Entity System**: Base classes for Player, Teacher, Friend entities with collision detection
- **Question System**: Modular question database with difficulty levels and subject categories
- **Data Persistence**: SharedPreferences for scores, level progress, and settings

### Project Structure

```
app/src/main/
├── java/org/example/
│   ├── MainActivity.java (entry point)
│   ├── game/
│   │   ├── GameEngine.java (main game loop)
│   │   ├── GameState.java (enum for game states)
│   │   ├── GameView.java (custom View with Canvas)
│   │   ├── GameManager.java (coordinates game logic)
│   │   └── Camera.java (camera system for scrolling in all directions)
│   ├── entities/
│   │   ├── Player.java (free kid character)
│   │   ├── Teacher.java (enemy/guard)
│   │   ├── Friend.java (rescue target)
│   │   └── Entity.java (base class)
│   ├── physics/
│   │   ├── CollisionDetector.java
│   │   ├── MovementController.java
│   │   └── Camera.java (viewport/camera system for scrolling) (4-directional movement)
│   ├── game/
│   │   └── Camera.java (viewport scrolling system)
│   ├── questions/
│   │   ├── Question.java (data model)
│   │   ├── QuestionDatabase.java (question storage)
│   │   └── QuestionManager.java (question selection logic)
│   ├── levels/
│   │   ├── Level.java (level data model)
│   │   ├── LevelManager.java (level progression)
│   │   └── LevelData.java (level definitions)
│   ├── ui/
│   │   ├── IntroActivity.java
│   │   ├── HelpActivity.java
│   │   ├── SettingsActivity.java
│   │   ├── GameOverActivity.java
│   │   └── QuestionDialog.java
│   └── data/
│       ├── ScoreManager.java (score persistence)
│       └── PreferencesManager.java (settings storage)
└── res/
    ├── layout/ (UI layouts)
    ├── drawable/ (game sprites/assets)
    ├── values/
    │   ├── strings.xml (all text resources)
    │   ├── colors.xml
    │   └── questions.xml (question data)
```

## Implementation Steps

### Phase 1: Foundation & Core Game Engine

1. **Setup Game Infrastructure**

   - Create `GameView.java` extending View with Canvas rendering
   - Implement `GameEngine.java` with game loop (update/render cycle)
   - Add `GameState.java` enum (MENU, PLAYING, PAUSED, QUESTION, GAME_OVER)
   - Create `GameManager.java` to coordinate game flow

2. **Entity System**

   - Create base `Entity.java` class with position, size, velocity
   - Implement `Player.java` with touch controls for movement
   - Create `Teacher.java` with AI movement patterns (patrol/chase)
   - Implement `Friend.java` as static rescue targets
   - Add sprite rendering for all entities

3. **Physics & Collision**

   - Implement `CollisionDetector.java` for rectangle-based collision
   - Add collision handling: player-teacher (caught), player-friend (rescue)
   - Create `MovementController.java` for smooth character movement in 4 directions (up, down, left, right)
   - Implement camera/viewport system for scrolling in all directions
   - Add tile-based or free movement system (no gravity, top-down perspective)

### Phase 2: Gameplay Mechanics

4. **Level System**

   - Create `Level.java` data model (grade level, difficulty, question pool)
   - Implement `LevelManager.java` for progression tracking
   - Define `LevelData.java` with level definitions:
     - Elementary (Grades 1-5): Simple math, basic questions
     - Middle School (Grades 6-8): Intermediate questions
     - High School (Grades 9-12): Subject-specific questions
     - College: Advanced questions
     - PhD: Expert-level questions
   - Add level selection screen

5. **Question System**

   - Create `Question.java` model (question text, options, correct answer, difficulty)
   - Implement `QuestionDatabase.java` with initial question sets
   - Create `QuestionManager.java` to select questions based on level
   - Build `QuestionDialog.java` for question display
   - Add question result handling (correct = continue, incorrect = game over)

6. **Rescue Mechanics**

   - Implement friend rescue logic (touch friend = rescue)
   - Add score calculation based on friends rescued
   - Track rescued friends count
   - Add win condition (rescue all friends in level)

### Phase 3: UI & Navigation

7. **Main Menu & Navigation**

   - Update `MainActivity.java` as main menu hub
   - Add navigation to: Intro, Help, Settings, Play Game
   - Create `IntroActivity.java` with game story/introduction
   - Implement `HelpActivity.java` with game instructions
   - Add back navigation handling

8. **Settings System**

   - Create `SettingsActivity.java` with preferences UI
   - Implement `PreferencesManager.java` for settings storage
   - Add sound toggle (background music, SFX)
   - Add difficulty preference (if applicable)
   - Persist settings using SharedPreferences

9. **Game Over & Score Display**

   - Create `GameOverActivity.java` showing final score
   - Display rescued friends count
   - Show level reached
   - Add "Play Again" and "Main Menu" buttons

### Phase 4: Data Persistence & Progression

10. **Score Management**

    - Implement `ScoreManager.java` for score persistence
    - Store high scores per level
    - Track total score across sessions
    - Display score during gameplay (HUD overlay)
    - Add score calculation: base points + time bonus + difficulty multiplier

11. **Level Progression**

    - Save current level/unlocked levels
    - Track highest grade reached (elementary → PhD)
    - Add level unlock logic (complete level N to unlock N+1)
    - Store progression in SharedPreferences

### Phase 5: Polish & Assets

12. **Visual Assets**

    - Create simple sprite graphics for player, teachers, friends
    - Design school background tiles
    - Add simple animations (walking, idle)
    - Create UI button graphics
    - Add game icons and launcher icon

13. **Audio (Optional)**

    - Add background music
    - Add sound effects (rescue, caught, correct answer, wrong answer)
    - Implement audio manager with settings integration

14. **Testing & Refinement**

    - Test collision detection accuracy
    - Balance teacher AI difficulty
    - Verify question difficulty scaling
    - Test score persistence across app restarts
    - Optimize performance (frame rate, memory)

## Technical Details

### Dependencies

- Current: androidx.appcompat, material, constraintlayout
- Add: None required for basic version (using native Canvas)

### Key Files to Create/Modify

- [`app/src/main/java/org/example/MainActivity.java`](app/src/main/java/org/example/MainActivity.java) - Update to main menu
- [`app/src/main/java/org/example/game/GameView.java`](app/src/main/java/org/example/game/GameView.java) - New game rendering view
- [`app/src/main/java/org/example/game/GameEngine.java`](app/src/main/java/org/example/game/GameEngine.java) - New game loop
- [`app/src/main/java/org/example/entities/Player.java`](app/src/main/java/org/example/entities/Player.java) - New player entity
- [`app/src/main/java/org/example/questions/QuestionDatabase.java`](app/src/main/java/org/example/questions/QuestionDatabase.java) - New question storage
- [`app/src/main/java/org/example/data/ScoreManager.java`](app/src/main/java/org/example/data/ScoreManager.java) - New score persistence
- [`app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml) - Add all game strings
- [`app/src/main/res/values/questions.xml`](app/src/main/res/values/questions.xml) - New question data file

### Game Loop Structure

```java
// Pseudocode
while (gameRunning) {
    update(deltaTime);  // Update entities, check collisions
    render(canvas);     // Draw all game elements
}
```

### Data Models

- **Question**: questionText, options[], correctAnswerIndex, difficulty, subject
- **Level**: levelId, gradeLevel, maxFriends, teacherCount, questionPool
- **Score**: currentScore, highScore, levelReached, friendsRescued

## Future Enhancements (Post-Basic Version)

- Paid features: Custom school builder, photo uploads for friends/teachers
- More question categories and difficulty levels
- Power-ups and special abilities
- Multiplayer mode
- Achievement system