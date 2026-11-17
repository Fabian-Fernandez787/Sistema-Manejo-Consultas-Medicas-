
package clinic.registry;

import clinic.common.Interfaces;
import clinic.common.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class RegistryServiceImpl implements Interfaces.IRegistryModule {

    // A1: Stores patients using their unique IDs
    private final Map<String, Types.Patient> patients;

    // A1: Stores employees using their unique IDs
    private final Map<String, Types.Employee> employees;

    // Constructor
    public RegistryServiceImpl() {
        patients = new HashMap<>();
        employees = new HashMap<>();
    }

    // A1 - Op 1: Register a new patient
    @Override
    public boolean registerPatient(Types.Patient p) {

        if (p == null || p.id == null || p.id.isBlank()) {
            throw new IllegalArgumentException("Invalid patient");
        }

        // Reject duplicate patient IDs
        if (patients.containsKey(p.id)) {
            return false;
        }

        // Store patient using ID as key
        patients.put(p.id, p);

        return true;
    }

    // A1 - Op 2: Register a new employee
    @Override
    public boolean registerEmployee(Types.Employee e) {

        if (e == null || e.id == null || e.id.isBlank()) {
            throw new IllegalArgumentException("Invalid employee");
        }

        if (e.role == null) {
            throw new IllegalArgumentException("Invalid employee role");
        }

        // Reject duplicate employee IDs
        if (employees.containsKey(e.id)) {
            return false;
        }

        // Store employee using ID as key
        employees.put(e.id, e);

        return true;
    }

    // A1: Find a patient by ID
    // Returns null if the patient does not exist
    @Override
    public Types.Patient getPatient(String id) {
        return patients.get(id);
    }

    // A1: Find an employee by ID
    // Returns null if the employee does not exist
    @Override
    public Types.Employee getEmployee(String id) {
        return employees.get(id);
    }

    // A2 methods - To be implemented later
    @Override
    public boolean removeDoctor(String doctorId) {
        throw new UnsupportedOperationException("Pending A2");
    }

    @Override
    public List<Types.Doctor> findDoctorsBySpecialty(
            Types.Specialty spec) {
        throw new UnsupportedOperationException("Pending A2");
    }

    @Override
    public List<Types.Doctor> getAvailableDoctors() {
        throw new UnsupportedOperationException("Pending A2");
    }

    @Override
    public void setDoctorStatus(
            String doctorId, Types.DoctorStatus status) {
        throw new UnsupportedOperationException("Pending A2");
    }

    // A3 method - To be implemented later
    @Override
    public boolean linkPatientToDoctor(
            String doctorId, String patientId) {
        throw new UnsupportedOperationException("Pending A3");
    }
}