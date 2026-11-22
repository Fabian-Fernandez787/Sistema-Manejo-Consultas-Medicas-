package clinic;

import clinic.common.Types;
import clinic.registry.RegistryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RegistryServiceTest {

    private RegistryServiceImpl registry;

    // Creates a new registry before each test.
    // Calls: RegistryServiceImpl constructor.
    // Called by: JUnit before executing each test method.
    @BeforeEach
    void setUp() {

        registry = new RegistryServiceImpl();
    }

    // Registers a patient and verifies that the patient can be retrieved.
    // Calls: registerPatient(), getPatient().
    // Called by: JUnit when executing this test.
    @Test
    void testRegisterPatient() {

        Types.Patient patient = new Types.Patient(
            "P001", "John Smith", "7871234567"
        );

        assertTrue(registry.registerPatient(patient));
        assertEquals(patient, registry.getPatient("P001"));
    }

    // Verifies that the registry rejects a patient with an existing ID.
    // Calls: registerPatient().
    // Called by: JUnit when executing this test.
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

    // Registers an employee and verifies that the employee can be retrieved.
    // Calls: registerEmployee(), getEmployee().
    // Called by: JUnit when executing this test.
    @Test
    void testRegisterEmployee() {

        Types.Employee employee = new Types.Employee(
            "E001", "Jane Smith", Types.Role.NURSE
        );

        assertTrue(registry.registerEmployee(employee));
        assertEquals(employee, registry.getEmployee("E001"));
    }

    // Verifies that the registry rejects an employee with an existing ID.
    // Calls: registerEmployee().
    // Called by: JUnit when executing this test.
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

    // Verifies that searching for a nonexistent patient returns null.
    // Calls: getPatient().
    // Called by: JUnit when executing this test.
    @Test
    void testMissingPatient() {

        assertNull(registry.getPatient("P999"));
    }

    // Verifies that searching for a nonexistent employee returns null.
    // Calls: getEmployee().
    // Called by: JUnit when executing this test.
    @Test
    void testMissingEmployee() {

        assertNull(registry.getEmployee("E999"));
    }

    // Verifies that registering a null patient or an empty ID throws an exception.
    // Calls: registerPatient().
    // Called by: JUnit when executing this test.
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

    // Verifies that registering a null employee or an empty ID throws an exception.
    // Calls: registerEmployee().
    // Called by: JUnit when executing this test.
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

    // Verifies that the registry can store and retrieve 1,000 patients.
    // Calls: registerPatient(), getPatient().
    // Called by: JUnit when executing this test.
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

    // Verifies that the registry can store and retrieve 1,000 employees.
    // Calls: registerEmployee(), getEmployee().
    // Called by: JUnit when executing this test.
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

    // Verifies that searching an empty specialty returns an empty list.
    // Calls: findDoctorsBySpecialty().
    // Called by: JUnit when executing this test.
    @Test
    void testEmptySpecialty() {

        List<Types.Doctor> result =
            registry.findDoctorsBySpecialty(
                Types.Specialty.GENERAL
            );

        assertTrue(result.isEmpty());
    }

    // Verifies that doctors are returned under the correct specialty.
    // Calls: registerEmployee(), findDoctorsBySpecialty().
    // Called by: JUnit when executing this test.
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

    // Verifies that removing a doctor also removes the doctor from the employee and specialty registries.
    // Calls: registerEmployee(), removeDoctor(), getEmployee(), findDoctorsBySpecialty().
    // Called by: JUnit when executing this test.
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

    // Verifies that changing a doctor's status to BUSY updates the doctor object.
    // Calls: registerEmployee(), setDoctorStatus(), getAvailableDoctors().
    // Called by: JUnit when executing this test.
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

    // Verifies that only doctors with AVAILABLE status are returned.
    // Calls: registerEmployee(), setDoctorStatus(), getAvailableDoctors().
    // Called by: JUnit when executing this test.
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

    // Verifies that changing the status of a nonexistent doctor throws an exception.
    // Calls: setDoctorStatus().
    // Called by: JUnit when executing this test.
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

    // Verifies that an existing patient can be linked to an existing doctor.
    // Calls: registerEmployee(), registerPatient(), linkPatientToDoctor().
    // Called by: JUnit when executing this test.
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

    // Verifies that the same patient cannot be linked twice to the same doctor.
    // Calls: registerEmployee(), registerPatient(), linkPatientToDoctor().
    // Called by: JUnit when executing this test.
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

    // Verifies that a patient cannot be linked to a doctor that does not exist.
    // Calls: registerPatient(), linkPatientToDoctor().
    // Called by: JUnit when executing this test.
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

    // Verifies that a nonexistent patient cannot be linked to an existing doctor.
    // Calls: registerEmployee(), linkPatientToDoctor().
    // Called by: JUnit when executing this test.
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

    // Verifies that the patient list of a doctor contains the linked patient.
    // Calls: registerEmployee(), registerPatient(), linkPatientToDoctor(), getPatientsForDoctor().
    // Called by: JUnit when executing this test.
    @Test
    void testGetPatientsForDoctor() {

        Types.Doctor doctor = new Types.Doctor(
            "D011", "Doctor Eleven", Types.Specialty.GENERAL
        );

        Types.Patient patient = new Types.Patient(
            "P011", "Patient Eleven", "7871234567"
        );

        registry.registerEmployee(doctor);
        registry.registerPatient(patient);

        registry.linkPatientToDoctor("D011", "P011");

        Set<String> result =
            registry.getPatientsForDoctor("D011");

        assertEquals(1, result.size());
        assertTrue(result.contains("P011"));
    }

    // Loads the initial clinic data and verifies that all doctors, employees, and patients are registered.
    // Calls: loadSeedData(), findDoctorsBySpecialty(), getEmployee(), getPatient(), getAvailableDoctors().
    // Called by: JUnit when executing this test.
    @Test
    void testLoadSeedData() {

        registry.loadSeedData();

        assertEquals(
            1,
            registry.findDoctorsBySpecialty(
                Types.Specialty.GENERAL
            ).size()
        );

        assertEquals(
            1,
            registry.findDoctorsBySpecialty(
                Types.Specialty.PEDIATRIC
            ).size()
        );

        assertEquals(
            1,
            registry.findDoctorsBySpecialty(
                Types.Specialty.NEURO
            ).size()
        );

        assertEquals(
            1,
            registry.findDoctorsBySpecialty(
                Types.Specialty.RETINA
            ).size()
        );

        assertNotNull(registry.getEmployee("N101"));
        assertNotNull(registry.getEmployee("N102"));
        assertNotNull(registry.getEmployee("S101"));

        for (int i = 1; i <= 5; i++) {
            assertNotNull(registry.getPatient("P10" + i));
        }

        assertEquals(4, registry.getAvailableDoctors().size());
    }

    // Verifies that loading the initial data more than once does not create duplicate records.
    // Calls: loadSeedData(), findDoctorsBySpecialty(), getPatient(), getAvailableDoctors().
    // Called by: JUnit when executing this test.
    @Test
    void testSeedDataDoesNotCreateDuplicates() {

        registry.loadSeedData();
        registry.loadSeedData();

        assertEquals(
            1,
            registry.findDoctorsBySpecialty(
                Types.Specialty.GENERAL
            ).size()
        );

        assertEquals(
            1,
            registry.findDoctorsBySpecialty(
                Types.Specialty.PEDIATRIC
            ).size()
        );

        assertEquals(
            1,
            registry.findDoctorsBySpecialty(
                Types.Specialty.NEURO
            ).size()
        );

        assertEquals(
            1,
            registry.findDoctorsBySpecialty(
                Types.Specialty.RETINA
            ).size()
        );

        for (int i = 1; i <= 5; i++) {
            assertNotNull(registry.getPatient("P10" + i));
        }

        assertEquals(4, registry.getAvailableDoctors().size());
    }
}