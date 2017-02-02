package com.inqbarna.bintree;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author David García <david.garcia@inqbarna.com>
 * @version 1.0 31/1/17
 */

public class CellNode<T> {
    private boolean     isRoot;
    private CellNode<T> left;
    private CellNode<T> right;
    private T           data;
    private boolean     leftThread;
    private boolean     rightThread;

    public CellNode() {
        this(false);
    }

    public CellNode(boolean isRoot) {
        this.isRoot = isRoot;
        rightThread = leftThread = true;
    }

    public CellNode(CellNode<T> other) {
        left = other.left;
        right = other.right;
        data = other.data;
        leftThread = other.leftThread;
        rightThread = other.rightThread;
        isRoot = other.isRoot;
    }

    @Nullable
    private static <T> CellNode<T> commonAncestor(CellNode<T> left, CellNode<T> right) {
        Set<CellNode<T>> leftParents = new HashSet<>();
        Set<CellNode<T>> rightParents = new HashSet<>();
        CellNode<T> lP = left;
        CellNode<T> rP = right;
        while (true) {
            lP = null != lP ? lP.parent() : null;
            rP = null != rP ? rP.parent() : null;
            if (null == lP && null == rP) {
                return null; // no common parent
            }
            if (null != lP && lP == rP) {
                return lP; // fast path
            }
            if (null != lP) {
                if (rightParents.contains(lP)) {
                    return lP;
                }
                leftParents.add(lP);
            }

            if (null != rP) {
                if (leftParents.contains(rP)) {
                    return rP;
                }
                rightParents.add(rP);
            }
        }
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public boolean isLeaf() {
        return (null == left || leftThread) && (null == right || rightThread);
    }

    public boolean isEmpty() {
        return null == data;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public Iterable<? extends CellNode<T>> inorder() {
        return inorder(false);
    }

    public Iterable<? extends CellNode<T>> inorder(final boolean skipEmpty) {
        return inorder(skipEmpty, null);
    }

    public Iterable<? extends CellNode<T>> inorder(final boolean skipEmpty, final CellNode<T> stopPoint) {
        return new Iterable<CellNode<T>>() {
            @Override
            public Iterator<CellNode<T>> iterator() {
                return new ForwardIterator<>(CellNode.this, skipEmpty, stopPoint);
            }
        };
    }

    public Iterable<? extends CellNode<T>> preorder() {
        return preorder(false);
    }

    public Iterable<? extends CellNode<T>> preorder(final boolean skipEmpty) {
        return preorder(skipEmpty, null);
    }

    public Iterable<? extends CellNode<T>> preorder(final boolean skipEmpty, final CellNode<T> stopPoint) {
        return new Iterable<CellNode<T>>() {
            @Override
            public Iterator<CellNode<T>> iterator() {
                return new PreorderIterator<>(CellNode.this, skipEmpty, stopPoint);
            }
        };
    }

    public Iterable<? extends CellNode<T>> reverseInorder() {
        return reverseInorder(false);
    }

    public Iterable<? extends CellNode<T>> reverseInorder(final boolean skipEmpty) {
        return reverseInorder(skipEmpty, null);
    }

    public Iterable<? extends CellNode<T>> reverseInorder(final boolean skipEmpty, final CellNode<T> stopPoint) {
        return new Iterable<CellNode<T>>() {
            @Override
            public Iterator<CellNode<T>> iterator() {
                return new BackwardIterator<>(CellNode.this, skipEmpty, stopPoint);
            }
        };
    }

    public void left(@NotNull CellNode<T> left) {
        // we want whole input happen before (left insertion)
        CellNode<T> inputRightMost = left.rightmost();
        CellNode<T> inputLeftmost = left.leftmost();

        // 1. current left is null
        // 2. current left is not null, but it's a thread to this predecessor (or parent)
        // 3. current left is not null and it's not thread... new input comes before it

        if (null != this.left) {
            if (!leftThread) {
                CellNode<T> leftRightmost = this.left.rightmost();
                leftRightmost.right = inputLeftmost;
                leftRightmost.rightThread = true;
            }
            inputLeftmost.left = this.left;
            inputLeftmost.leftThread = leftThread;
        }

        this.left = left;
        leftThread = false;
        inputRightMost.rightThread = true;
        inputRightMost.right = this;
    }

    @Nullable
    public CellNode<T> commonParentWith(CellNode<T> other) {
        return commonAncestor(this, other);
    }

    public void right(@NotNull CellNode<T> right) {
        // we want whole input happen after (right insertion)
        CellNode<T> inputRightmost = right.rightmost();
        CellNode<T> inputLeftmost = right.leftmost();

        // 1. current right is null
        // 2. current right is not null, but it's a thread to this successor (or parent)
        // 3. current right is not null and it's not thread... new input comes before
        if (this.right != null) {
            if (!rightThread) {
                final CellNode<T> leftmost = this.right.leftmost();
                leftmost.left = right;
                leftmost.leftThread = true;
            }
            inputRightmost.right = this.right;
            inputRightmost.rightThread = rightThread;
        }

        this.right = right;
        rightThread = false;
        // this becomes root of the input subtree
        inputLeftmost.leftThread = true;
        inputLeftmost.left = this;
    }

    public CellNode<T> rightmost() {
        CellNode<T> current = this;
        while (true) {
            if (null != current.right && !current.rightThread) {
                current = current.right;
            } else {
                return current;
            }
        }
    }

    public CellNode<T> leftmost() {
        CellNode<T> current = this;
        while (true) {
            if (null != current.left && !current.leftThread) {
                current = current.left;
            } else {
                return current;
            }
        }
    }

    public CellNode<T> left() {
        return left;
    }

    public CellNode<T> right() {
        return right;
    }

    public CellNode<T> treeRoot() {
        return treeRoot(null);
    }

    public CellNode<T> treeRoot(CellNode<T> noCrossBoundary) {
        CellNode<T> candidate = this;
        CellNode<T> parent;
        while ((parent = candidate.parent()) != null) {
            if (null != noCrossBoundary && parent == noCrossBoundary) {
                break;
            } else {
                candidate = parent;
            }
        }
        return candidate;
    }

    public CellNode<T> parent() {
        CellNode<T> l = this;
        CellNode<T> r = this;
        CellNode<T> parent = null;
        while (true) {
            if (r.rightThread) {
                parent = r.right;
                if (null == parent || parent.left != this) {
                    parent = l;
                    while (!parent.leftThread) {
                        parent = parent.left;
                    }
                    parent = parent.left;
                }
                return parent;
            } else if (l.leftThread) {
                parent = l.left;
                if (null == parent || parent.right != this) {
                    parent = r;
                    while (!parent.rightThread) {
                        parent = parent.right;
                    }
                    parent = parent.right;
                }
                return parent;
            } else {
                l = l.left;
                r = r.right;
            }
        }
    }

    public List<CellNode<T>> asListInOrder() {
        return executeInOrder(
                new F<T, List<CellNode<T>>>() {
                    @Override
                    public List<CellNode<T>> compute(CellNode<T> node, List<CellNode<T>> prevResult) {
                        prevResult.add(node);
                        return prevResult;
                    }
                }, new ArrayList<CellNode<T>>(50)
        );
    }

    public void inOrder(Visitor<T> visitor) {
        inOrder(visitor, null);
    }

    public void inOrder(final Visitor<T> visitor, CellNode<T> stop) {
        executeInOrder(
                new F<T, Void>() {
                    @Override
                    public Void compute(CellNode<T> node, Void prevResult) {
                        visitor.visit(node);
                        return null;
                    }
                }, null, stop);
    }

    public <R> R executeInOrder(F<T, R> func, R initial) {
        return executeInOrder(func, initial, null);
    }

    public <R> R executeInOrder(F<T, R> func, R initial, CellNode<T> stop) {
        R res = initial;
        CellNode<T> current = this;
        Set<CellNode<T>> visited = new HashSet<>(100);
        while (true) {
            if (null != current.left && !current.leftThread && !visited.contains(current.left())) {
                current = current.left();
                visited.add(current);
            } else {
                if (current == this) {
                    visited.add(current);
                }
                res = func.compute(current, res);
                if (null != stop && current == stop) {
                    return res;
                }
                if (null != current.right) {
                    if (current.rightThread) {
                        visited.add(current.right());
                    }
                    current = current.right();
                } else {
                    return res;
                }
            }
        }
    }

    public int countNonEmpty() {
        return countNonEmpty(null);
    }

    public int countNonEmpty(CellNode<T> upToIncluding) {
        return executeInOrder(
                new F<T, Integer>() {
                    @Override
                    public Integer compute(CellNode<T> node, Integer prevResult) {
                        if (node.isEmpty()) {
                            return prevResult;
                        } else {
                            return prevResult + 1;
                        }
                    }
                }, 0, upToIncluding
        );
    }

    public boolean leftThread() {
        return leftThread;
    }

    public boolean rightThread() {
        return rightThread;
    }

    public interface Visitor<T> {
        void visit(@NotNull CellNode<T> cell);
    }

    public interface F<V, R> {
        R compute(CellNode<V> node, R prevResult);
    }

    public interface PeekNextIterator<T> extends Iterator<T> {
        T peekNext();
    }

    /** Forward inorder iterator */
    private static class ForwardIterator<T> implements PeekNextIterator<CellNode<T>> {
        private CellNode<T>      current;
        private Set<CellNode<T>> visited;
        private boolean          skipEmpty;
        private CellNode<T>      stopPoint; // last one, not visited

        public ForwardIterator(CellNode<T> initial) {
            this(initial, false);
        }

        public ForwardIterator(CellNode<T> initial, boolean skipEmpty) {
            this(initial, skipEmpty, null);
        }

        public ForwardIterator(CellNode<T> initial, boolean skipEmpty, CellNode<T> stopPoint) {
            visited = new HashSet<>(256);
            this.stopPoint = stopPoint;
            this.skipEmpty = skipEmpty;
            if (null != stopPoint && initial == stopPoint) {
                current = null;
            } else {
                advanceToNext(initial);
            }
        }

        private void advanceToNext(@Nullable CellNode<T> initial) {
            if (null != initial) {
                current = initial;
            }

            if (current.left != null && !visited.contains(current.left()) && !current.leftThread) {
                // go left first
                while (current.left != null && !current.leftThread && !visited.contains(current.left())) {
                    current = current.left();
                    visited.add(current);
                }
            } else if (null == initial) {
                if (current.rightThread) {
                    visited.add(current.right());
                }
                current = current.right();
            } else {
                visited.add(initial);
            }

            if (null != stopPoint && current == stopPoint) {
                current = null;
            }

            if (skipEmpty && null != current && current.data == null) {
                advanceToNext(null);
            }
        }

        @Override
        public boolean hasNext() {
            return null != current;
        }

        @Override
        public CellNode<T> peekNext() {
            return current;
        }

        @Override
        public CellNode<T> next() {
            CellNode<T> retVal = current;
            advanceToNext(null);
            return retVal;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    /** Backward inorder iterator */
    private static class BackwardIterator<T> implements PeekNextIterator<CellNode<T>> {
        private CellNode<T> current;
        private Set<CellNode<T>> visited;
        private boolean skipEmpty;
        private CellNode<T> stopPoint; // last one, not visited

        public BackwardIterator(CellNode<T> node) {
            this(node, false);
        }

        public BackwardIterator(CellNode<T> node, boolean skipEmpty) {
            this(node, skipEmpty, null);
        }

        public BackwardIterator(CellNode<T> node, boolean skipEmpty, CellNode<T> stopPoint) {
            this.skipEmpty = skipEmpty;
            this.stopPoint = stopPoint;
            visited = new HashSet<>(256);
            if (null != stopPoint && stopPoint == node) {
                current = null;
            } else {
                advanceNext(node);
            }
        }

        @Override
        public boolean hasNext() {
            return null != current;
        }

        @Override
        public CellNode<T> peekNext() {
            return current;
        }

        @Override
        public CellNode<T> next() {
            CellNode<T> retVal = current;
            advanceNext(null);
            return retVal;
        }

        private void advanceNext(CellNode<T> initial) {
            if (null != initial) {
                current = initial;
            }
            if (current.right != null && !current.rightThread && !visited.contains(current.right())) {
                while (current.right != null && !current.rightThread && !visited.contains(current.right())) {
                    current = current.right();
                    visited.add(current);
                }
            } else if (null == initial) {
                if (current.leftThread) {
                    visited.add(current.left());
                }
                current = current.left();
                if (null != stopPoint && current == stopPoint) {
                    current = null;
                }
                if (null != current && current.right != null && !current.rightThread && !visited.contains(current.right())) {
                    while (current.right != null && !current.rightThread && !visited.contains(current.right())) {
                        current = current.right();
                        visited.add(current);
                    }
                }
            } else {
                visited.add(current);
            }

            if (null != stopPoint && current == stopPoint) {
                current = null;
            }

            if (skipEmpty && null != current && null == current.data) {
                advanceNext(null);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    private static class PreorderIterator<T> implements Iterator<CellNode<T>> {
        private CellNode<T> current;
        private boolean skipEmpty;
        private CellNode<T> stopPoint; // last one, not visited
        private Stack<CellNode<T>> parents;

        public PreorderIterator(CellNode<T> initial) {
            this(initial, false);
        }

        public PreorderIterator(CellNode<T> initial, boolean skipEmpty) {
            this(initial, skipEmpty, null);
        }

        public PreorderIterator(CellNode<T> initial, boolean skipEmpty, CellNode<T> stopPoint) {
            this.skipEmpty = skipEmpty;
            this.stopPoint = stopPoint;
            parents = new Stack<>();

            if (null != stopPoint && stopPoint == initial) {
                current = null;
            } else {
                advanceNext(initial);
            }
        }

        private void advanceNext(CellNode<T> initial) {
            if (null != initial) {
                current = initial;
                parents.push(current);
            } else {
                if (current.left != null && !current.leftThread) {
                    current = current.left;
                    parents.push(current);
                } else {
                    do {
                        current = parents.pop();
                    } while ((current.right == null || current.rightThread) && !parents.isEmpty());

                    if (current.right != null && !current.rightThread) {
                        current = current.right;
                        parents.push(current);
                    } else {
                        current = null;
                    }
                }
            }

            if (null != stopPoint && current == stopPoint) {
                current = null;
            }

            if (skipEmpty && null != current && null == current.data) {
                advanceNext(null);
            }
        }

        @Override
        public boolean hasNext() {
            return null != current;
        }

        @Override
        public CellNode<T> next() {
            CellNode<T> retVal = current;
            advanceNext(null);
            return retVal;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet");
        }
    }
}
