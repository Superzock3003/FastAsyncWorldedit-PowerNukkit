package com.boydti.fawe.object.brush;

import com.boydti.fawe.config.BBC;
import com.boydti.fawe.object.brush.heightmap.ScalableHeightMap;
import com.boydti.fawe.object.exception.FaweException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.function.pattern.Pattern;
import java.io.IOException;
import java.io.InputStream;

public class HeightBrush implements DoubleActionBrush {

    public final ScalableHeightMap heightMap;
    public final int rotation;
    public final double yscale;
    public final BrushTool tool;

    public HeightBrush(InputStream stream, int rotation, double yscale, BrushTool tool, Clipboard clipboard) {
        this(stream, rotation, yscale, tool, clipboard, ScalableHeightMap.Shape.CONE);
    }

    public HeightBrush(InputStream stream, int rotation, double yscale, BrushTool tool, Clipboard clipboard, ScalableHeightMap.Shape shape) {
        this.rotation = (rotation / 90) % 4;
        this.yscale = yscale;
        if (stream != null) {
            try {
                heightMap = ScalableHeightMap.fromPNG(stream);
            } catch (IOException e) {
                throw new FaweException(BBC.BRUSH_HEIGHT_INVALID);
            }
        } else if (clipboard != null) {
            heightMap = ScalableHeightMap.fromClipboard(clipboard);
        } else {
            heightMap = ScalableHeightMap.fromShape(shape);
        }
        this.tool = tool;
    }

    @Override
    public void build(BrushTool.BrushAction action, EditSession editSession, Vector position, Pattern pattern, double sizeDouble) throws MaxChangedBlocksException {
        int size = (int) sizeDouble;
        Mask mask = tool.getMask();
        if (mask == Masks.alwaysTrue() || mask == Masks.alwaysTrue2D()) {
            mask = null;
        }
        heightMap.setSize(size);
        heightMap.apply(editSession, mask, position, size, rotation, action == BrushTool.BrushAction.PRIMARY ? yscale : -yscale, true, false);
    }
}
