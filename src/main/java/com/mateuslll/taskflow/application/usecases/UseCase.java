package com.mateuslll.taskflow.application.usecases;
public interface UseCase<I, O> {
    
O execute(I input);
}
