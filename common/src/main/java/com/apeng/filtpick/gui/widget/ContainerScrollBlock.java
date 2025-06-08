package com.apeng.filtpick.gui.widget;

import net.minecraft.util.Mth;

/**
 * Scroll block specialized for containers, i.e., it supports changing its position within a bar using integer row numbers
 * instead of double y-coordinates.
 */
public class ContainerScrollBlock extends ScrollBlock {

    private final int actualRowNum;
    private final int displayedRowNum;
    private final int scrollSpaceInRow;

    private int displayedRowOffset = 0;

    /**
     * ScrollBar constructed by this method is inactive if actualRowNum is not bigger than displayedRowNum.
     *
     * @param pX               x position of the scroll bar in its parent
     * @param pY               y position of the scroll bar in its parent
     * @param scrollSlotHeight the height of the slot in which the scroll bar scrolls in pixel.
     * @param displayedRowNum
     * @param actualRowNum
     */
    public ContainerScrollBlock(int pX, int pY, int scrollSlotHeight, int displayedRowNum, int actualRowNum) {
        super(pX, pY, scrollSlotHeight, actualRowNum > displayedRowNum);
        this.displayedRowNum = displayedRowNum;
        this.actualRowNum = actualRowNum;
        this.scrollSpaceInRow = Math.max(actualRowNum - displayedRowNum, 0);
    }

    /**
     * ScrollBar constructed by this method is active by default.
     *
     * @param pX                 x position of the scroll bar in its parent
     * @param pY                 y position of the scroll bar in its parent
     * @param scrollSlotHeight   the height of the slot in which the scroll bar scrolls in pixel.
     * @param displayedRowNum
     * @param actualRowNum
     * @param displayedRowOffset
     */
    public ContainerScrollBlock(int pX, int pY, int scrollSlotHeight, int displayedRowNum, int actualRowNum, int displayedRowOffset) {
        super(pX, pY, scrollSlotHeight);
        this.displayedRowNum = displayedRowNum;
        this.actualRowNum = actualRowNum;
        this.scrollSpaceInRow = Math.max(actualRowNum - displayedRowNum, 0);
        this.displayedRowOffset = displayedRowOffset;
    }

    /**
     * @param pX               x position of the scroll bar in its parent
     * @param pY               y position of the scroll bar in its parent
     * @param scrollSlotHeight the height of the slot in which the scroll bar scrolls in pixel.
     * @param displayedRowNum
     * @param actualRowNum
     * @param active           if scroll block is active
     */
    public ContainerScrollBlock(int pX, int pY, int scrollSlotHeight, int displayedRowNum, int actualRowNum, boolean active) {
        super(pX, pY, scrollSlotHeight, active);
        this.displayedRowNum = displayedRowNum;
        this.actualRowNum = actualRowNum;
        this.scrollSpaceInRow = Math.max(actualRowNum - displayedRowNum, 0);
    }


    /**
     * Set displayed row start index and update the slots render.
     * Should be used for both side to maintain slot consistency.
     *
     * @param displayedRowOffset the row index fot the first row of displayed filtlist
     * @throws IndexOutOfBoundsException if displayedRowOffset out of bound
     */
    public void setDisplayedRowOffsetAndUpdate(int displayedRowOffset) {
        if (!validateDisplayedRowOffset(displayedRowOffset)) {
            throw new IndexOutOfBoundsException(String.format("displayedRowOffset %d out of menu bound", displayedRowOffset));
        }
        this.displayedRowOffset = displayedRowOffset;
    }

    /**
     * @return how much space the list has scrolled by ratio.
     */
    public double getScrollOffsetByRatio() {
        return (double) displayedRowOffset / scrollSpaceInRow;
    }

    /**
     * Safe version of {@link #increaseDisplayedRowOffsetAndUpdate()}. Do nothing if the index is already on bound.
     *
     * @return {@code ture} if {@code displayedRowOffset} is modified.
     */
    public boolean safeIncreaseDisplayedRowOffsetAndUpdate() {
        if (validateDisplayedRowOffset(displayedRowOffset + 1)) {
            increaseDisplayedRowOffsetAndUpdate();
            return true;
        }
        return false;
    }

    /**
     * Safe version of {@link #decreaseDisplayedRowOffsetAndUpdate()}. Do nothing if the index is already on bound.
     *
     * @return {@code ture} if {@code displayedRowOffset} is modified.
     */
    public boolean safeDecreaseDisplayedRowOffsetAndUpdate() {
        if (validateDisplayedRowOffset(displayedRowOffset - 1)) {
            decreaseDisplayedRowOffsetAndUpdate();
            return true;
        }
        return false;
    }

    /**
     * Increase displayedRowOffset by 1 and update the slot render. Remember to check the bound first or an exception may be thrown.
     *
     * @throws IllegalStateException {@code displayedRowOffset} is on the high bound so can not be increased
     * @see #setDisplayedRowOffsetAndUpdate(int)
     */
    public void increaseDisplayedRowOffsetAndUpdate() {
        try {
            setDisplayedRowOffsetAndUpdate(displayedRowOffset + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException(
                    String.format(
                            "Failed to increase displayedRowOffset from %d to %d",
                            displayedRowOffset, displayedRowOffset + 1),
                    e);
        }
    }

    /**
     * Decrease displayedRowOffset by 1 and update the slot render. Remember to check the bound first or an exception may be thrown.
     *
     * @throws IllegalStateException {@code displayedRowOffset} is on the low bound so can not be decreased
     * @see #setDisplayedRowOffsetAndUpdate(int)
     */
    public void decreaseDisplayedRowOffsetAndUpdate() {
        try {
            setDisplayedRowOffsetAndUpdate(displayedRowOffset - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException(
                    String.format(
                            "Failed to decrease displayedRowOffset from %d to %d",
                            displayedRowOffset, displayedRowOffset - 1),
                    e);
        }
    }

    private boolean validateDisplayedRowOffset(int displayedRowOffset) {
        return displayedRowOffset <= scrollSpaceInRow && displayedRowOffset >= 0;
    }

    public int getDisplayedRowOffset() {
        return displayedRowOffset;
    }

    public void setRowOffset(int rowOffset) {
        displayedRowOffset = Mth.clamp(rowOffset, 0, scrollSpaceInRow);
        setPosByRatio((double) displayedRowOffset / scrollSpaceInRow);
    }

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        super.onDrag(pMouseX, pMouseY, pDragX, pDragY);
        displayedRowOffset = (int) (this.getPosRatio() * scrollSpaceInRow);
    }
}
