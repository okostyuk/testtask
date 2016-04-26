package com.oleg.testttask;

import java.util.ArrayList;
import java.util.List;

public class Request{
    private final Operation operation;
    private final List<Integer> arguments;

    public Request(Operation operation, List<Integer> params) {
        this.operation = operation;
        this.arguments = params;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<Integer> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(operation.getType().name());
        for (Integer argument : arguments){
            stringBuilder.append(" ").append(argument);
        }
        return stringBuilder.toString();
    }

    public static Request fromString(String text){
        String[] splitted = text.split(" ");
        String operationTypeName = splitted[0];
        Operation.Type type = Operation.Type.valueOf(operationTypeName);
        Operation operation = new Operation(type);
        List<Integer> arguments = new ArrayList<>(splitted.length - 1);
        for (int i = 1; i < splitted.length; i++) {
            arguments.add(Integer.valueOf(splitted[i]));
        }
        return new Request(operation, arguments);
    }
}
