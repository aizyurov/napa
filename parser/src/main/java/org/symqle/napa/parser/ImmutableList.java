package org.symqle.napa.parser;

import java.util.AbstractList;
import java.util.Collection;

public class ImmutableList<T> extends AbstractList<T> {

    private Object[] data;

    public ImmutableList() {
        data = new Object[0];
    }

    public ImmutableList(Collection<T> collection) {
        data = new Object[collection.size()];
        int index = 0;
        for (T element: collection) {
            data[index++] = element;
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param index
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public T get(int index) {
        return (T) data[index];
    }

    @Override
    public int size() {
        return data.length;
    }

    /**
     * Returns the hash code value for this list.
     * <p>
     * <p>This implementation uses exactly the code that is used to define the
     * list hash function in the documentation for the {@link List#hashCode}
     * method.
     *
     * @return the hash code value for this list
     */
    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = super.hashCode();
        }
        return hash;
    }

    private int hash = 0;
}
