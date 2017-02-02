/*                                                                            
 * Copyright 2016 InQBarna Kenkyuu Jo                                         
 *                                                                            
 * Licensed under the Apache License, Version 2.0 (the "License");            
 * you may not use this file except in compliance with the License.           
 * You may obtain a copy of the License at                                    
 *                                                                            
 *     http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                            
 * Unless required by applicable law or agreed to in writing, software        
 * distributed under the License is distributed on an "AS IS" BASIS,          
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   
 * See the License for the specific language governing permissions and        
 * limitations under the License.                                             
 */                                                                           
                                                                              
                                                                              

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
    public static <T> CellNode<T> fromBlock(@NotNull GroupItem<T> block) {
        return fromBlocks(Collections.singletonList(block));
    }

    public static <T> CellNode<T> fromBlocks(@NotNull Collection<? extends GroupItem<T>> blocks) {
        CellNode<T> root = null;

        for (GroupItem<T> block : blocks) {
            root = setNextBlock(root, block);
        }

        return root;
    }


    private static <T> CellNode<T> setNextBlock(CellNode<T> root, @NotNull GroupItem<T> block) {
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

    static <T> CellNode<T> nodeFromBlock(@NotNull GroupItem<T> block) {
        CellNode<T> me = new CellNode<>();
        me.setData(block.dataContent());

        List<? extends GroupItem<T>> childs = block.childBlocks();
        CellNode<T> left = null;
        if (null != childs && !childs.isEmpty()) {
            Queue<GroupItem<T>> rightElementBlocks = new LinkedList<>();
            for (GroupItem<T> childBlock : childs) {
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

    private static <T> void processRightBlocks(CellNode<T> prev, Queue<? extends GroupItem<T>> rightElementBlocks) {
        GroupItem<T> poll = rightElementBlocks.poll();
        if (null != poll) {
            CellNode<T> right = nodeFromBlock(poll);
            prev.right(right);
            processRightBlocks(prev.right(), rightElementBlocks);
        }
    }
}
