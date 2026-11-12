package com.aether.client;

public class Module {
    public final String name, desc, tag;
    public final int tagColor;
    public boolean enabled;

    public Module(String name, String desc, String tag, int tagColor, boolean enabled) {
        this.name = name; this.desc = desc; this.tag = tag; this.tagColor = tagColor; this.enabled = enabled;
    }
}
