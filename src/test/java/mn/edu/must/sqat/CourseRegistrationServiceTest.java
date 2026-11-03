package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
}
