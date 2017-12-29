package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.function.Function;


public class AVLTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;

    public Node root; //todo: Создайте новый класс если нужно. Добавьте новые поля, если нужно.
    private int size;
    //todo: добавьте дополнительные переменные и/или методы если нужно

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }



   public int height(Node tmp){
       return (tmp==null)?  0 : tmp.height;
   }

   public int balansedValue(Node tmp) {
       return height(tmp.right) - height(tmp.left);
   }

   public void fixHeight(Node tmp){
       int first=tmp.right==null?0:tmp.right.height;
       int second=tmp.left==null?0:tmp.left.height;
       tmp.height=1+Math.max(first,second);
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

        if (root == null) {
            root = new Node(value,null);
            size++;
            return true;
        }

        if(contains(value)){
            return false;
        } else {

            Node r=root;
            boolean insert=false;
            while(!insert) {
                if (compare(value, r.value) < 0) {
                    if (r.left == null) {
                        r.left = new Node(value,r);
                        insert=true;
                    } else {
                        r = r.left;
                    }
                } else {
                    if (r.right == null) {
                        r.right = new Node(value,r);
                        insert=true;
                    } else {
                        r = r.right;
                    }
                }
            }
            while (r.parent!=null){
                r=balance(r);
                if(r.parent!=null) {
                    r = r.parent;
                }
            }
            size++;
            return true;
        }
    }

    private Node rotateLeft(Node node) {
        Node tmp = node.right;
        node.right=node.left;
        tmp.left=node;
        fixHeight(node);
        fixHeight(tmp);
        return tmp;
    }

    private Node rotateRight(Node node) {
        Node tmp = node.left;
        node.left=tmp.right;
        tmp.right=node;
        fixHeight(node);
        fixHeight(tmp);
        return tmp;
    }


    public Node balance(Node node){
        if(node==null){
            return null;
        }

        fixHeight(node);
        if(balansedValue(node)==2){
            if(balansedValue(node.right)<0){
                node.right=rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        if(balansedValue(node)==-2){
            if(balansedValue(node.left)>0){
                node.left=rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        return node;
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
        Node tmp = root;
        if(!contains(value)){
            return false;
        }
        while (tmp.right.value!=value && tmp.left.value!=value){
            if(compare(tmp.value,value)<0){
                tmp=tmp.left;
            }else {
                tmp=tmp.right;
            }
        }
        Node left,right;
        if(compare(tmp.right.value,value)==0) {
            left = tmp.right.left;
            right = tmp.right.right;
        } else  {
            left = tmp.left.left;
            right = tmp.left.right;
        }

        if(right==null){
            tmp.right=left;
            size--;
            balance(tmp);
            return true;
        }
        Node z=right;
        while (z.left!=null){
            z=z.left;
        }
        z.left=left;
        tmp.right=right;
        balance(tmp);
        size--;
        return true;
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
        if(isEmpty()){
            return false;
        }
        Node tmp=root;
        while(true){
            if(compare(value,tmp.value)<0){
                if(tmp.left!=null) {
                    tmp = tmp.left;
                } else {
                    return false;
                }
            }
            if(compare(value,tmp.value)>0){
                if(tmp.right!=null) {
                    tmp = tmp.right;
                } else {
                    return false;
                }
            }
            if (compare(value,tmp.value)==0){
                return true;
            }
        }
    }

    /**
     * Ищет наименьший элемент в дереве
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("first");
        }
        Node tmp = root;
        while (tmp.left != null) {
            tmp = tmp.left;
        }
        return tmp.value;
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("last");
        }
        Node tmp = root;
        while (tmp.right != null) {
            tmp = tmp.right;
        }
        return tmp.value;
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
        return "AVLTree{" +
                "tree=" + root +
                "size=" + size + ", " +
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

    public void print(Node z){


       if(z.left!=null){

           print(z.left);
       }
        System.out.print(z.toString());
       if(z.right!=null){
           print(z.right);
       }
    }
    /**
     * Обходит дерево и проверяет что высоты двух поддеревьев
     * различны по высоте не более чем на 1
     *
     * @throws NotBalancedTreeException если высоты отличаются более чем на один
     */
    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        traverseTreeAndCheckBalanced(root);
    }

    private int traverseTreeAndCheckBalanced(Node curr) throws NotBalancedTreeException {
        if (curr == null) {
            return 1;
        }
        int leftHeight = traverseTreeAndCheckBalanced(curr.left);
        int rightHeight = traverseTreeAndCheckBalanced(curr.right);
        if (Math.abs(leftHeight - rightHeight) > 1) {
            throw NotBalancedTreeException.create("The heights of the two child subtrees of any node must be differ by at most one",
                    leftHeight, rightHeight, curr.toString());
        }
        return Math.max(leftHeight, rightHeight) + 1;
    }

    class Node {

        E value;
        Node left;
        Node right;
        int height;
        Node parent;
        Node(E value,Node parent) {
            this.value = value;
            this.right=null;
            this.left=null;
            this.height=1;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Node {");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        AVLTree<Integer> a=new AVLTree<>();
        for (int i=0;i<10;i++){
            a.add(i);
            a.print(a.root);
            System.out.println("\n=============");
        }
    }
}
