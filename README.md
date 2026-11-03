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

Хувилбар шалгах команд:

```bash
java -version
mvn -version
```

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
```

`target/` хавтсыг `.gitignore` файлд оруулсан тул Git репозиторт оруулахгүй.

## 3. Тестлэх системийн тухай

Энэ лабораторийн ажлаар `CourseRegistrationService` классын `register()` үйлдлийг Mockito ашиглан тестэлсэн.

Тестлэх үндсэн нөхцөлүүд:

- Оюутан бүртгэлтэй, идэвхтэй эсэх.
- Сонгосон хичээл байгаа эсэх.
- Урьдчилсан нөхцөл хангагдсан эсэх.
- Бүртгэл амжилттай хадгалагдах эсэх.
- И-мэйл илгээх үед алдаа гарсан эсэх.
- `null` утгатай оролтод зөв exception үүсэх эсэх.

## 4. Unit Test-ийн жагсаалт

| № | Тестийн нэр | Шалгах нөхцөл | Хүлээгдэж буй үр дүн |
|---|---|---|---|
| 1 | `activeStudentWithValidCourse_registersSuccessfully` | Идэвхтэй оюутан, хүчинтэй хичээл | `OK`, бүртгэл хадгалж, и-мэйл илгээнэ |
| 2 | `missingStudent_doesNotRegisterOrSendEmail` | Оюутан олдохгүй | `ERROR_NO_STUDENT`, хадгалалт болон и-мэйл хийгдэхгүй |
| 3 | `inactiveStudent_doesNotSendEmail` | Оюутан идэвхгүй | `ERROR_INACTIVE_STUDENT`, и-мэйл илгээхгүй |
| 4 | `missingPrerequisite_doesNotSaveRegistration` | Урьдчилсан нөхцөл хангаагүй | `ERROR_PREREQUISITES`, бүртгэл хадгалахгүй |
| 5 | `emailFailure_registrationStillSucceeds` | И-мэйл илгээлтэд exception үүсэх | Бүртгэлийн үр дүн `OK` хэвээр байна |
| 6 | `nullInput_throwsExceptionWithoutDependencyInteractions` | Оролтын утга `null` | `IllegalArgumentException`, dependency дуудагдахгүй |
| 7 | `successfulRegistration_emailBodyContainsCourseCode` | И-мэйлийн агуулга | `ArgumentCaptor` ашиглан хичээлийн код орсныг шалгана |
| 8–10 | `registrationError_scenarioReturnsExpectedError` | Идэвхгүй оюутан, байхгүй хичээл, урьдчилсан нөхцөл дутуу | `@ParameterizedTest`, `@CsvSource` ашиглан 3 тохиолдлыг шалгана |

Нийт **10 тест** ажиллуулсан.

## 5. Mockito ашигласан байдал

| Mockito / JUnit боломж | Зориулалт |
|---|---|
| `@Mock` | `StudentRepository`, `EmailSender` dependency-үүдийг mock болгох |
| `@InjectMocks` | Mock dependency-үүдийг `CourseRegistrationService`-д холбох |
| `when(...).thenReturn(...)` | Mock method-ийн буцаах утгыг тохируулах |
| `verify(...)` | Dependency method дуудагдсан эсэхийг шалгах |
| `never()` | Тодорхой method огт дуудагдаагүйг шалгах |
| `verifyNoInteractions(...)` | Dependency-тэй харилцан үйлчлэл хийгдээгүйг шалгах |
| `doThrow(...)` | И-мэйл илгээх үед exception үүсгэх |
| `assertThrows(...)` | Хүлээгдэж буй exception үүссэн эсэхийг шалгах |
| `ArgumentCaptor` | И-мэйлийн агуулгыг барьж авч шалгах |
| `@ParameterizedTest` | Нэг тестийг олон өгөгдлөөр ажиллуулах |
| `@CsvSource` | Параметрчилсэн тестийн өгөгдөл дамжуулах |

### Lenient stubbing

Mockito-ийн `lenient()` нь ашиглагдаагүй stub-ийг strict stubbing шалгалтаар алдаа гэж үзэхгүй өнгөрүүлэх боломж олгодог.

Энэ лабораторийн ажилд `lenient()` ашиглаагүй. Учир нь тест бүрт шаардлагатай stub-ийг тусад нь тохируулсан бөгөөд ашиглагдаагүй stub-ийг илрүүлэх Mockito-ийн strict stubbing шалгалтыг хэвээр үлдээсэн. Ингэснээр шаардлагагүй mock тохиргоо болон тестийн алдааг эрт илрүүлэх боломжтой.

## 6. Тест ажиллуулах

Төслийн үндсэн хавтсанд дараах командыг ажиллуулна.

```bash
mvn clean test
```

Эцсийн амжилттай тестийн үр дүнг `results/mvn-test.txt` файлд хадгалсан.

| Үзүүлэлт | Үр дүн |
|---|---:|
| Tests run | 10 |
| Failures | 0 |
| Errors | 0 |
| Skipped | 0 |
| Build | SUCCESS |

## 7. Mutation Testing

Mutation testing ашиглан тестүүд програмын логик дахь алдаатай өөрчлөлтийг илрүүлж чадаж байгаа эсэхийг шалгасан.

| № | Mutation хийсэн өөрчлөлт | Tests run | Failures | Үр дүн |
|---|---|---:|---:|---|
| 1 | Идэвхгүй оюутны шалгалтыг устгасан | 10 | 2 | Илэрсэн |
| 2 | `repository.saveRegistration()` дуудлагыг устгасан | 10 | 2 | Илэрсэн |
| 3 | `courseId == null` шалгалтыг устгасан | 10 | 1 | Илэрсэн |

Mutation бүрийн дараа тухайн өөрчлөлтийн үеийн тестийн үр дүнг тусдаа файлд хадгалсан.

- `results/mvn-test-mutant-1.txt`
- `results/mvn-test-mutant-2.txt`
- `results/mvn-test-mutant-3.txt`

Гурван mutation бүгд тестээр илэрсэн. Mutation туршилтын дараа эх кодыг анхны хувилбараар сэргээж, `mvn clean test` командыг дахин ажиллуулсан. Эцсийн тестийн үр дүнд 10 тест бүгд амжилттай болсон.

## 8. Дүгнэлт

Энэ лабораторийн ажлаар Mockito ашиглан Java програмын unit test боловсруулах дадлага хийлээ. `CourseRegistrationService` классын амжилттай болон алдаатай нөхцөлүүдийг тус тусад нь шалгасан. Mock объект ашигласнаар repository болон email service-ийн ажиллагааг үндсэн логикоос тусгаарлан тестлэх боломжтой болсон. `verify()`, `never()` болон `verifyNoInteractions()` ашиглан dependency-ийн харилцан үйлчлэлийг баталгаажуулсан. `doThrow()` болон `assertThrows()` ашиглан exception үүсэх нөхцөлүүдийг шалгасан. `ArgumentCaptor` болон параметрчилсэн тест ашиглан и-мэйлийн агуулга болон гурван алдааны нөхцөлийг шалгасан. Mutation testing-ийн гурван өөрчлөлтийг тестүүд илрүүлж, эцсийн `mvn clean test` ажиллуулалтаар 10 тест бүгд амжилттай болж `BUILD SUCCESS` үр дүн гарсан.
