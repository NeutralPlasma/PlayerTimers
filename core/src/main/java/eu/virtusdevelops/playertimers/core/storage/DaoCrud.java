package eu.virtusdevelops.playertimers.core.storage;

import org.checkerframework.checker.calledmethods.qual.EnsuresCalledMethods;

import java.util.List;

public interface DaoCrud <T, I>{

    void init();

    T getById(I id);

    List<T> getAll();

    T save(T t);

    boolean delete(T t);

}
