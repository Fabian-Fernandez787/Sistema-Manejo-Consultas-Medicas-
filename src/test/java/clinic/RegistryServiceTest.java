package clinic;

import clinic.common.Types;
import clinic.registry.RegistryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegistryServiceTest {

    private RegistryServiceImpl registry;

    @BeforeEach
    void setUp() {
        registry = new RegistryServiceImpl();
    }


    // ==========================================
    // A1: PATIENT AND EMPLOYEE REGISTRY
    // ==========================================

    @Test
    void testRegisterPatient() {

        Types.Patient patient = new Types.Patient(
            "P001", "John Smith", "7871234567"
        );

        assertTrue(registry.registerPatient(patient));
        assertEquals(patient, registry.getPatient("P001"));
    }


    @Test
    void testDuplicatePatient() {

        Types.Patient patient1 = new Types.Patient(
            "P001", "John Smith", "7871234567"
        );

        Types.Patient patient2 = new Types.Patient(
            "P001", "Mary Jones", "7879876543"
        );

        assertTrue(registry.registerPatient(patient1));
        assertFalse(registry.registerPatient(patient2));
    }


    @Test
    void testRegisterEmployee() {

        Types.Employee employee = new Types.Employee(
            "E001", "Jane Smith", Types.Role.NURSE
        );

        assertTrue(registry.registerEmployee(employee));
        assertEquals(employee, registry.getEmployee("E001"));
    }


    @Test
    void testDuplicateEmployee() {

        Types.Employee employee1 = new Types.Employee(
            "E001", "Jane Smith", Types.Role.NURSE
        );

        Types.Employee employee2 = new Types.Employee(
            "E001", "John Smith", Types.Role.SECRETARY
        );

        assertTrue(registry.registerEmployee(employee1));
        assertFalse(registry.registerEmployee(employee2));
    }


    @Test
    void testMissingPatient() {

        assertNull(registry.getPatient("P999"));
    }


    @Test
    void testMissingEmployee() {

        assertNull(registry.getEmployee("E999"));
    }


    @Test
    void testInvalidPatient() {

        assertThrows(
            IllegalArgumentException.class,
            () -> registry.registerPatient(null)
        );

        Types.Patient patient = new Types.Patient(
            "", "John Smith", "7871234567"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> registry.registerPatient(patient)
        );
    }


    @Test
    void testInvalidEmployee() {

        assertThrows(
            IllegalArgumentException.class,
            () -> registry.registerEmployee(null)
        );

        Types.Employee employee = new Types.Employee(
            "", "Jane Smith", Types.Role.NURSE
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> registry.registerEmployee(employee)
        );
    }


    @Test
    void testOneThousandPatients() {

        for (int i = 0; i < 1000; i++) {

            Types.Patient patient = new Types.Patient(
                "P" + i,
                "Patient " + i,
                "7870000000"
            );

            assertTrue(registry.registerPatient(patient));
        }

        assertNotNull(registry.getPatient("P500"));
        assertNotNull(registry.getPatient("P999"));
    }


    @Test
    void testOneThousandEmployees() {

        for (int i = 0; i < 1000; i++) {

            Types.Employee employee = new Types.Employee(
                "E" + i,
                "Employee " + i,
                Types.Role.NURSE
            );

            assertTrue(registry.registerEmployee(employee));
        }

        assertNotNull(registry.getEmployee("E500"));
        assertNotNull(registry.getEmployee("E999"));
    }


    // ==========================================
    // A2: DOCTOR MANAGEMENT
    // ==========================================

    @Test
    void testEmptySpecialty() {

        List<Types.Doctor> result =
            registry.findDoctorsBySpecialty(
                Types.Specialty.GENERAL
            );

        assertTrue(result.isEmpty());
    }


    @Test
    void testFindDoctorsBySpecialty() {

        Types.Doctor doctor1 = new Types.Doctor(
            "D001", "Doctor One", Types.Specialty.GENERAL
        );

        Types.Doctor doctor2 = new Types.Doctor(
            "D002", "Doctor Two", Types.Specialty.RETINA
        );

        assertTrue(registry.registerEmployee(doctor1));
        assertTrue(registry.registerEmployee(doctor2));

        List<Types.Doctor> result =
            registry.findDoctorsBySpecialty(
                Types.Specialty.GENERAL
            );

        assertEquals(1, result.size());
        assertEquals("D001", result.get(0).id);
    }


    @Test
    void testRemoveDoctor() {

        Types.Doctor doctor = new Types.Doctor(
            "D003", "Doctor Three", Types.Specialty.PEDIATRIC
        );

        registry.registerEmployee(doctor);

        assertTrue(registry.removeDoctor("D003"));

        assertNull(registry.getEmployee("D003"));

        assertTrue(
            registry.findDoctorsBySpecialty(
                Types.Specialty.PEDIATRIC
            ).isEmpty()
        );

        assertFalse(registry.removeDoctor("D003"));
    }


    @Test
    void testChangeDoctorStatus() {

        Types.Doctor doctor = new Types.Doctor(
            "D004", "Doctor Four", Types.Specialty.NEURO
        );

        registry.registerEmployee(doctor);

        registry.setDoctorStatus(
            "D004",
            Types.DoctorStatus.BUSY
        );

        assertEquals(
            Types.DoctorStatus.BUSY,
            doctor.status
        );

        assertTrue(registry.getAvailableDoctors().isEmpty());
    }


    @Test
    void testGetAvailableDoctors() {

        Types.Doctor doctor1 = new Types.Doctor(
            "D005", "Doctor Five", Types.Specialty.GENERAL
        );

        Types.Doctor doctor2 = new Types.Doctor(
            "D006", "Doctor Six", Types.Specialty.RETINA
        );

        registry.registerEmployee(doctor1);
        registry.registerEmployee(doctor2);

        registry.setDoctorStatus(
            "D006",
            Types.DoctorStatus.BUSY
        );

        List<Types.Doctor> available =
            registry.getAvailableDoctors();

        assertEquals(1, available.size());
        assertEquals("D005", available.get(0).id);
    }


    @Test
    void testSetStatusForMissingDoctor() {

        assertThrows(
            IllegalArgumentException.class,
            () -> registry.setDoctorStatus(
                "D999",
                Types.DoctorStatus.BUSY
            )
        );
    }


    // ==========================================
    // A3: DOCTOR'S PATIENT LIST - NO DUPLICATES
    // ==========================================

    @Test
    void testLinkPatientToDoctor() {

        Types.Doctor doctor = new Types.Doctor(
            "D007", "Doctor Seven", Types.Specialty.GENERAL
        );

        Types.Patient patient = new Types.Patient(
            "P007", "Patient Seven", "7871234567"
        );

        registry.registerEmployee(doctor);
        registry.registerPatient(patient);

        assertTrue(
            registry.linkPatientToDoctor("D007", "P007")
        );
    }


    @Test
    void testSamePatientLinkedTwiceToSameDoctor() {

        Types.Doctor doctor = new Types.Doctor(
            "D008", "Doctor Eight", Types.Specialty.RETINA
        );

        Types.Patient patient = new Types.Patient(
            "P008", "Patient Eight", "7871234567"
        );

        registry.registerEmployee(doctor);
        registry.registerPatient(patient);

        assertTrue(
            registry.linkPatientToDoctor("D008", "P008")
        );

        assertFalse(
            registry.linkPatientToDoctor("D008", "P008")
        );
    }


    @Test
    void testLinkPatientToMissingDoctor() {

        Types.Patient patient = new Types.Patient(
            "P009", "Patient Nine", "7871234567"
        );

        registry.registerPatient(patient);

        assertFalse(
            registry.linkPatientToDoctor("D999", "P009")
        );
    }


    @Test
    void testLinkMissingPatientToDoctor() {

        Types.Doctor doctor = new Types.Doctor(
            "D010", "Doctor Ten", Types.Specialty.NEURO
        );

        registry.registerEmployee(doctor);

        assertFalse(
            registry.linkPatientToDoctor("D010", "P999")
        );
    }
}