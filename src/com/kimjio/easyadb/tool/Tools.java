package com.kimjio.easyadb.tool;

public interface Tools<Return, Return2, Type> {
    public Return isStringEmpty(String string);
    //EX) "AA", new String[] = {"AA", "BB"}, "EE"
    public Return2 replace(Type type, Type[] targets, Type replace);
}
