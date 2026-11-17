package clinic;

import clinic.common.Types;
import clinic.registry.RegistryServiceImpl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistryServiceTest {

    // Test 1: Register a patient successfully
    @Test
    void testRegisterPatient() {

        RegistryServiceImpl registry = new RegistryServiceImpl();

        Types.Patient patient =
                new Types.Patient("P001", "John Smith", "7871234567");

        assertTrue(registry.registerPatient(patient));

        assertEquals(patient, registry.getPatient("P001"));
    }

    // Test 2: Reject duplicate patient IDs
    @Test
    void testDuplicatePatientID() {

        RegistryServiceImpl registry = new RegistryServiceImpl();

        Types.Patient patient1 =
                new Types.Patient("P001", "John Smith", "7871234567");

        Types.Patient patient2 =
                new Types.Patient("P001", "Mary Smith", "7879876543");

        assertTrue(registry.registerPatient(patient1));

        assertFalse(registry.registerPatient(patient2));

        assertEquals(patient1, registry.getPatient("P001"));
    }

    // Test 3: Register an employee successfully
    @Test
    void testRegisterEmployee() {

        RegistryServiceImpl registry = new RegistryServiceImpl();

        Types.Employee employee =
                new Types.Employee("E001", "Jane Smith", Types.Role.NURSE);

        assertTrue(registry.registerEmployee(employee));

        assertEquals(employee, registry.getEmployee("E001"));
    }

    // Test 4: Reject duplicate employee IDs
    @Test
    void testDuplicateEmployeeID() {

        RegistryServiceImpl registry = new RegistryServiceImpl();

        Types.Employee employee1 =
                new Types.Employee("E001", "Jane Smith", Types.Role.NURSE);

        Types.Employee employee2 =
                new Types.Employee("E001", "John Smith", Types.Role.SECRETARY);

        assertTrue(registry.registerEmployee(employee1));

        assertFalse(registry.registerEmployee(employee2));

        assertEquals(employee1, registry.getEmployee("E001"));
    }

    // Test 5: Lookup returns null when ID is missing
    @Test
    void testMissingID() {

        RegistryServiceImpl registry = new RegistryServiceImpl();

        assertNull(registry.getPatient("P999"));
        assertNull(registry.getEmployee("E999"));
    }

    // Test 6: Reject invalid IDs
    @Test
    void testInvalidIDs() {

        RegistryServiceImpl registry = new RegistryServiceImpl();

        Types.Patient patient =
                new Types.Patient("", "John Smith", "7871234567");

        Types.Employee employee =
                new Types.Employee(null, "Jane Smith", Types.Role.NURSE);

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.registerPatient(patient)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.registerEmployee(employee)
        );
    }

    // Test 7: Register 1000 patients
    @Test
    void test1000PatientInsertions() {

        RegistryServiceImpl registry = new RegistryServiceImpl();

        for (int i = 0; i < 1000; i++) {

            Types.Patient patient =
                    new Types.Patient(
                            "P" + i,
                            "Patient " + i,
                            "7870000000"
                    );

            assertTrue(registry.registerPatient(patient));
        }

        assertNotNull(registry.getPatient("P0"));
        assertNotNull(registry.getPatient("P999"));
        assertNull(registry.getPatient("P1000"));
    }

    // Test 8: Register 1000 employees
    @Test
    void test1000EmployeeInsertions() {

        RegistryServiceImpl registry = new RegistryServiceImpl();

        for (int i = 0; i < 1000; i++) {

            Types.Employee employee =
                    new Types.Employee(
                            "E" + i,
                            "Employee " + i,
                            Types.Role.NURSE
                    );

            assertTrue(registry.registerEmployee(employee));
        }

        assertNotNull(registry.getEmployee("E0"));
        assertNotNull(registry.getEmployee("E999"));
        assertNull(registry.getEmployee("E1000"));
    }
}