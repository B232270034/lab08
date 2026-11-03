package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.mockito.ArgumentCaptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock
    private StudentRepository repository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private CourseRegistrationService service;

    private Student activeStudent;

    @BeforeEach
    void setUp() {
        activeStudent = new Student(
                "S001",
                "student@example.com",
                true,
                List.of("CS100")
        );
    }

    // A1: Амжилттай бүртгэл
    @Test
    void activeStudentWithValidCourse_registersSuccessfully() {
        when(repository.findStudent("S001")).thenReturn(activeStudent);
        when(repository.courseExists("CS101")).thenReturn(true);
        when(repository.prerequisites("CS101")).thenReturn(List.of("CS100"));

        String result = service.register("S001", "CS101");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S001", "CS101");
        verify(emailSender).send(
                eq("student@example.com"),
                eq("Бүртгэл баталгаажлаа"),
                anyString()
        );
    }

    // A2: Оюутан олдоогүй
    @Test
    void missingStudent_doesNotRegisterOrSendEmail() {
        when(repository.findStudent("S999")).thenReturn(null);

        String result = service.register("S999", "CS101");

        assertEquals("ERROR_NO_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }

    // A3: Идэвхгүй оюутан
    @Test
    void inactiveStudent_doesNotSendEmail() {
        Student inactiveStudent = new Student(
                "S002",
                "inactive@example.com",
                false,
                List.of()
        );
        when(repository.findStudent("S002")).thenReturn(inactiveStudent);

        String result = service.register("S002", "CS101");

        assertEquals("ERROR_INACTIVE_STUDENT", result);
        verifyNoInteractions(emailSender);
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }

    // A4: Өмнөх холбоо хичээл дутуу
    @Test
    void missingPrerequisite_doesNotSaveRegistration() {
        when(repository.findStudent("S001")).thenReturn(activeStudent);
        when(repository.courseExists("CS200")).thenReturn(true);
        when(repository.prerequisites("CS200")).thenReturn(List.of("CS150"));

        String result = service.register("S001", "CS200");

        assertEquals("ERROR_PREREQUISITES", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }

    // A5: Email алдаатай байсан ч бүртгэл амжилттай байна.
    @Test
    void emailFailure_registrationStillSucceeds() {
        when(repository.findStudent("S001")).thenReturn(activeStudent);
        when(repository.courseExists("CS101")).thenReturn(true);
        when(repository.prerequisites("CS101")).thenReturn(List.of("CS100"));

        doThrow(new RuntimeException("Email service unavailable"))
                .when(emailSender)
                .send(anyString(), anyString(), anyString());

        String result = service.register("S001", "CS101");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S001", "CS101");
    }

    // A6: Null оролтод exception шидэж, dependency ашиглахгүй.
    @Test
    void nullInput_throwsExceptionWithoutDependencyInteractions() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(null, "CS101")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register("S001", null)
        );

        verifyNoInteractions(repository, emailSender);
    }

    // A7: Email body дотор хичээлийн код орсон байна.
    @Test
    void successfulRegistration_emailBodyContainsCourseCode() {
        when(repository.findStudent("S001")).thenReturn(activeStudent);
        when(repository.courseExists("CS101")).thenReturn(true);
        when(repository.prerequisites("CS101")).thenReturn(List.of("CS100"));

        service.register("S001", "CS101");

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailSender).send(
                eq("student@example.com"),
                eq("Бүртгэл баталгаажлаа"),
                bodyCaptor.capture()
        );

        assertTrue(bodyCaptor.getValue().contains("CS101"));
    }


    // A8: Гурван төрлийн алдааг параметртэй тестээр шалгана.
    @ParameterizedTest
    @CsvSource({
            "inactive, ERROR_INACTIVE_STUDENT",
            "missing-course, ERROR_NO_COURSE",
            "missing-prerequisite, ERROR_PREREQUISITES"
    })
    void registrationError_scenarioReturnsExpectedError(
            String scenario,
            String expected
    ) {
        if (scenario.equals("inactive")) {
            Student inactiveStudent = new Student(
                    "S001",
                    "student@example.com",
                    false,
                    List.of()
            );
            when(repository.findStudent("S001")).thenReturn(inactiveStudent);

        } else if (scenario.equals("missing-course")) {
            when(repository.findStudent("S001")).thenReturn(activeStudent);
            when(repository.courseExists("CS101")).thenReturn(false);

        } else if (scenario.equals("missing-prerequisite")) {
            when(repository.findStudent("S001")).thenReturn(activeStudent);
            when(repository.courseExists("CS101")).thenReturn(true);
            when(repository.prerequisites("CS101")).thenReturn(List.of("CS200"));
        }

        String result = service.register("S001", "CS101");

        assertEquals(expected, result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verifyNoInteractions(emailSender);
    }

}
