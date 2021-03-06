/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.upa.impl.bulk.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.upa.PortabilityHint;

/**
 * @author Taha BEN SALAH <taha.bensalah@gmail.com>
 */
@PortabilityHint(target = "C#", name = "ignore")
public class StackBlockingSAXHandler<E> implements ContentHandler, StackBlockingSAXReader<E> {

    private static final Logger log = Logger.getLogger(StackBlockingSAXHandler.class.getName());
    Stack<GenericXmlNode> stack = new Stack<GenericXmlNode>();
    boolean someNext = true;
    private BlockingQueue<BlockingVal> queue;
    private StackBlockingSAXProcessor processor;
    private Thread thread;

    public StackBlockingSAXHandler(int capacity, StackBlockingSAXProcessor processor) {
        queue = new ArrayBlockingQueue<BlockingVal>(capacity);
        this.processor = processor;
    }

    private BlockingQueue<BlockingVal> getQueue() {
        return queue;
    }

    public void setDocumentLocator(Locator locator) {
        //
    }

    public void startDocument() throws SAXException {
        ///_mutex.lock();
    }

    public void endDocument() throws SAXException {
        try {
            putEOF();
            ///_mutex.unlock();
        } catch (InterruptedException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        GenericXmlNode n = new GenericXmlNode();
        n.name = qName;
        n.properties = new LinkedHashMap<String, String>();
        for (int i = 0; i < atts.getLength(); i++) {
            n.properties.put(atts.getQName(i), atts.getValue(i));

        }
        stack.push(n);
        processor.startElement(n, this);
    }

    public void startElement(GenericXmlNode n) {
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        processor.endElement(stack.peek(), this);
        stack.pop();
    }

    public GenericXmlNode peek(int x) {
        return stack.get(stack.size() - 1 - x);
    }

    public boolean isTopPath(String... elems) {
        for (int i = 0; i < elems.length; i++) {
            if (!peek(i).name.equals(elems[elems.length - i - 1])) {
                return false;
            }
        }
        return true;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        GenericXmlNode n = stack.peek();
        if (n.content == null) {
            n.content = new ArrayList<String>();
        }
        List<String> content = n.content;

        content.add(new String(ch, start, length));
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    private E unbox(BlockingVal v) throws InterruptedException, EOFException, IOException {
        if (v == null) {
            return null;
        }
        switch (v.blockingValType) {
            case BlockingVal.TYPE_EOF: {
                throw new EOFException();
            }
            case BlockingVal.TYPE_THROWABLE: {
                Throwable t = (Throwable) (v.getValue());
                if (t instanceof InterruptedException) {
                    throw (InterruptedException) t;
                }
                if (t instanceof IOException) {
                    throw (IOException) t;
                }
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                throw new RuntimeException(t);
            }
        }
        return (E) v.value;
    }

    public E take() throws InterruptedException, EOFException, IOException {
        BlockingVal v = getQueue().take();
        return unbox(v);
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException, EOFException, IOException {
        return unbox(getQueue().poll(timeout, unit));
    }

    public E remove() throws EOFException, IOException {
        try {
            return unbox(getQueue().remove());
        } catch (InterruptedException ex) {
            //never happens
            throw new RuntimeException();
        } catch (EOFException ex) {
            //never happens
            throw new NoSuchElementException();
        }
    }

    public E poll() throws IOException {
        try {
            return unbox(getQueue().poll());
        } catch (InterruptedException ex) {
            //never happens
            throw new RuntimeException();
        } catch (EOFException ex) {
            //never happens
            return null;
        }
    }

    public List<E> takeList() throws InterruptedException, EOFException, IOException {
        List<E> r = new ArrayList<E>();
        while (true) {
            try {
                E t = take();
                r.add(t);
            } catch (EOFException ex) {
//                log.log(Level.SEVERE, null, ex);
                break;
            }
        }
        return r;
    }

    public void putEOF() throws InterruptedException {
        getQueue().put(new BlockingVal(BlockingVal.TYPE_EOF, null));
    }

    public void putThrowable(Throwable throwable) throws InterruptedException {
        getQueue().put(new BlockingVal(BlockingVal.TYPE_THROWABLE, throwable));
    }

    public void putValue(E e) throws InterruptedException {
        getQueue().put(new BlockingVal(BlockingVal.TYPE_VALUE, e));
    }

    public void addValue(E e) {
        getQueue().add(new BlockingVal(BlockingVal.TYPE_VALUE, e));
    }

    public void offerValue(E e) {
        getQueue().offer(new BlockingVal(BlockingVal.TYPE_VALUE, e));
    }

    public void offerValue(E e, long timeout, TimeUnit unit) throws InterruptedException {
        getQueue().offer(new BlockingVal(BlockingVal.TYPE_VALUE, e), timeout, unit);
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void dispose() {
        if (thread != null) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
    }

}
