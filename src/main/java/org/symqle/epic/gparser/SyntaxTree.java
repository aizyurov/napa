package org.symqle.epic.gparser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by aizyurov on 10/28/17.
 */
public interface SyntaxTree {

    List<String> getPreface();
    String getName();
    String getValue();
    SyntaxTree getParent();
    List<SyntaxTree> getChildren();
    int getLine();
    int getPos();
    String getSource();

    default List<SyntaxTree> find(final String path) {
        final List<String> nameList = path == null || path.equals("")
                ? Collections.<String>emptyList()
                : Arrays.asList(path.split("\\."));
        return find(nameList);
    }

    default List<SyntaxTree> find(List<String> nameList) {
        if (nameList.isEmpty()) {
            return Collections.singletonList(this);
        } else {
            final List<SyntaxTree> result = new ArrayList<SyntaxTree>();
            final String firstName = nameList.get(0);
            final List<String> otherNames = nameList.subList(1, nameList.size());
            if (firstName.equals("^")) {
                return getParent() == null ? Collections.<SyntaxTree>emptyList() : getParent().find(otherNames);
            } else {
                for (SyntaxTree child: getChildren()) {
                    if (child.getName().equals(firstName)) {
                        result.addAll(child.find(otherNames));
                    }
                }
            }
            return result;
        }
    }

    default void print(int offset, Writer writer) throws IOException {
        for (int i=0; i<offset; i++) {
            writer.write("    ");
        }
        print(writer);
        for (SyntaxTree child: getChildren()) {
            child.print(offset + 1, writer);
        }
    }

    void print(Writer writer) throws IOException ;

    default void print(OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        print(0, writer);
        writer.write("\n");
        writer.flush();
    }

}
