package org.symqle.epic.tokenizer;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author lvovich
 */
public class AsyncTokenizer<T> implements Tokenizer<T> {

    private final Tokenizer<T>  delegate;

    private BlockingQueue<Token<T>> queue = new ArrayBlockingQueue<Token<T>>(1000);

    private boolean eofReached = false;

    private IOException exception = null;

    public AsyncTokenizer(final Tokenizer<T> delegate) {
        this.delegate = delegate;
        new Thread(this::pollTokens).run();
    }

    @Override
    public Token<T> nextToken() throws IOException {
        if (exception != null) {
            throw exception;
        }
        if (eofReached) {
            return null;
        }
        Token<T> token = queue.poll();
        if (token.getType() == null) {
            eofReached = true;
            return null;
        }
        return token;
    }

    private void pollTokens() {
        try {
            for (Token<T> token = delegate.nextToken(); token != null; token = delegate.nextToken()) {
                queue.put(token);
            }
            queue.put(new Token<T>(null, -1, -1, null));
        } catch (IOException e) {
            exception = e;
        } catch (InterruptedException e) {
            queue.add(new Token<T>(null, -1, -1, null));
        }
    }


}
