# Project Implementation & Roadmap

This document tracks the technical implementation of ASTRON features.

## Implemented Features

### 1. Notification Section (`cc.astron.ui.notifications`)
- **Implemented**: `NotificationItem` (Room Entity), `NotificationAdapter`, `NotificationFragment`.
- **UI**: A list-based view displaying notifications with titles, messages, and timestamps.

### 2. Playlist Management (`cc.astron.db`, `cc.astron.ui.player`)
- **Implemented**: `Playlist` and `VideoItem` (Room Entities), `AstronDao`, `AstronDatabase`.
- **UI**: "Add to Playlist" dialog in `PlayerActivity` allowing users to choose or create playlists.

### 3. Media Player Controls (`cc.astron.ui.player`)
- **Implemented**: `SubtitleController`, `StreamController`, `PlayerActivity`.
- **UI**: In-player buttons and dialogs for:
    - **Subtitles**: Toggle on/off and language selection.
    - **Quality**: Select specific resolutions (1080p, 720p, etc.) or "Auto".
    - **Audio Language**: Switch between available audio tracks.
    - **Quality Bypass**: Automatically forces highest supported bitrate upon video load.

### 4. Core UI & Navigation (`cc.astron.ui`)
- **Implemented**: `MainActivity` with `BottomNavigationView` connecting Home, Notifications, and Library fragments.

## Technical Stack
- **Android SDK**: Min 24, Target 34.
- **Language**: Kotlin.
- **Media Player**: ExoPlayer.
- **Database**: Room Persistence Library.
- **UI Components**: Material Design, ConstraintLayout, RecyclerView.

## Future Milestones
- Implement real-time push notifications using Firebase Cloud Messaging (FCM).
- Add support for offline video downloads with background service management.
- Enhance UI/UX with custom themes and animations.
