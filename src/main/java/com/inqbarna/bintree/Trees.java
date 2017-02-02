package com.inqbarna.bintree;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author David García <david.garcia@inqbarna.com>
 * @version 1.0 31/1/17
 */

public class Trees {
    public static <T> CellNode<T> fromBlock(@NotNull LinearItem<T> block) {
        return fromBlocks(Collections.singletonList(block));
    }

    public static <T> CellNode<T> fromBlocks(@NotNull Collection<? extends LinearItem<T>> blocks) {
        CellNode<T> root = null;

        for (LinearItem<T> block : blocks) {
            root = setNextBlock(root, block);
        }

        return root;
    }


    private static <T> CellNode<T> setNextBlock(CellNode<T> root, @NotNull LinearItem<T> block) {
        return setNextBlock(root, nodeFromBlock(block));
    }

    private static <T> CellNode<T> setNextBlock(CellNode<T> root, CellNode<T> blockSubTree) {
        if (null != blockSubTree.right()) {
            throw new IllegalArgumentException("Given subtree, extends more than one block. Not allowed here");
        }

        if (null == root) {
            // just make the incoming subtree the root then
            root = blockSubTree;
            blockSubTree.setRoot(true);
        } else {
            root.rightmost().right(blockSubTree);
        }
        return root;
    }

    static <T> CellNode<T> nodeFromBlock(@NotNull LinearItem<T> block) {
        CellNode<T> me = new CellNode<>();
        me.setData(block.dataContent());

        List<? extends LinearItem<T>> childs = block.childBlocks();
        CellNode<T> left = null;
        if (null != childs && !childs.isEmpty()) {
            Queue<LinearItem<T>> rightElementBlocks = new LinkedList<>();
            for (LinearItem<T> childBlock : childs) {
                if (null == left) {
                    left = nodeFromBlock(childBlock);
                    me.left(left);
                } else {
                    rightElementBlocks.offer(childBlock);
                }
            }

            processRightBlocks(left, rightElementBlocks);
        }
        return me;
    }

    private static <T> void processRightBlocks(CellNode<T> prev, Queue<? extends LinearItem<T>> rightElementBlocks) {
        LinearItem<T> poll = rightElementBlocks.poll();
        if (null != poll) {
            CellNode<T> right = nodeFromBlock(poll);
            prev.right(right);
            processRightBlocks(prev.right(), rightElementBlocks);
        }
    }
}
