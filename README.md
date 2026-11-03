# Лаборатори №8 — Mockito ашиглан Unit Test боловсруулах

- **Хичээл:** F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест
- **Оюутны нэр:** Munkhjin
- **Оюутны код:** B232270034

## 1. Ашигласан технологи

| Технологи | Хувилбар |
|---|---|
| Java | 17.0.20.1 |
| Apache Maven | 3.10.0 |
| JUnit Jupiter | 5.10.2 |
| Mockito JUnit Jupiter | 5.11.0 |
| Maven Surefire Plugin | 3.2.5 |

## 2. Төслийн бүтэц

```text
starter/
├── pom.xml
├── README.md
├── .gitignore
├── src/
│   ├── main/java/mn/edu/must/sqat/
│   │   ├── CourseRegistrationService.java
│   │   ├── EmailSender.java
│   │   ├── Student.java
│   │   └── StudentRepository.java
│   └── test/java/mn/edu/must/sqat/
│       └── CourseRegistrationServiceTest.java
└── results/
    ├── mvn-test.txt
    ├── mvn-test-mutant-1.txt
    ├── mvn-test-mutant-2.txt
    └── mvn-test-mutant-3.txt
