/*
 * MIT License
 *
 * Copyright (c) 2020 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.andavin.images.legacy.image;

import com.andavin.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ItemFrame;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @since February 08, 2018
 * @author Andavin
 */
@SerializableAs("com.andavin.images.image.Image")
public final class LegacyImage implements ConfigurationSerializable {

    public static File dataFolder;
    private final File imageFile;
    private final Location location;
    private final Map<Location, LegacyImageSection> sections;

    public LegacyImage(Location location, File imageFile) {
        this.location = location;
        this.imageFile = imageFile;
        this.sections = new HashMap<>();
    }

    public LegacyImage(Map<String, Object> map) {
        this.location = (Location) map.get("location");
        this.sections = new HashMap((Map<Location, LegacyImageSection>) map.get("sections"));
        this.imageFile = new File(dataFolder, (String) map.get("file"));
    }

    /**
     * Get the start location of this image.
     *
     * @return The location.
     */
    public Location getLocation() {
        return this.location.clone();
    }

    /**
     * Get the file that stores the actual image
     * for this object.
     *
     * @return The image file.
     */
    public File getImageFile() {
        return this.imageFile;
    }

    /**
     * Add a new section to this image with the given ID and at
     * relative coordinates and the location given.
     *
     * @param id The ID of the new section and its map.
     * @param x The relative X coordinate.
     * @param y The relative Y coordinate.
     * @param loc The full location of the section.
     */
    public void addSection(short id, int x, int y, Location loc) {
        this.sections.put(loc, new LegacyImageSection(id, x, y));
    }

    /**
     * Get all of the {@link LegacyImageSection}s of this image.
     *
     * @return The sections of this image.
     */
    public Collection<LegacyImageSection> getSections() {
        return this.sections.values();
    }

    public Map<Location, LegacyImageSection> getImageSections() {
        return sections;
    }

    /**
     * Destroy the physical blocks of this image.
     */
    public void destroy() {

        this.sections.forEach((loc, section) -> Scheduler.syncRegionized(() -> loc.getWorld().getNearbyEntities(loc, 1, 1, 1).forEach(entity -> {
            if (entity instanceof ItemFrame) {

                Location loc1 = entity.getLocation();
                if (loc1.getBlockX() == loc.getBlockX() && loc1.getBlockY() == loc.getBlockY() && loc1.getBlockZ() == loc.getBlockZ()) {
                    entity.remove();
                }
            }
        }), loc));
    }

    /**
     * Get a section of this image by it's world {@link Location}.
     *
     * @param loc The location of the section.
     * @return The {@link LegacyImageSection} or null if none was found.
     */
    public LegacyImageSection getSection(Location loc) {

        LegacyImageSection section = this.sections.get(loc);
        if (section == null) {
            loc.setX(loc.getBlockX());
            loc.setY(loc.getBlockY());
            loc.setZ(loc.getBlockZ());
            loc.setYaw(0);
            loc.setPitch(0);
            section = this.sections.get(loc);
        }

        return section;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>(3);
        map.put("location", this.location);
        map.put("sections", this.sections);
        map.put("file", this.imageFile.getName());
        return map;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof LegacyImage)) {
            return false;
        }

        LegacyImage image = (LegacyImage) o;
        return Objects.equals(this.imageFile, image.imageFile) &&
                Objects.equals(this.location, image.location) &&
                Objects.equals(this.sections, image.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.imageFile, this.location, this.sections);
    }
}
