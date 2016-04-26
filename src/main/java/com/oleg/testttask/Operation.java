package com.oleg.testttask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public class Operation implements BinaryOperator<Integer>{
    private final Type type;

    public Operation(Type type) {
        this.type = type;
    }


    @Override
    public Integer apply(Integer value, Integer value2) {
        switch (type) {
            case PLUS:
                return value + value2;
            case MINUS:
                return value - value2;
            case TIMES:
                return value * value2;
            case DIVIDE:
                return value / value2;
        }
        return null;
    }

    public Type getType() {
        return type;
    }


    public enum Type{
        PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/");

        private final String shortName;

        Type(String name) {
            this.shortName = name;
        }

        public String getShortName() {
            return shortName;
        }


        private static final Map<String, Type> namesMap = new HashMap<>();
        static{
            for (Type type : Type.values()){
                namesMap.put(type.shortName, type);
            }
        }
        public static Type findByShortName(String shortName){
            return namesMap.get(shortName);
        }

    }

}
