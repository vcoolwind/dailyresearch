package com.balckstone.dailyresearch.designpatterns.template;

public class Model<T> {
    private T origin;
    private T dest;

    public Model(T origin) {
        this.origin = origin;
    }

    public T getOrigin() {
        return origin;
    }

    public void setOrigin(T origin) {
        this.origin = origin;
    }

    public T getDest() {
        return dest;
    }

    public void setDest(T dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return origin + "<-->" + dest;
    }
}
