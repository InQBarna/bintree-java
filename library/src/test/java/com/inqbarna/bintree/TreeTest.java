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

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author David García <david.garcia@inqbarna.com>
 * @version 1.0 2/2/17
 */
public class TreeTest {

    private static CellNode<Integer> tree;

    @BeforeClass
    public static void setupTree() {
        final GroupItem<Integer> first = new BlockBuilder(1)
                .addChild(new BlockBuilder(2).addChilds(3, 4, 5).build())
                .addChild(new BlockBuilder(6).addChilds(7, 8, 9).build())
                .addChild(10)
                .build();

        final GroupItem<Integer> second = new BlockBuilder(11).build();
        tree = Trees.fromBlocks(Arrays.asList(first, second));
    }

    static class ItemBlock implements GroupItem<Integer> {
        private Integer                  mContent;
        private List<GroupItem<Integer>> mChildren;

        public ItemBlock(Integer content) {
            this(content, Collections.<GroupItem<Integer>>emptyList());
        }

        public ItemBlock(Integer content, List<GroupItem<Integer>> children) {
            mContent = content;
            mChildren = children;
        }

        @Override
        public Integer dataContent() {
            return mContent;
        }

        @Override
        public List<? extends GroupItem<Integer>> childBlocks() {
            return mChildren;
        }
    }

    static class BlockBuilder {
        private List<GroupItem<Integer>> mItems;
        private final Integer            mData;

        public BlockBuilder(Integer data) {
            mData = data;
            mItems = new ArrayList<>();
        }

        public BlockBuilder addChild(GroupItem<Integer> child) {
            mItems.add(child);
            return this;
        }

        public BlockBuilder addChild(int value) {
            return addChild(builder(value).build());
        }

        public BlockBuilder addChilds(int ...values) {
            for (int i : values) {
                addChild(i);
            }
            return this;
        }

        public BlockBuilder addChilds(Collection<? extends Integer> vals) {
            for (Integer i : vals) {
                addChild(i);
            }
            return this;
        }

        public GroupItem<Integer> build() {
            return new ItemBlock(mData, mItems);
        }

        static BlockBuilder builder(int value) {
            return new BlockBuilder(value);
        }
    }

    @Test
    public void preorderWalk() {

        List<Integer> preorderValues = new ArrayList<>();
        for (CellNode<Integer> node : tree.preorder()) {
            preorderValues.add(node.getData());
        }
        assertThat(preorderValues, hasSize(11));
        assertThat(preorderValues, contains(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
    }

    @Test
    public void inorderWalk() {

        List<Integer> preorderValues = new ArrayList<>();
        for (CellNode<Integer> node : tree.inorder()) {
            preorderValues.add(node.getData());
        }
        assertThat(preorderValues, hasSize(11));
        assertThat(preorderValues, contains(3, 4, 5, 2, 7, 8, 9, 6, 10, 1, 11));
    }

    @Test
    public void reverseInorder() {

        List<Integer> preorderValues = new ArrayList<>();
        for (CellNode<Integer> node : tree.reverseInorder()) {
            preorderValues.add(node.getData());
        }
        assertThat(preorderValues, hasSize(11));
        assertThat(preorderValues, contains(11, 1, 10, 6, 9, 8, 7, 2, 5, 4, 3));
    }

    public static Matcher<? super CellNode<Integer>> nodeValue(final int val) {
        return new CustomTypeSafeMatcher<CellNode<Integer>>("Node has value " + val) {
            @Override
            protected boolean matchesSafely(CellNode<Integer> item) {
                Integer nodeVal = item.getData();
                if (null == nodeVal) {
                    return false;
                }
                return nodeVal == val;
            }
        };
    }
}
