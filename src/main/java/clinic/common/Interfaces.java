package clinic.common;

import java.util.List;

public class Interfaces {

    public interface IRegistryModule {
        boolean registerPatient(Types.Patient p);
        boolean registerEmployee(Types.Employee e);
        Types.Patient getPatient(String id);
        Types.Employee getEmployee(String id);
        boolean removeDoctor(String doctorId);
        List<Types.Doctor> findDoctorsBySpecialty(Types.Specialty spec);
        List<Types.Doctor> getAvailableDoctors();
        void setDoctorStatus(String doctorId, Types.DoctorStatus status);
        boolean linkPatientToDoctor(String doctorId, String patientId);
    }

    public interface IWaitingRoomModule {
        boolean checkIn(String patientId, int emergencyLevel);
        List<String> listWaiting();
        String callNext(String doctorId);
        void recordVitals(String patientId, String nurseId, String vitals);
        Types.Visit finishVisit(String patientId, String doctorId, String nurseId, String notes);
    }

    public interface IAppointmentsModule {
        Types.Appointment scheduleAppointment(String patientId, String doctorId, String startTime);
        boolean cancelAppointment(String appointmentId);
        boolean isDoctorFree(String doctorId, String startTime);
        List<Types.Appointment> getTodaysAppointments();
        void addVisit(Types.Visit visit);
        List<Types.Visit> getHistory(String patientId);
        List<Types.Visit> getRecentCompleted(int n);
    }
}