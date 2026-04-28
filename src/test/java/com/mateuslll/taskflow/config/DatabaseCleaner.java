package com.mateuslll.taskflow.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Transactional
    public void truncateTables() {
        if (tableNames == null) {
            tableNames = extractTableNames();
        }

        entityManager.flush();
        entityManager.createNativeQuery("SET session_replication_role = 'replica'").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " CASCADE").executeUpdate();
        }

        entityManager.createNativeQuery("SET session_replication_role = 'origin'").executeUpdate();
    }


    private List<String> extractTableNames() {
        List<String> names = new ArrayList<>();

        for (EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
            Class<?> javaType = entity.getJavaType();
            Table tableAnnotation = javaType.getAnnotation(Table.class);

            if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
                names.add(tableAnnotation.name());
            } else {
                names.add(convertCamelCaseToSnakeCase(javaType.getSimpleName()));
            }
        }

        return names;
    }

    private String convertCamelCaseToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
