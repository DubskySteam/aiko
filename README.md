<div align="center" style="display: flex; align-items: center; gap: 10px;">
    <img src="docs/github/logo.png" alt="WinFlux Logo" width="128" style="border-radius: 50%; vertical-align: middle; align: left">
    <h1 style="margin: 0;">Aiko</h1>
</div>

## Anime Browser & Watchlist Manager

A **Kotlin Multiplatform Compose** desktop app to browse anime, manage your personal watchlists from **AniList**, and watch anime directly in an embedded player. Includes synced watch parties with friends and **Discord Rich Presence** support.

## Features (targeted for stable version)

- 🔥 **Explore Trending & Seasonal Anime** – Stay up to date with the latest hits.
- 🔍 **Browse the AniList Database** – Search and view detailed anime information.
- 🎥 **Embedded Video Player** – Watch anime directly in the app.
- 👫 **Watch Together** – Sync up with friends and enjoy anime together.
- 🎮 **Discord RPC Integration** – Show your currently watched anime in Discord.

## Roadmap

- [X] Basic UI for browsing anime
- [X] Implement AniList API integration (search, trending etc.)
- [X] Add user authentication for AniList
- [ ] Implement watchlist management
- [X] Implement embedded video player
- [ ] Implement "Watch Together"
- [ ] Improve UI/UX for a seamless experience

## Build from source

1. Clone the repository:
   ```sh
   git clone https://github.com/dubskysteam/aiko.git
   cd aiko
   ```
2. Build the project:
   ```sh
   ./gradlew build
   ```
   
## Install from releases

**Windows**:
1. Download the latest release from the [Releases](https://github.com/dubskysteam/aiko/releases) page.
2. Run the installer and follow the instructions.
3. Launch the app from the Start menu.

**Linux**:

_(Only as self-built for now, a until stable version is achieved and then published on the package managers)_

## Screenshots
![Screenshot](docs/github/homescreen.png)
![Screenshot](docs/github/browse.png)
![Screenshot](docs/github/settings.png)
