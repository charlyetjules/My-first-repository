package com.aether.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** Native Minecraft recreation of the Aether HTML mockup. UI only: toggles do not change gameplay. */
public class AetherScreen extends Screen {
    private static final int BG = 0xE60A0A0C, PANEL = 0xD0141419, CARD = 0xB01E1E28, HEADER = 0x20FFFFFF;
    private static final int PURPLE = 0xFF7D4FFF, CYAN = 0xFF00D4FF, MUTED = 0xFF8B8B9E, WHITE = 0xFFFFFFFF, GREEN = 0xFF00FF88;
    private static final int RED = 0xFFFF4444, LIME = 0xFF44FF44, BLUE = 0xFF6677FF;
    private static final String[] TABS = {"Home", "Modules", "Settings", "Cosmetics"};

    // module state persists while the game runs
    private static final List<Module> HOME = new ArrayList<>();
    private static final List<Module> EXTRA = new ArrayList<>();
    static {
        HOME.add(new Module("KillAura", "Attacks entities in range.", "Combat", RED, true));
        HOME.add(new Module("Velocity", "Reduces knockback.", "Combat", RED, true));
        HOME.add(new Module("AutoArmor", "Equips best armor.", "Combat", RED, false));
        HOME.add(new Module("Speed", "Increases movement speed.", "Movement", LIME, true));
        HOME.add(new Module("Flight", "Fly in survival mode.", "Movement", LIME, false));
        HOME.add(new Module("ESP", "Highlights nearby entities.", "Render", BLUE, true));
        HOME.add(new Module("FullBright", "Maximizes light levels.", "Render", BLUE, false));
        HOME.add(new Module("NoFall", "Prevents fall damage.", "Utility", LIME, false));
        EXTRA.add(new Module("BowAimbot", "Aims your bow.", "Combat", RED, false));
        EXTRA.add(new Module("ChestStealer", "Loots chests.", "Utility", LIME, false));
    }

    private int tab = 0;

    public AetherScreen() { super(Text.literal("Aether")); }

    @Override public boolean shouldPause() { return false; }

    // ---- layout helpers ----
    private int left() { return 20; }
    private int top() { return 20; }
    private int h() { return height - 40; }
    private int leftW() { return 130; }
    private int rightW() { return 140; }
    private int centerX() { return left() + leftW() + 8; }
    private int centerW() { return width - 40 - leftW() - rightW() - 16; }
    private int rightX() { return centerX() + centerW() + 8; }

    private int cardW() { return 112; }
    private int cardH() { return 58; }

    private List<Module> current() { return tab == 0 ? HOME : tab == 1 ? EXTRA : List.of(); }

    private int[] cardRect(int i) {
        int cols = Math.max(1, (centerW() - 16) / (cardW() + 8));
        int x = centerX() + 8 + (i % cols) * (cardW() + 8);
        int y = top() + 56 + (i / cols) * (cardH() + 8);
        return new int[]{x, y, cardW(), cardH()};
    }

    private boolean hit(double mx, double my, int x, int y, int w, int hh) {
        return mx >= x && mx < x + w && my >= y && my < y + hh;
    }

    // ---- render ----
    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        c.fill(0, 0, width, height, BG);

        // panels
        c.fill(left(), top(), left() + leftW(), top() + h(), PANEL);
        c.fill(centerX(), top(), centerX() + centerW(), top() + h(), PANEL);
        c.fill(rightX(), top(), rightX() + rightW(), top() + h(), PANEL);

        // left: logo + nav
        c.fill(left(), top(), left() + leftW(), top() + 28, HEADER);
        c.drawText(textRenderer, "AETHER", left() + 10, top() + 10, PURPLE, true);
        c.drawText(textRenderer, "v2.4.1", left() + leftW() - 38, top() + 10, MUTED, false);
        for (int i = 0; i < TABS.length; i++) {
            int y = top() + 38 + i * 24;
            boolean active = i == tab, hover = hit(mx, my, left() + 6, y, leftW() - 12, 20);
            if (active) { c.fill(left() + 6, y, left() + leftW() - 6, y + 20, 0x407D4FFF); c.fill(left() + 6, y, left() + 8, y + 20, PURPLE); }
            else if (hover) c.fill(left() + 6, y, left() + leftW() - 6, y + 20, 0x20FFFFFF);
            c.drawText(textRenderer, TABS[i], left() + 16, y + 6, active ? PURPLE : (hover ? WHITE : MUTED), false);
        }
        int py = top() + h() - 40;
        c.fill(left() + 8, py, left() + leftW() - 8, py + 32, 0x207D4FFF);
        c.drawText(textRenderer, "Premium Account", left() + 14, py + 6, PURPLE, false);
        c.drawText(textRenderer, "Expires in 24 days", left() + 14, py + 18, MUTED, false);

        // center
        c.fill(centerX(), top(), centerX() + centerW(), top() + 28, HEADER);
        c.drawText(textRenderer, TABS[tab], centerX() + 10, top() + 10, WHITE, true);
        c.drawText(textRenderer, tab == 0 ? "Quick Access" : tab == 1 ? "All Modules" : TABS[tab], centerX() + 10, top() + 38, WHITE, false);
        c.fill(centerX() + 10, top() + 50, centerX() + centerW() - 10, top() + 51, 0x20FFFFFF);

        List<Module> mods = current();
        for (int i = 0; i < mods.size(); i++) drawCard(c, mods.get(i), cardRect(i), mx, my);
        if (tab == 2) c.drawText(textRenderer, "Language: English    Accent: Purple", centerX() + 10, top() + 62, MUTED, false);
        if (tab == 3) c.drawText(textRenderer, "Particle Style: Sparkles    Kill Effect: Explosion", centerX() + 10, top() + 62, MUTED, false);

        // right: info
        c.fill(rightX(), top(), rightX() + rightW(), top() + 28, HEADER);
        c.drawText(textRenderer, "Info", rightX() + 10, top() + 10, WHITE, true);
        MinecraftClient mc = MinecraftClient.getInstance();
        String name = mc.player != null ? mc.player.getName().getString() : "Steve";
        String chunk = mc.player != null ? mc.player.getChunkPos().x + " / " + mc.player.getChunkPos().z : "-";
        stat(c, "Player", name, 40);
        stat(c, "FPS", String.valueOf(mc.getCurrentFps()), 58);
        stat(c, "Chunk X/Z", chunk, 76);

        c.drawText(textRenderer, "Active Mods", rightX() + 10, top() + h() - 70, MUTED, false);
        int ay = top() + h() - 56;
        for (Module m : HOME) {
            if (!m.enabled || ay > top() + h() - 12) continue;
            c.drawText(textRenderer, m.name, rightX() + 10, ay, m.tagColor, false);
            ay += 10;
        }

        // status bar
        c.fill(0, height - 14, width, height, 0xE60A0A0C);
        c.drawText(textRenderer, "Aether Visual Client v2.4.1", 8, height - 11, MUTED, false);
        String f = "FPS: " + mc.getCurrentFps();
        c.drawText(textRenderer, f, width - 8 - textRenderer.getWidth(f), height - 11, GREEN, false);

        super.render(c, mx, my, delta);
    }

    private void stat(DrawContext c, String label, String value, int dy) {
        c.drawText(textRenderer, label, rightX() + 10, top() + dy, MUTED, false);
        c.drawText(textRenderer, value, rightX() + rightW() - 10 - textRenderer.getWidth(value), top() + dy, CYAN, false);
    }

    private void drawCard(DrawContext c, Module m, int[] r, int mx, int my) {
        boolean hover = hit(mx, my, r[0], r[1], r[2], r[3]);
        c.fill(r[0], r[1], r[0] + r[2], r[1] + r[3], CARD);
        if (hover) { // glow border
            c.fill(r[0], r[1], r[0] + r[2], r[1] + 1, PURPLE); c.fill(r[0], r[1] + r[3] - 1, r[0] + r[2], r[1] + r[3], PURPLE);
            c.fill(r[0], r[1], r[0] + 1, r[1] + r[3], PURPLE); c.fill(r[0] + r[2] - 1, r[1], r[0] + r[2], r[1] + r[3], PURPLE);
        }
        c.drawText(textRenderer, m.name, r[0] + 6, r[1] + 6, WHITE, false);
        int tx = r[0] + r[2] - 28, ty = r[1] + 6; // toggle switch
        c.fill(tx, ty, tx + 22, ty + 10, m.enabled ? PURPLE : 0x40FFFFFF);
        int kx = m.enabled ? tx + 12 : tx + 1;
        c.fill(kx, ty + 1, kx + 9, ty + 9, WHITE);
        c.drawText(textRenderer, textRenderer.trimToWidth(m.desc, r[2] - 12), r[0] + 6, r[1] + 24, MUTED, false);
        c.drawText(textRenderer, m.tag, r[0] + 6, r[1] + 42, m.tagColor, false);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (int i = 0; i < TABS.length; i++) {
            if (hit(mx, my, left() + 6, top() + 38 + i * 24, leftW() - 12, 20)) { tab = i; return true; }
        }
        List<Module> mods = current();
        for (int i = 0; i < mods.size(); i++) {
            int[] r = cardRect(i);
            if (hit(mx, my, r[0], r[1], r[2], r[3])) { mods.get(i).enabled = !mods.get(i).enabled; return true; }
        }
        return super.mouseClicked(mx, my, button);
    }
}
