package com.studyolle;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {
    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";

    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage("..module.study..").should().onlyBeAccessed().byClassesThat().resideInAnyPackage(STUDY, EVENT);
    //스터디 패키지 안에 클래스들은 STUDY와 EVENT패키지의 클래스들만 접근할 수 있다.

    //ACCOUNT패키지 안에 클래스들은 STUDY와 EVENT패키지의 클래스들만 접근할 수 있다.
    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT).should().accessClassesThat().resideInAnyPackage(ACCOUNT, ZONE,TAG);

    //EVENT패키지 안에 클래스들은 어떤 클래스도 접근 할 수 없다.
    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT).should().accessClassesThat().resideInAnyPackage(EVENT,STUDY,ACCOUNT);
    @ArchTest
    ArchRule cycleCheck = slices().matching("com.studyolle.modules.(*)..").should().beFreeOfCycles();

    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.studyolle.modules..").should().onlyBeAccessed().byClassesThat().resideInAnyPackage("com.studyolle.modules..");
}
