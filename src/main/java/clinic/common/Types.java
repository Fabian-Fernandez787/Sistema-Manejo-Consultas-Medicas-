package clinic.common;

public class Types {
    public enum Specialty { GENERAL, PEDIATRIC, NEURO, RETINA }
    public enum Role { DOCTOR, NURSE, SECRETARY }
    public enum DoctorStatus { AVAILABLE, BUSY }
    public enum AppointmentStatus { SCHEDULED, COMPLETED, CANCELLED }

    public static class Patient {
        public String id;
        public String name;
        public String phone;

        public Patient(String id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }
    }

    public static class Employee {
        public String id;
        public String name;
        public Role role;

        public Employee(String id, String name, Role role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }
    }

    public static class Doctor extends Employee {
        public Specialty specialty;
        public DoctorStatus status;

        public Doctor(String id, String name, Specialty specialty) {
            super(id, name, Role.DOCTOR);
            this.specialty = specialty;
            this.status = DoctorStatus.AVAILABLE;
        }
    }

    public static class Appointment {
        public String id;
        public String patientId;
        public String doctorId;
        public String startTime;
        public AppointmentStatus status;
    }

    public static class Visit {
        public String patientId;
        public String doctorId;
        public String nurseId;
        public String timestamp;
        public String vitals;
        public String notes;
    }
}