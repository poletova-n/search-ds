package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {
    private static final int INIT_SIZE = 32;
    private int size; //количество элементов в хеш-таблице
    private int tableSize; //размер хещ-таблицы todo: измените на array.length
    private E[] hashTable;
    private boolean del[];

    public OpenHashTable() {
        //todo
        size = 0;
        hashTable =(E[]) new OpenHashTableEntity[INIT_SIZE];
        del = new boolean[INIT_SIZE];
    }

    private void resize() {
        if (2*size < hashTable.length) {
            return;
        }
        E[] tmp = this.hashTable;
        hashTable =(E[]) new OpenHashTableEntity[2*tmp.length];
        del = new boolean[2*tmp.length];
        size = 0;
        for (int i = 0; i < tmp.length; i++) {
            E x =  tmp[i];
            if (x != null) {
                add(x);
            }
        }
    }

    /**
     * Вставляет элемент в хеш-таблицу.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент в хеш-таблице отсутствовал
     */
    @Override
    public boolean add(E value) {
        //todo: следует реализовать
        //Используйте value.hashCode(tableSize, probId) для вычисления хеша
        int probId=0;
        int hash = value.hashCode(hashTable.length, probId);
        if (hashTable[hash] == null) {
            hashTable[hash] = value;
        } else {
            while (hashTable[hash] != null && !value.equals(hashTable[hash]) && !del[hash]) {
                hash = value.hashCode(hashTable.length, ++probId);
            }
            if (value.equals(hashTable[hash])) {
                return false;
            }
            del[hash] = false;
            hashTable[hash] = value;
        }
        size++;
        resize();
        return true;
    }

    /**
     * Удаляет элемент с таким же значением из хеш-таблицы.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в хеш-таблице
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        //todo: следует реализовать
        //Используйте value.hashCode(tableSize, probId) для вычисления хеша
        int probId=0;

        int hash = value.hashCode(hashTable.length, probId);

        if (value.equals(hashTable[hash])) {
            del[hash] = true;
            hashTable[hash] = null;
            size--;
            return true;
        }

        while (hashTable[hash] != null && (!value.equals(hashTable[hash]) || del[hash])) {
            hash = value.hashCode(hashTable.length, ++probId);
        }

        if (value.equals(hashTable[hash])) {
            hashTable[hash] = null;
            del[hash] = true;
            size--;
            return true;
        }

        resize();
        return false;
    }

    /**
     * Ищет элемент с таким же значением в хеш-таблице.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в хеш-таблице
     */
    @Override
    public boolean contains(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        //todo: следует реализовать
        //Используйте value.hashCode(tableSize, probId) для вычисления хеша
        int probId=0;
        int hash = value.hashCode(hashTable.length, probId);
        if (value.equals(hashTable[hash])) {
            return true;
        }
        while (hashTable[hash] != null && !value.equals(hashTable[hash]) || del[hash]) {
            hash = value.hashCode(hashTable.length, ++probId);
        }
        if (hashTable[hash] != null && value.equals(hashTable[hash])) {
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    public int getTableSize() {
        return hashTable.length;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

}
