package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;


public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private Node root;
    private int size;
    private Node nullNode = new Node();

    public RedBlackTree() {
        this(null);
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Вставляет элемент в дерево.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент в дереве отсутствовал
     */
    @Override
    public boolean add(E value) {
        Node parent = nullNode;
        Node node = root;
        while (node != nullNode && node != null) {
            parent=node;
            if (compare(value, node.value) < 0){
                node = node.left;
            } else {
                if (compare(value, node.value) > 0) {
                    node = node.right;
                } else {
                    return false;
                }
            }
        }
        node = new Node(value, Color.RED);
        node.parent = parent;
        node.left = node.right = nullNode;
        if (parent != nullNode)
        {
            if (compare(value, parent.value) < 0) {
                parent.left = node;
            } else {
                parent.right = node;
            }

        } else {
            root = node;
        }
        balance(node);
        size++;
        return true;
    }

    private void fixRemove(Node node)
    {
        Node tmp;
        while(node != root && node.color == Color.BLACK) {
            if(node == node.parent.left) {
                tmp = node.parent.right;

                if(tmp.color == Color.RED) {
                    tmp.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    leftRotate(node.parent);
                    tmp = node.parent.right;
                }
                if(tmp.left.color == Color.BLACK && tmp.right.color == Color.BLACK) {
                    tmp.color = Color.RED;
                    node = node.parent;
                }
                else {
                    if(tmp.right.color == Color.BLACK) {
                        tmp.left.color = Color.BLACK;
                        tmp.color = Color.RED;
                        rightRotate(tmp);
                        tmp = node.parent.right;
                    }
                    tmp.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    tmp.right.color = Color.BLACK;
                    leftRotate(node.parent);
                    node = root;
                }
            }
            else {
                tmp = node.parent.left;
                if(tmp.color == Color.RED) {
                    tmp.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    rightRotate(node.parent);
                    tmp = node.parent.left;
                }
                if(tmp.left.color == Color.BLACK && tmp.right.color == Color.BLACK) {
                    tmp.color = Color.RED;
                    node = node.parent;
                }
                else {
                    if( tmp.left.color == Color.BLACK) {
                        tmp.right.color = Color.BLACK;
                        tmp.color = Color.RED;;
                        leftRotate(tmp);
                        tmp = node.parent.left;
                    }
                    tmp.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    tmp.left.color = Color.BLACK;

                    rightRotate(node.parent);
                    node = root;
                }
            }
        }
        node.color = Color.BLACK;
    }

    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в дереве
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        Node curr = root;
        while ( curr != nullNode && curr != null)
        {
            if (compare(value, curr.value) < 0) {
                curr = curr.left;
            } else {
                if (compare(value, curr.value) > 0)
                    curr = curr.right;
                else if (compare(value, curr.value) == 0)
                    break;
            }
        }
        if (curr != nullNode && curr!=null && compare(value, curr.value) == 0) {
            Node tmp;
            Node successor;
            if (curr == nullNode) {
                return false;
            }
            if (!curr.hasLeft() || !curr.hasRight()) {
                successor = curr;
            } else {
                successor = curr.getSuccess();
            }

            if (successor.hasLeft()) {
                tmp = successor.left;
            } else {
                tmp = successor.right;
            }

            tmp.parent = successor.parent;
            if (!successor.hasParent()) {
                root = tmp;
            } else {
                if (successor == successor.parent.left) {
                    successor.parent.left = tmp;
                } else {
                    successor.parent.right = tmp;
                }
            }
            if (successor != curr) {
                curr.value = (E) successor.value;
            }

            if (successor.color == Color.BLACK) {
                fixRemove(tmp);
            }
            size--;
            return true;

        }
        return false;
    }


    /**
     * Ищет элемент с таким же значением в дереве.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в дереве
     */
    @Override
    public boolean contains(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        Node curr = root;
        while (curr != nullNode && curr != null) {
            if (compare(curr.value, value)== 0) {
                return true;
            }
            if (compare(value, curr.value) < 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
        }
        return false;
    }

    /**
     * Ищет наименьший элемент в дереве
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (root == null || root == nullNode) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while (curr.hasLeft()){
            curr = curr.left;
        }
        return curr.value;
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (root == null || root == nullNode) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while (curr.hasRight()) {
            curr = curr.right;
        }
        return  curr.value;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "RBTree{" +
                "size=" + size + ", " +
                "tree=" + root +
                '}';
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        throw new UnsupportedOperationException("subSet");
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        throw new UnsupportedOperationException("headSet");
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        throw new UnsupportedOperationException("tailSet");
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("iterator");
    }

    /**
     * Обходит дерево и проверяет выполнение свойств сбалансированного красно-чёрного дерева
     * <p>
     * 1) Корень всегда чёрный.
     * 2) Если узел красный, то его потомки должны быть чёрными (обратное не всегда верно)
     * 3) Все пути от узла до листьев содержат одинаковое количество чёрных узлов (чёрная высота)
     *
     * @throws NotBalancedTreeException если какое-либо свойство невыполнено
     */
    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        if (root != null) {
            if (root.color != Color.BLACK) {
                throw new NotBalancedTreeException("Root must be black");
            }
            traverseTreeAndCheckBalanced(root);
        }
    }

    private Node leftRotate(Node node)
    {
        Node right = node.right;
        node.right = right.left;

        if (right.left != nullNode)
            right.left.parent = node;

        if (right != nullNode)
            right.parent = node.parent;

        if (node.parent != nullNode)
        {
            if (node == node.parent.left)
                node.parent.left = right;
            else
                node.parent.right = right;

        } else
            root = right;
        right.left = node;
        if (node != nullNode)
            node.parent = right;
        return root;
    }


    private Node rightRotate(Node node)
    {
        Node left = node.left;
        node.left = left.right;
        if (left.right != nullNode)
            left.right.parent = node;
        if (left != nullNode)
            left.parent = node.parent;
        if (node.parent != nullNode)
        {
            if (node == node.parent.right)
                node.parent.right = left;
            else
                node.parent.left = left;

        } else
            root = left;
        left.right = node;
        if (node != nullNode)
            node.parent = left;
        return root;
    }

    private void balance(Node node)
    {
        Node uncle;
        while (node != root && node.parent.color == Color.RED)
        {
            if (node.parent == node.parent.parent.left)
            {
                uncle = node.parent.parent.right;
                if (uncle.color == Color.RED)
                {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right)
                    {
                        node = node.parent;
                        root = leftRotate(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    root = rightRotate(node.parent.parent);
                }
            } else
            {
                uncle = node.parent.parent.left;
                if (uncle.color == Color.RED)
                {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left)
                    {
                        node = node.parent;
                        root = rightRotate(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    root = leftRotate(node.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
    }

    private int traverseTreeAndCheckBalanced(Node node) throws NotBalancedTreeException {
        if (node == null) {
            return 1;
        }
        int leftBlackHeight = traverseTreeAndCheckBalanced(node.left);
        int rightBlackHeight = traverseTreeAndCheckBalanced(node.right);
        if (leftBlackHeight != rightBlackHeight) {
            throw NotBalancedTreeException.create("Not balanced", leftBlackHeight, rightBlackHeight, node.toString());
        }
        if (node.color == Color.RED) {
            checkRedNodeRule(node);
            return leftBlackHeight;
        }
        return leftBlackHeight + 1;
    }

    private void checkRedNodeRule(Node node) throws NotBalancedTreeException {
        if (node.left != null && node.left.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then left child must be black.\n" + node.toString());
        }
        if (node.right != null && node.right.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then right child must be black.\n" + node.toString());
        }
    }

    enum Color {
        RED, BLACK
    }

    public class Node {
        E value;
        Node left;
        Node right;
        Node parent;
        Color color;

        public Node(){
            this.value = null;
            this.color = Color.BLACK;
        }
        public Node(E value, Color color)
        {
            this.value = value;
            this.color = color;
        }


        public boolean hasLeft() {
            return left != null && left != nullNode;
        }

        public boolean hasRight() {
            return right != null && right != nullNode;
        }

        public boolean hasParent() {
            return parent != null && parent != nullNode;
        }

        public Node getSuccess()
        {
            Node tmp;
            Node node = this;
            if(node.hasRight()) {
                tmp = node.right;
                while(tmp.hasLeft())
                    tmp = tmp.left;
                return tmp;
            }
            tmp = node.parent;
            while(tmp != nullNode && node == tmp.right) {
                node = tmp;
                tmp = tmp.parent;
            }
            return tmp;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", left=" + left +
                    ", right=" + right +
                    ", color=" + color +
                    '}';
        }
    }
}