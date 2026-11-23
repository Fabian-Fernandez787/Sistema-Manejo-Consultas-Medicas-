package clinic.registry;

import clinic.common.Interfaces;
import clinic.common.Types;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegistryServiceImpl implements Interfaces.IRegistryModule {

    // Stores patients using their unique IDs.
    private final Map<String, Types.Patient> patients;

    // Stores all employees using their unique IDs.
    private final Map<String, Types.Employee> employees;

    // Stores doctors using their unique IDs.
    private final Map<String, Types.Doctor> doctors;

    // Organizes doctors according to their specialties.
    private final Map<Types.Specialty, Map<String, Types.Doctor>>
            specialtyIndex;

    // Stores the patient IDs assigned to each doctor.
    private final Map<String, Set<String>> doctorPatients;

    // Initializes all registries and creates an empty index for each specialty.
    // Calls: HashMap, EnumMap.
    // Called by: The program when creating a RegistryServiceImpl object.
    public RegistryServiceImpl() {

        patients = new HashMap<>();
        employees = new HashMap<>();
        doctors = new HashMap<>();

        specialtyIndex =
                new EnumMap<>(Types.Specialty.class);

        doctorPatients = new HashMap<>();

        for (Types.Specialty specialty : Types.Specialty.values()) {
            specialtyIndex.put(specialty, new HashMap<>());
        }
    }

    // Registers a patient after validating the patient and checking for duplicate IDs.
    // Calls: patients.containsKey(), patients.put().
    // Called by: The CLI or another module when registering a patient.
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

    // Registers an employee and sends doctor objects to the doctor registration method.
    // Calls: addDoctor(), employees.containsKey(), employees.put().
    // Called by: The CLI or another module when registering an employee.
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

    // Registers a doctor in the employee registry, doctor registry, specialty index, and patient list.
    // Calls: employees.put(), doctors.put(), specialtyIndex.get(), doctorPatients.put().
    // Called by: registerEmployee() when the employee has the DOCTOR role.
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

        employees.put(doctor.id, doctor);
        doctors.put(doctor.id, doctor);

        specialtyIndex
            .get(doctor.specialty)
            .put(doctor.id, doctor);

        doctorPatients.put(doctor.id, new HashSet<>());

        return true;
    }

    // Searches for a patient using the patient's ID and returns null if the patient does not exist.
    // Calls: patients.get().
    // Called by: The CLI or another module when retrieving a patient.
    @Override
    public Types.Patient getPatient(String id) {

        return patients.get(id);
    }

    // Searches for an employee using the employee's ID and returns null if the employee does not exist.
    // Calls: employees.get().
    // Called by: The CLI or another module when retrieving an employee.
    @Override
    public Types.Employee getEmployee(String id) {

        return employees.get(id);
    }

    // Removes a doctor from the employee registry, doctor registry, specialty index, and patient list.
    // Calls: doctors.remove(), employees.remove(), specialtyIndex.get(), doctorPatients.remove().
    // Called by: The CLI or another module when removing a doctor.
    @Override
    public boolean removeDoctor(String doctorId) {

        Types.Doctor doctor = doctors.remove(doctorId);

        if (doctor == null) {
            return false;
        }

        employees.remove(doctorId);

        specialtyIndex
            .get(doctor.specialty)
            .remove(doctorId);

        doctorPatients.remove(doctorId);

        return true;
    }

    // Returns all doctors registered under the requested specialty.
    // Calls: specialtyIndex.get(), result.addAll().
    // Called by: The CLI or another module when searching doctors by specialty.
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

    // Returns a list containing only doctors whose status is AVAILABLE.
    // Calls: doctors.values(), availableDoctors.add().
    // Called by: The CLI or another module when requesting available doctors.
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

    // Changes the status of an existing doctor to AVAILABLE or BUSY.
    // Calls: doctors.get().
    // Called by: The CLI or another module when updating a doctor's status.
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

    // Links an existing patient to an existing doctor without allowing duplicate assignments.
    // Calls: doctors.containsKey(), patients.containsKey(), doctorPatients.get(), patientSet.add().
    // Called by: The CLI or another module when assigning a patient to a doctor.
    @Override
    public boolean linkPatientToDoctor(
            String doctorId,
            String patientId) {

        if (!doctors.containsKey(doctorId)) {
            return false;
        }

        if (!patients.containsKey(patientId)) {
            return false;
        }

        Set<String> patientSet = doctorPatients.get(doctorId);

        if (patientSet == null) {
            return false;
        }

        return patientSet.add(patientId);
    }

    // Returns a copy of the patient IDs assigned to a doctor.
    // Calls: doctorPatients.get().
    // Called by: Tests or other code that needs to inspect a doctor's patient list.
    public Set<String> getPatientsForDoctor(String doctorId) {

        Set<String> patientSet = doctorPatients.get(doctorId);

        if (patientSet == null) {
            return new HashSet<>();
        }

        return new HashSet<>(patientSet);
    }

    // Loads the initial doctors, nurses, secretary, and patients into the registries.
    // Calls: registerEmployee(), registerPatient().
    // Called by: The main program during startup when loading initial data.
    public void loadSeedData() {

        registerEmployee(new Types.Doctor(
            "D101",
            "General Doctor",
            Types.Specialty.GENERAL
        ));

        registerEmployee(new Types.Doctor(
            "D102",
            "Pediatric Doctor",
            Types.Specialty.PEDIATRIC
        ));

        registerEmployee(new Types.Doctor(
            "D103",
            "Neuro Doctor",
            Types.Specialty.NEURO
        ));

        registerEmployee(new Types.Doctor(
            "D104",
            "Retina Doctor",
            Types.Specialty.RETINA
        ));

        registerEmployee(new Types.Employee(
            "N101",
            "Nurse One",
            Types.Role.NURSE
        ));

        registerEmployee(new Types.Employee(
            "N102",
            "Nurse Two",
            Types.Role.NURSE
        ));

        registerEmployee(new Types.Employee(
            "S101",
            "Secretary",
            Types.Role.SECRETARY
        ));

        for (int i = 1; i <= 5; i++) {

            registerPatient(new Types.Patient(
                "P10" + i,
                "Patient " + i,
                "7870000000"
            ));
        }
    }
}