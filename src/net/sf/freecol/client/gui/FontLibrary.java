/**
 *  Copyright (C) 2002-2015   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.client.gui;

import java.awt.Font;
import java.util.logging.Logger;
import net.sf.freecol.common.resources.ResourceManager;

/**
 * Wraps anything Font related.
 * 
 * Changes can be made with <code>FontType</code and <code>FontSize</code><br>
 * <br>
 * FontType Sets the typeface. Choices are: NORMAL, SIMPLE or HEADER.<br>
 * FontSize Sets the font size. Choices are: TINY, SMALLER, SMALL, MEDIUM or BIG
 */
public class FontLibrary {

    private static final Logger logger = Logger.getLogger(FontLibrary.class.getName());

    /**
     * Sets the typeface of the font.
     * 
     * Choices are:
     * <ul>
     * <li>NORMAL -- Sets the typeface to be the normal system typeface.</li>
     * <li>SIMPLE</li>
     * <li>HEADER -- Sets the typeface to be the stylized Blackletter typeface.</li>
     * </ul>
     */
    public static enum FontType {
        NORMAL,
        SIMPLE,
        HEADER
    }

    /**
     * Sets the size of the font. This size is later multiplied by the
     * ScaleFactor, currently set to "1".
     * 
     * Choices are:
     * <ul>
     * <li>TINY sets the font to be 12px</li>
     * <li>SMALLER sets the font to be 16px</li>
     * <li>SMALL sets the font to be 24px</li>
     * <li>MEDIUM sets the font to be 36px</li>
     * <li>BIG sets the font to be 48px</li>
     * </ul>
     */
    public static enum FontSize {
        TINY,
        SMALLER,
        SMALL,
        MEDIUM,
        BIG
    }

    private final float scaleFactor;

    public FontLibrary() {
        this.scaleFactor = 1f;
    }

    public FontLibrary(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    static Font createMainFont(String fontName) {
        if (fontName != null) {
            Font font = Font.decode(fontName);
            if (font != null)
                return font;
            logger.warning("Font not found: " + fontName);
        }
        return ResourceManager.getFont("NormalFont");
    }

    public Font createScaledFont(FontType fontType, FontSize fontSize) {
        return createFont(fontType, fontSize, Font.PLAIN, scaleFactor);
    }

    public Font createScaledFont(FontType fontType, FontSize fontSize, int style) {
        return createFont(fontType, fontSize, style, scaleFactor);
    }

    public static Font createFont(FontType fontType, FontSize fontSize) {
        return createFont(fontType, fontSize, Font.PLAIN, 1f);
    }

    public static Font createFont(FontType fontType, FontSize fontSize, int style) {
        return createFont(fontType, fontSize, style, 1f);
    }

    public static Font createFont(FontType fontType, FontSize fontSize, float scaleFactor) {
        return createFont(fontType, fontSize, Font.PLAIN, scaleFactor);
    }

    public static Font createFont(FontType fontType, FontSize fontSize, int style, float scaleFactor) {
        String fontName;
        switch(fontType) {
            default:
                logger.warning("Unknown FontType");
            case NORMAL:
                fontName = "NormalFont";
                break;
            case SIMPLE:
                fontName = "SimpleFont";
                break;
            case HEADER:
                fontName = "HeaderFont";
        }
        float pixelSize;
        switch(fontSize) {
            default:
                logger.warning("Unknown FontSize");
            case TINY:
                pixelSize = 12f;
                break;
            case SMALLER:
                pixelSize = 16f;
                break;
            case SMALL:
                pixelSize = 24f;
                break;
            case MEDIUM:
                pixelSize = 36f;
                break;
            case BIG:
                pixelSize = 48f;
        }
        float scaledSize = pixelSize*scaleFactor;
        Font font = ResourceManager.getFont(fontName);
        font = font.deriveFont(style, scaledSize);
        return font;
    }

}
