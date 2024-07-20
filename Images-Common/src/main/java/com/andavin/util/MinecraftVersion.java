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
package com.andavin.util;

import org.bukkit.Bukkit;

public enum MinecraftVersion {
    v1_20;

    /**
     * The current {@link MinecraftVersion} of this server.
     */
    public static final MinecraftVersion CURRENT;

    static {
        String versionString = Bukkit.getMinecraftVersion();
        try {
            CURRENT = MinecraftVersion.of(versionString);
        } catch (RuntimeException e) {
            throw new UnsupportedOperationException("Version " + versionString + " is not supported.", e);
        }
    }

    private static MinecraftVersion of(String version){
        switch (version){
            case "1.20.6" -> {
                return MinecraftVersion.v1_20;
            }

            default -> throw new RuntimeException("Unsupported version " + version + " !");
        }
    }

    private static final String FULL_VERSION = "v" + Bukkit.getMinecraftVersion().replaceAll("\\.", "_");

    /**
     * Tell if the {@link #CURRENT current server
     * version} is the specified version.
     * <p>
     * For example, if {@code MinecraftVersion.is(v1_8_R3)} is
     * {@code true}, then the current server version is {@code 1.8.8}.
     *
     * @param version The version to test the current version against.
     * @return If the version is the same as the current version.
     */
    public static boolean is(MinecraftVersion version) {
        return CURRENT == version;
    }


    @Override
    public String toString() {
        return FULL_VERSION;
    }
}
