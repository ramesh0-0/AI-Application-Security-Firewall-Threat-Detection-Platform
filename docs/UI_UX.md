# AIShield — UI/UX Design Brief

## 1. Visual Design Language & Aesthetics
AIShield embodies a **Cyber-Defense Command Center** aesthetic — high-contrast, sleek, purposeful, and authoritative, adhering strictly to **Material Design 3**.

### Color Palette (Dark Theme Optimized)
- **Canvas / Background:** `#0A0F1D` (Deep Cyber Obsidian)
- **Surface / Card Background:** `#131C31` (Midnight Slate)
- **Surface Variant / Nested:** `#1C2744` (Elevated Deep Navy)
- **Primary Accent (Cyber Shield Blue):** `#00E5FF` (Vibrant Cyan / Electric Glow)
- **Secondary Accent (AI Matrix Indigo):** `#6366F1` (Digital Violet)
- **Success / Safe:** `#10B981` (Emerald Shield)
- **Warning / Medium Risk:** `#F59E0B` (Amber Alert)
- **High Risk / Danger:** `#F97316` (Neon Orange)
- **Critical Risk / Blocked:** `#EF4444` (Crimson Breach)
- **Text Primary:** `#F8FAFC` (High-contrast Pure Light Slate)
- **Text Secondary:** `#94A3B8` (Muted Steel)
- **Border / Outline:** `#223354` (Subtle Cyber Grid Stroke)

### Typography Hierarchy
- **Brand / Header Display:** Sans-Serif Bold Display with crisp character tracking (`Spacings: -0.5sp`)
- **Headings (H1/H2/H3):** Semibold High-contrast Slate (`20sp` - `24sp`)
- **Body & Controls:** Clean M3 Roboto/Sans (`14sp` - `16sp`)
- **Code & Token Inspector:** Monospace Font for payloads, regexes, API keys, and diffs (`13sp`, line height `18sp`)

---

## 2. Reusable Component Design System

1. **`RiskBadge`**: Dynamic color-coded pill with numeric score and categorical label (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) with subtle border glow.
2. **`CyberCard`**: Elevated surface card featuring rounded corners (`16.dp`), subtle border stroke (`1.dp` in `#223354`), and soft tonal elevation.
3. **`MetricStatCard`**: Compact dashboard metric with icon, value, delta badge, and trend indicator.
4. **`ForensicTokenView`**: Monospace formatted inspection box highlighting detected malicious tokens in high-contrast red/amber text highlights.
5. **`VerdictBanner`**: High-visibility status banner displaying firewall action (`REQUEST BLOCKED`, `PAYLOAD SANITIZED`, `ACCESS ALLOWED`) with shield icon and timestamp.
6. **`InteractiveCodeBox`**: Copyable, scrollable code container for sanitized inputs, API payloads, and audit JSON.

---

## 3. Responsive & Adaptive Architecture
- **Mobile Handheld (Compact < 600dp):** Single-column layout, bottom M3 Navigation Bar, collapsible forensic cards, full-width touch targets (minimum 48dp).
- **Tablet / DeX (Expanded > 600dp):** Two-pane List-Detail view for Incidents and Gateway Inspector (Left: Incident feed, Right: Comprehensive Forensics & Token Diff), Navigation Rail.

---

## 4. Accessibility & Polish
- Guaranteed minimum 48dp interactive touch target on all buttons, chips, and toggles (`minimumInteractiveComponentSize()`).
- High-contrast ratio (> 4.5:1) for all text on dark slate surfaces.
- Clear `contentDescription` on all vector icons and action buttons.
- Haptic-friendly visual feedback on all clicks using Material ripple effects.
