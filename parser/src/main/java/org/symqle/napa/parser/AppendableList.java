package org.symqle.napa.parser;

import java.util.ArrayList;
import java.util.List;

public class AppendableList<T> {

    private AppendableList<T> parent;
    private T element;

    public static <T> AppendableList<T> empty() {
        return new AppendableList<>(null, null);
    }

    private AppendableList(AppendableList<T> parent, T element) {
        this.parent = parent;
        this.element = element;
    }

    public AppendableList<T> append(T element) {
        return new AppendableList<>(this, element);
    }

    public List<T> asList() {
        List<T> list = new ArrayList<>();
        addElements(list);
        return list;
    }

    private void addElements(List<T> list) {
        if (parent == null) {
            return;
        }
        parent.addElements(list);
        list.add(element);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppendableList<?> that = (AppendableList<?>) o;

        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        return element != null ? element.equals(that.element) : that.element == null;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (element != null ? element.hashCode() : 0);
        return result;
    }
}
