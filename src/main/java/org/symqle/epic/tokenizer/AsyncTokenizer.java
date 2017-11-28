package org.symqle.epic.tokenizer;

import org.symqle.epic.gparser.GrammarException;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author lvovich
 */
public class AsyncTokenizer<T> implements Tokenizer<T> {

    private final Tokenizer<T>  delegate;

    private BlockingQueue<Token<T>> queue = new ArrayBlockingQueue<Token<T>>(1000);

    private Token<T> eofToken = null;

    private IOException exception = null;

    public AsyncTokenizer(final Tokenizer<T> delegate) {
        this.delegate = delegate;
        new Thread(this::pollTokens).start();
    }

    @Override
    public Token<T> nextToken() throws IOException {
        if (exception != null) {
            throw exception;
        }
        if (eofToken != null) {
            return eofToken;
        }
        Token<T> token = null;
        try {
            token = queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GrammarException("Unexpected interrupt");
        }
        if (token.getType() == null) {
            eofToken = token;
            return token;
        }
        return token;
    }

    private void pollTokens() {
        try {
            while(true) {
                Token<T> token = delegate.nextToken();
                queue.put(token);
                if (token.getType() == null) {
                    break;
                }
            }
        } catch (IOException e) {
            exception = e;
        } catch (InterruptedException e) {
            queue.add(new Token<T>(null, -1, -1, null));
        }
    }


}
