package com.pm.integration;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class ArchitectureTest {
    private static JavaClasses classes;

    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter().importPackages("com.pm");
    }

    @Test
    void controllersShouldDependOnServices() {
        ArchRule rule = classes()
            .that().resideInAPackage("..controller..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("..controller..", "..service..", "..dto..", "java..",
                "org.springframework..", "lombok..", "jakarta..", "org.slf4j..")
            .allowEmptyShould(true);
        rule.check(classes);
    }

    @Test
    void servicesShouldNotDependOnControllers() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat()
            .resideInAPackage("..controller..")
            .allowEmptyShould(true);
        rule.check(classes);
    }

    @Test
    void repositoriesShouldBeInFeaturePackages() {
        ArchRule rule = classes()
            .that().haveNameMatching(".*Repository")
            .should().resideInAnyPackage("..activity..", "..comment..", "..issue..", "..project..", "..sprint..", "..user..", "..workflow..", "..auth..");
        rule.check(classes);
    }
}
