# Once Upon a Time — Minecraft Mod

> *"All magic comes with a price, dearie."*

A lore-faithful recreation of the *Once Upon a Time* TV series for **Minecraft 1.21.1** with **NeoForge 21.1.x**. Play as characters from the show, learn spells, perform rituals, brew potions, wield legendary artifacts, and travel between five magical dimensions.

---

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [Player Roles](#player-roles)
- [Magic System](#magic-system)
- [Spells](#spells)
- [Rituals](#rituals)
- [Potion Brewer & Recipes](#potion-brewer)
- [Items & Artifacts](#items--artifacts)
- [Custom Entities](#custom-entities)
- [Dimensions](#dimensions)
- [Commands](#commands)
- [Building from Source](#building-from-source)

---

## Installation

1. Install Minecraft **1.21.1** with NeoForge (21.1.x recommended).
2. Download `onceuponatime-1.0.0.jar` from the [Releases](https://github.com/THTStreamer/OnceUponaTime/releases) page.
3. Place the `.jar` file in your Minecraft `mods/` folder.
4. Launch Minecraft with the NeoForge profile.

---

## Quick Start

1. **Create a new world** and pick a role using the `/ouat` command.
2. **Learn your first spell** — run `/ouat spells` to see what's available based on your proficiency.
3. **Learn a spell** — run `/ouat learn healing_light` (or any spell you meet the proficiency for).
4. **Cast it** — run `/ouat cast healing_light` or use the spell's keybind.
5. **Brew potions** — craft a Potion Brewer and combine ingredients in the GUI.
6. **Build a ritual altar** — construct the structure for your chosen ritual to claim a unique role.
7. **Explore dimensions** — use Portal Creation spells or the Enchanted Compass to travel.

---

## Player Roles

Roles define your character's place in the story. Each role is obtained through a ritual or special action.

| Role | How to Obtain | Description |
|------|--------------|-------------|
| **Dark One** | Dark One Ascension Ritual | The darkest of all magic users. Soul bound to darkness. Unlocks the *Dark One's Power* spell. Only **one Dark One** can exist at a time across the entire server. |
| **Savior** | Savior Awakening Ritual | Champion of light magic. "The light of hope shines through you." |
| **Truest Believer** | Truest Believer Awakening Ritual | "Your belief can defy reality itself." The highest expression of faith in the story. |
| **Author** | Author Awakening Ritual | "The pen is in your hand. The story awaits." Can write reality itself. |

### Magic Alignment

Every player has a magical alignment that affects which spells they can learn. Your alignment is an integer from **-100** (pure Dark) to **+100** (pure Light), recomputing automatically:

| Alignment | Range | Description |
|-----------|-------|-------------|
| **Light** | +31 to +100 | Healing, protection, and hope spells |
| **Neutral** | -30 to +30 | Utility, teleportation, and transformation spells |
| **Dark** | -100 to -31 | Curses, corruption, and destruction spells |
| **None** | — | No magic used yet |

**Alignment Shifts:**

| Action | Shift |
|--------|-------|
| Heart Ripping | -15 |
| Curse of Corruption | -10 |
| Memory Wipe | -5 |
| Dark One's Power | -3 |
| Dark Curse | -8 |
| Heart Release | +10 |
| Healing Light | +5 |
| Heart Protection | +5 |
| Ward Spell | +3 |

### Magic Proficiency

Your proficiency (0–100) determines which spells you can learn. Each spell tracks its own proficiency independently, and your **overall proficiency** is the average of all known spell proficiencies.

**Per-Spell Proficiency Tiers:**

| Tier | Range | Food Cost |
|------|-------|-----------|
| **Novice** | 0–20 | 100% |
| **Apprentice** | 21–40 | 90% |
| **Adept** | 41–60 | 80% |
| **Expert** | 61–80 | 70% |
| **Master** | 81–100 | 60% |

Gain proficiency by casting spells — each cast grants 2–3 points for that spell. Proficiency also increases when you remove curses or complete story progression.

---

## Spells

Spells are learned by running `/ouat learn <spell_name>` and cast with `/ouat cast <spell_name>`. Each spell costs **food points** (hunger) when cast in Survival mode. Creative mode players cast for free.

### Light Magic

| Spell | Proficiency | Food Cost | Effect |
|-------|------------|-----------|--------|
| **Healing Light** | 10 | 6 | Heals 6 HP, grants Regeneration II (10s) and Absorption I (30s) |
| **Light Blast** | 15 | 4 | Deals 10 magic damage with knockback to nearest player (12 blocks) |
| **Protection** | 20 | 8 | Resistance II, Absorption III, Fire Resistance (30s) |
| **Heart Protection** | 25 | 8 | Protects a player's heart from being ripped (prevents Heart Ripping spell) |
| **Heart Release** | 25 | 6 | Removes heart protection from a target |
| **Memory Restoration** | 25 | 10 | Removes ALL curses. If no curses, gains +2 proficiency |
| **True Love's Kiss** | 30 | 12 | Removes ALL curses, heals to full HP, grants True Love's Blessing (20 min) |
| **Spell of Shattered Sight** | 40 | 14 | Affects ALL players within 30 blocks. Weakness, Slowness, Blindness, Confusion, Glowing (30s). Everyone turns on each other |

### Dark Magic

| Spell | Proficiency | Food Cost | Effect |
|-------|------------|-----------|--------|
| **Fireball** | 15 | 6 | Shoots a fireball projectile |
| **Heart Ripping** | 35 | 10 | Rips a player's heart, dropping a Stolen Heart item. Fails if heart is protected or already ripped |
| **Curse of Corruption** | 32 | 12 | Wither, Weakness, Mining Fatigue, Confusion (20–30s). Applies corruption curse (120s) |
| **Curse of the Empty Heart** | 30 | 12 | Weakness, Mining Fatigue, Confusion, Blindness (40s). **Forgets up to 3 spells.** |
| **Curse of the Savior** | 45 | 14 | Wither II, Weakness II, Darkness, Confusion, Mining Fatigue (30s). Reduces proficiency by 30 |
| **Frozen Heart Curse** | 30 | 10 | Slowness II, Weakness II, Mining Fatigue, Darkness (20–40s). Applies frozen heart curse (80s) |
| **Dark One's Power** | 40 | 14 | **Dark One only.** Strength II, Speed I, Fire Resistance, Night Vision, Regeneration II (20s) |
| **Sleeping Curse** | 20 | 8 | Blindness, Slowness II, Wither I, Night Vision (30s). Applies sleeping curse (60s) |
| **Night Root Absorption** | 22 | 8 | On target: Wither, Weakness + heals caster 8 HP. Self: cleanses darkness + Regeneration. Grants +1 proficiency |

### Utility & Transformation

| Spell | Proficiency | Food Cost | Effect |
|-------|------------|-----------|--------|
| **Conjuration** | 12 | 6 | Randomly conjures: Golden Apple, 2 Ender Pearls, 3 XP Bottles, 4 Glowstone Dust, or 4 Quartz |
| **Telekinesis** | 10 | 4 | Pushes all non-player entities in a 10-block cone forward |
| **Weather Control** | 18 | 8 | Cycles weather: Clear → Rain → Thunderstorm → Clear |
| **Invisibility** | 18 | 8 | Invisibility + Speed I (30s) |
| **Levitation** | 20 | 8 | Slow Falling, Jump Boost III, Levitation I (10–20s). Float like a fairy |
| **Teleportation** | 20 | 8 | Random teleport ±50 blocks from current position |
| **Immobilization** | 15 | 6 | Slowness X, Mining Fatigue X, Blindness (5–20s) — complete freeze |
| **Dreamcatcher** | 20 | 8 | Reveals target's magic proficiency, spell count, and curse count. Gives Confusion (10s) |
| **Shapeshifting** | 25 | 10 | Grants Invisibility + Speed II (30s) to mimic another player |
| **Banishment** | 28 | 12 | Teleports target to a random location ±500 blocks away |
| **Portal Creation** | 30 | 12 | Multi-portal system — save, list, and teleport to named portals. First cast saves location, named cast teleports. Cross-dimension only for saving, not teleporting |
| **Age Manipulation** | 35 | 14 | 50/50 chance: Aging (Weakness, Slowness, Mining Fatigue) OR Rejuvenation (Strength, Speed, Regeneration) |
| **Memory Wipe** | 25 | 10 | Clears ALL learned spells from target, reduces proficiency by 20 |
| **Squid Ink Paralysis** | 25 | 8 | Slowness 255, Mining Fatigue 255, Blindness, Weakness II, Levitation — complete paralysis |
| **Curse of the Empty Heart** | 30 | 12 | Weakness, Mining Fatigue, Confusion, Blindness (40s). Forgets up to 3 spells |
| **Ward** | 25 | 10 | Protection — look at an open doorway, cast to flood-fill interior. Blocks non-authorized players from breaking blocks inside. Owner can authorize others |

---

## Rituals

Rituals are performed by building a specific block structure. Ingredients are consumed from your inventory when the ritual activates. Right-click the bottom block of the structure to begin. Each ritual claims a **unique role** — only one player per server can hold each role at a time.

### Dark One Ascension

> *"The Dark One is the most powerful being in all the realms."*

**Proficiency Required:** 0
**Duration:** 1 hour real-time

**Ingredients:**
- 1× Shard of Dark Power
- 3× Essence of Shadow
- 1× Heart of Darkness

**Structure:**
```
        ◆ Diamond Block
        ■
        ■  ← Obsidian (3 blocks tall)
    ▢   ■   ▢  ← Redstone Torches (4 sides, at bottom level)
        ■
    ═══════  ← Base
```
*Redstone Torches go on the North, South, East, and West sides of the bottom Obsidian block.*

**Effect:** Claims the Dark One role. Unlocks the *Dark One's Power* spell. Server-wide broadcast when completed.

---

### Savior Awakening

> *"The light of hope shines through you."*

**Proficiency Required:** 25
**Duration:** 1 hour real-time

**Ingredients:**
- 3× Shard of Light
- 1× Essence of Hope
- 1× Crystal of Purity

**Structure:**
```
        ◆ Diamond Block
        □  ← Quartz Block
        □
        □
    ▣   ☆   ▣  ← Torches (4 sides, at bottom level)
        ☆  ← Gold Block (base)
    ═══════
```
*Torches go on the North, South, East, and West sides of the Gold Block.*

**Effect:** Claims the Savior role. Light and Totem particle effects.

---

### Truest Believer Awakening

> *"Your belief can defy reality itself."*

**Proficiency Required:** 50
**Duration:** 2 hours real-time

**Ingredients:**
- 1× Tear of True Love
- 3× Essence of Belief
- 1× Heart of Innocence

**Structure:**
```
        ◆ Diamond Block
        ▨  ← Purpur Block
        ▨
        ▨
        ▨
    ║   ▨   ║  ← End Rods (4 sides, at bottom level)
        ▨  ← Purpur Block (base, 5 blocks tall)
    ═══════
```
*End Rods go on the North, South, East, and West sides of the bottom Purpur Block.*

**Effect:** Claims the Truest Believer role. Dragon breath and portal particle effects.

---

### Author Awakening

> *"The pen is in your hand. The story awaits."*

**Proficiency Required:** 75
**Duration:** 3 hours real-time

**Ingredients:**
- 1× Ink of Creation
- 1× Quill of Fate
- 3× Page of Destiny

**Structure:**
```
        ◆ Diamond Block
        ⬚  ← Bookshelf
        ⬚
        ⬚
        ⬚
        ⬚
    ⬚   ⬚   ⬚  ← Enchanting Tables (4 sides, at bottom level)
        ⬚  ← Bookshelf (base, 6 blocks tall)
    ═══════
```
*Enchanting Tables go on the North, South, East, and West sides of the bottom Bookshelf.*

**Effect:** Claims the Author role. Enchant and smoke particle effects.

---

## Potion Brewer

The **Potion Brewer** is a craftable item that opens a custom brewing GUI. Place ingredients in the 3×3 grid and the output slot will produce the result.

### Crafting the Potion Brewer

The Potion Brewer itself must be obtained through creative mode or commands — it is not yet obtainable through vanilla crafting.

### How to Use

1. Right-click with the Potion Brewer in hand to open the GUI.
2. Place the required ingredients in any of the 3×3 input slots.
3. The result will appear in the output slot.
4. Click the output to collect your potion.

### All Recipes

| Recipe | Ingredients | Output |
|--------|------------|--------|
| **Dark Curse** | Stolen Heart + Shard of Dark Power + Essence of Shadow + Heart of Darkness + Night Root + Fairy Dust | Dark Curse |
| **Sleeping Curse** | Poisoned Apple + Enchanted Rose | Poisoned Apple (Cursed) |
| **True Love Potion** | Tear of True Love + Fairy Dust + Enchanted Candle | True Love Potion |
| **Memory Restoration** | Essence of Belief + Memory Potion | Memory Potion (Restored) |
| **Curse of Empty Heart** | Stolen Heart + Heart of Darkness | Stolen Heart (Cursed) |
| **Enhanced Dreamcatcher** | Dream Catcher + Night Root + Squid Ink | Dream Catcher (Enhanced) |
| **Grimoire of Darkness** | Essence of Shadow + Shard of Dark Power + Ink of Creation | Grimoire of Darkness |
| **Grimoire of Light** | Shard of Light + Essence of Hope + Crystal of Purity | Grimoire of Light |
| **Heart Protection Charm** | Tear of True Love + Heart of Innocence | Enchanted Candle |
| **Night Root Extract** | Fairy Dust + Night Root + Night Root | Night Root (Extract) |
| **Squid Ink Brew** | Essence of Shadow + Squid Ink + Squid Ink | Squid Ink (Brew) |

---

## Items & Artifacts

### Light Ritual Ingredients

| Item | Rarity | Used In |
|------|--------|---------|
| Shard of Light | Uncommon | Savior Awakening, Grimoire of Light |
| Essence of Hope | Uncommon | Savior Awakening, Grimoire of Light |
| Crystal of Purity | Rare | Savior Awakening, Grimoire of Light |
| Tear of True Love | Rare | Truest Believer Awakening, True Love Potion, Heart Protection Charm |
| Essence of Belief | Uncommon | Truest Believer Awakening, Memory Restoration |
| Heart of Innocence | Rare | Truest Believer Awakening, Heart Protection Charm |

### Dark Ritual Ingredients

| Item | Rarity | Used In |
|------|--------|---------|
| Shard of Dark Power | Uncommon | Dark One Ascension, Grimoire of Darkness, Dark Curse |
| Essence of Shadow | Uncommon | Dark One Ascension, Grimoire of Darkness, Dark Curse, Squid Ink Brew |
| Heart of Darkness | Rare | Dark One Ascension, Dark Curse, Curse of Empty Heart |
| Ink of Creation | Uncommon | Author Awakening, Grimoire of Darkness |
| Quill of Fate | Uncommon | Author Awakening |
| Page of Destiny | Uncommon | Author Awakening |

### Potions & Consumables

| Item | Rarity | Effect |
|------|--------|--------|
| True Love Potion | Epic | Removes all curses, heals to full HP |
| Memory Potion | Uncommon | Restores memories (used in Memory Restoration recipe) |
| Night Root | Uncommon | Used in Night Root Absorption spell and recipes |
| Squid Ink | Uncommon | Used in Squid Ink Paralysis spell and recipes |
| Fairy Dust | Uncommon | Used in multiple Potion Brewer recipes |

### Legendary Artifacts

| Item | Rarity | Effect |
|------|--------|--------|
| Dark One Dagger | Epic | The legendary weapon that can slay the Dark One. Durability: 1000 |
| Excalibur | Epic | The legendary sword. Durability: 2000 |
| Maleficent's Staff | Epic | Staff of the Mistress of All Evil. Durability: 1500 |
| Author's Quill | Epic | A quill that can write reality. Durability: 500 |
| Author's Book | Epic | The book where all stories are written |

### Other Artifacts

| Item | Rarity | Effect |
|------|--------|--------|
| Grimoire of Light | Rare | Contains 15 light magic spells |
| Grimoire of Darkness | Rare | Contains 15 dark magic spells |
| Enchanted Rose | Rare | A rose that never fades. Used in Sleeping Curse recipe |
| Magic Mirror | Rare | A mirror that shows truth |
| Cursed Talisman | Uncommon | A talisman with dark power |
| Dream Catcher | Rare | Reveals target's information |
| Enchanted Compass | Rare | Points toward enchanted locations |
| Chipped Cup | Rare | A memento from the Enchanted Forest |
| Magic Beans | Rare | Plant to grow magical beanstalks (stacks to 16) |
| Enchanted Candle | Rare | Used in Heart Protection Charm recipe (stacks to 3) |
| Poisoned Apple | Rare | Inflicts the Sleeping Curse |
| Flying Broomstick | Rare | Ride through the skies |
| Jefferson's Hat | Rare | The Mad Hatter's hat |
| Blue Fairy's Wand | Rare | A wand of the Blue Fairy |
| Excalibur Stone | Epic | The stone that holds Excalibur |
| Magic Globe | Rare | See across the realms |
| Dark Curse | Epic | Teleports all players within 300 chunks to Storybrooke |

---

## Custom Entities

| Entity | Type | Health | Damage | Armor | Special Ability |
|--------|------|--------|--------|-------|----------------|
| **Evil Queen** | Monster | 80 HP | 6 | 4 | Hostile to all players. Witch sounds, smoke particles. Spawns in Storybrooke. |
| **Forest Fairy** | Passive | 10 HP | — | — | Flies, neutral. Enchantment and End Rod particles. Spawns in Enchanted Forest. |
| **Lost Soul** | Monster | 30 HP | 5 | 2 | Soul particles. Withers nearby players every 2 seconds. Spawns in the Underworld. |
| **Lost Boy** | Monster | 20 HP | 4 | 0 | Very fast (0.38 speed). Self-speed boost in combat. Spawns in Neverland. |
| **Mad Hatter** | Monster | 40 HP | 5 | 0 | Applies random chaos effects to nearby players every 4 seconds (Speed, Jump Boost, Haste, Blindness, Confusion, Poison, or Levitation). Spawns in Wonderland. |
| **Dark Swarm** | Monster | 25 HP | 4 | 1 | Inflicts Darkness on nearby players every 3 seconds. Campfire smoke particles. Spawns in the Underworld. |

---

## Dimensions

Use the **Portal Creation** spell or teleport commands to travel between five magical realms.

| Dimension | Biomes | Description |
|-----------|--------|-------------|
| **Storybrooke** | Storybrooke Town, Storybrooke Forest | The cursed town where the characters live with no memories of their true selves |
| **Enchanted Forest** | Enchanted Magical Grove, Enchanted Dense Forest | The magical realm where fairy tales happen |
| **Underworld** | Underworld Wastes, River Styx | The land of the dead where lost souls wander |
| **Neverland** | Neverland Forest, Neverland Beach | The island where no one grows up |
| **Wonderland** | Strange Wonderland, Maze Wonderland | A bizarre realm where nothing makes sense |

### Dimension Commands

```bash
/ouat portal <name>    # Save or teleport to a named portal location
/ouat portal list      # List all saved portals
/ouat portal remove <name>  # Remove a saved portal
```

---

## Commands

All commands use the `/ouat` prefix.

### Player Commands

| Command | Description |
|---------|-------------|
| `/ouat check <player>` | Shows full supernatural data: role, alignment, proficiency, spells, curses, blessings, artifacts, and story progression |
| `/ouat learn <spell>` | Learn a new spell (must meet proficiency requirement) |
| `/ouat cast <spell> [argument]` | Cast a known spell |
| `/ouat spells` | List all available spells with proficiency requirements |
| `/ouat list` | List your currently learned spells |

### Role Commands

| Command | Description |
|---------|-------------|
| `/ouat role check [player]` | Check a player's current role |
| `/ouat role clear [player]` | Clear a player's role |

### Magic Commands

| Command | Description |
|---------|-------------|
| `/ouat magic check [player]` | Check magic proficiency, alignment, and spell count |
| `/ouat magic set [player]` | Set magic proficiency to 50 |

### Debug Commands

| Command | Description |
|---------|-------------|
| `/ouat data <player>` | View full supernatural data (alias for check) |
| `/ouat debug registry` | View the Unique Role Registry (who holds each unique role) |

---

## Building from Source

### Prerequisites

- Java 21 (Eclipse Adoptium recommended)
- Git

### Build Steps

```bash
# Clone the repository
git clone https://github.com/THTStreamer/OnceUponaTime.git
cd OnceUponaTime

# Set Java home (Windows)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"

# Build
./gradlew.bat build
```

The built jar will be at `build/libs/onceuponatime-1.0.0.jar`.

### Deploy to CurseForge Instance

```powershell
Copy-Item "build\libs\onceuponatime-1.0.0.jar" "C:\Users\<you>\curseforge\minecraft\Instances\<Instance>\mods\onceuponatime-1.0.0.jar"
```

---

## Magic Comes with a Price

Every spell costs **food points** when cast in Survival mode. The cost scales with your per-spell proficiency — the better you know a spell, the less food it costs:

| Tier | Proficiency | Food Cost | Example |
|------|------------|-----------|---------|
| Novice | 0–20 | 100% base cost | Healing Light (6) |
| Apprentice | 21–40 | 90% base cost | Healing Light (5) |
| Adept | 41–60 | 80% base cost | Healing Light (5) |
| Expert | 61–80 | 70% base cost | Healing Light (4) |
| Master | 81–100 | 60% base cost | Healing Light (4) |

**Creative Mode players cast for free** — the food cost check is skipped entirely.

---

## License

This mod is a fan project inspired by the *Once Upon a Time* TV series. All rights to the original show belong to ABC Studios and their respective owners.
