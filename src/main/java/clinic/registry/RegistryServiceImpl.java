package clinic.registry;

import clinic.common.Interfaces;
import clinic.common.Types;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistryServiceImpl implements Interfaces.IRegistryModule {

    // ==========================================
    // A1: PATIENT AND EMPLOYEE REGISTRY
    // ==========================================

    // Patients indexed by their unique ID
    private final Map<String, Types.Patient> patients;

    // Employees indexed by their unique ID
    private final Map<String, Types.Employee> employees;


    // ==========================================
    // A2: DOCTOR MANAGEMENT
    // ==========================================

    // Doctors indexed by their unique ID
    private final Map<String, Types.Doctor> doctors;

    // Doctors organized by their four valid specialties
    private final Map<Types.Specialty, Map<String, Types.Doctor>>
            specialtyIndex;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public RegistryServiceImpl() {

        patients = new HashMap<>();
        employees = new HashMap<>();
        doctors = new HashMap<>();

        specialtyIndex =
                new EnumMap<>(Types.Specialty.class);

        // Initialize an index for each valid specialty
        for (Types.Specialty specialty : Types.Specialty.values()) {
            specialtyIndex.put(specialty, new HashMap<>());
        }
    }

    // ==========================================
    // A1: REGISTER PATIENT
    // ==========================================

    @Override
    public boolean registerPatient(Types.Patient p) {

        if (p == null || p.id == null || p.id.isBlank()) {
            throw new IllegalArgumentException("Invalid patient");
        }

        if (patients.containsKey(p.id)) {
            return false;
        }

        patients.put(p.id, p);

        return true;
    }

    // ==========================================
    // A1 AND A2: REGISTER EMPLOYEE
    // ==========================================

    @Override
    public boolean registerEmployee(Types.Employee e) {

        if (e == null || e.id == null || e.id.isBlank()) {
            throw new IllegalArgumentException("Invalid employee");
        }

        if (e.role == null) {
            throw new IllegalArgumentException("Invalid employee role");
        }

        if (employees.containsKey(e.id)) {
            return false;
        }

        // Doctors must also be added to the doctor indexes
        if (e.role == Types.Role.DOCTOR) {

            if (!(e instanceof Types.Doctor)) {
                throw new IllegalArgumentException(
                    "A doctor must be a Types.Doctor object"
                );
            }

            return addDoctor((Types.Doctor) e);
        }

        employees.put(e.id, e);

        return true;
    }

    // ==========================================
    // A2: ADD DOCTOR
    // ==========================================

    public boolean addDoctor(Types.Doctor doctor) {

        if (doctor == null ||
                doctor.id == null ||
                doctor.id.isBlank()) {

            throw new IllegalArgumentException("Invalid doctor");
        }

        if (doctor.specialty == null || doctor.status == null) {
            throw new IllegalArgumentException(
                "Invalid doctor information"
            );
        }

        if (employees.containsKey(doctor.id)) {
            return false;
        }

        // Add doctor to employee and doctor maps
        employees.put(doctor.id, doctor);
        doctors.put(doctor.id, doctor);

        // Update the specialty index
        specialtyIndex
            .get(doctor.specialty)
            .put(doctor.id, doctor);

        return true;
    }


    // ==========================================
    // A1: GET PATIENT
    // ==========================================

    @Override
    public Types.Patient getPatient(String id) {

        return patients.get(id);
    }


    // ==========================================
    // A1: GET EMPLOYEE
    // ==========================================

    @Override
    public Types.Employee getEmployee(String id) {

        return employees.get(id);
    }


    // ==========================================
    // A2: REMOVE DOCTOR
    // ==========================================

    @Override
    public boolean removeDoctor(String doctorId) {

        Types.Doctor doctor = doctors.remove(doctorId);

        if (doctor == null) {
            return false;
        }

        // Remove doctor from employee registry
        employees.remove(doctorId);

        // Update the specialty index
        specialtyIndex
            .get(doctor.specialty)
            .remove(doctorId);

        return true;
    }


    // ==========================================
    // A2: FIND DOCTORS BY SPECIALTY
    // ==========================================

    @Override
    public List<Types.Doctor> findDoctorsBySpecialty(
            Types.Specialty spec) {

        List<Types.Doctor> result = new ArrayList<>();

        if (spec == null) {
            return result;
        }

        Map<String, Types.Doctor> specialtyDoctors =
                specialtyIndex.get(spec);

        if (specialtyDoctors != null) {
            result.addAll(specialtyDoctors.values());
        }

        return result;
    }


    // ==========================================
    // A2: GET AVAILABLE DOCTORS
    // ==========================================

    @Override
    public List<Types.Doctor> getAvailableDoctors() {

        List<Types.Doctor> availableDoctors =
                new ArrayList<>();

        for (Types.Doctor doctor : doctors.values()) {

            if (doctor.status == Types.DoctorStatus.AVAILABLE) {
                availableDoctors.add(doctor);
            }
        }

        return availableDoctors;
    }


    // ==========================================
    // A2: SET DOCTOR STATUS
    // ==========================================

    @Override
    public void setDoctorStatus(
            String doctorId,
            Types.DoctorStatus status) {

        Types.Doctor doctor = doctors.get(doctorId);

        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found");
        }

        if (status == null) {
            throw new IllegalArgumentException("Invalid doctor status");
        }

        doctor.status = status;
    }


    // ==========================================
    // A3: PENDING IMPLEMENTATION
    // ==========================================

    @Override
    public boolean linkPatientToDoctor(
            String doctorId,
            String patientId) {

        throw new UnsupportedOperationException("Pending A3");
    }
}