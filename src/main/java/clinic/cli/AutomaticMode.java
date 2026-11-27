package clinic.cli;

import java.time.LocalDate;
import java.util.Scanner;

import clinic.appointments.AppointmentManager;
import clinic.common.Types;
import clinic.registry.RegistryServiceImpl;
import clinic.waiting.WaitingRoomServiceImpl;

public class AutomaticMode {

    // Displays the automatic mode menu options
    // Calls: System.out.println()
    // Called by: run()
    private static void showMenu() {
        System.out.println("\n=== SISTEMA MEDICO - MODO AUTOMATICO ===");
        System.out.println("1. Registrar paciente");
        System.out.println("2. Registrar enfermera o secretario");
        System.out.println("3. Registrar medico");
        System.out.println("4. Eliminar medico");
        System.out.println("5. Buscar empleado por ID");
        System.out.println("6. Ver medicos disponibles");
        System.out.println("7. Buscar medicos por especialidad");
        System.out.println("8. Registrar llegada de paciente");
        System.out.println("9. Anadir paciente a sala de espera");
        System.out.println("10. Programar cita");
        System.out.println("11. Llamar proximo paciente");
        System.out.println("12. Registrar signos vitales y finalizar visita");
        System.out.println("13. Cancelar cita");
        System.out.println("14. Ver citas de hoy");
        System.out.println("15. Mostrar sala de espera");
        System.out.println("16. Ver historial de un paciente");
        System.out.println("17. Ver visitas recientes");
        System.out.println("18. Ver pacientes asociados a un medico");
        System.out.println("0. Regresar al menu principal");
    }

    // Runs automatic mode with preloaded data, sample appointments, a sample visit, and waiting patients
    // Calls: RegistryServiceImpl(), AppointmentManager(), WaitingRoomServiceImpl(), loadSeedData(), registerPatient(), LocalDate.now(), scheduleAppointment(), checkIn(), callNext(), recordVitals(), finishVisit(), showMenu()
    // Called by: Main.main(), main()
    public static void run(Scanner sc) {

        RegistryServiceImpl registry = new RegistryServiceImpl();
        AppointmentManager appointments = new AppointmentManager();

        WaitingRoomServiceImpl waiting =
                new WaitingRoomServiceImpl(registry, appointments);

        // Load initial seed data for doctors, staff, and patients
        registry.loadSeedData();

        // Add extra patients for automatic test scenarios
        registry.registerPatient(
                new Types.Patient("P106", "Paciente 6", "7871111111"));

        registry.registerPatient(
                new Types.Patient("P107", "Paciente 7", "7872222222"));

        // Create sample appointments for today
        String today = LocalDate.now().toString();

        Types.Appointment firstAppointment =
                appointments.scheduleAppointment(
                        "P101", "D101", today + "T10:00");

        appointments.scheduleAppointment(
                "P104", "D104", today + "T15:00");

        String appointmentId = firstAppointment.id;

        // Create one completed visit so history and recent visits have sample data
        waiting.checkIn("P105", 0);
        waiting.callNext("D104");
        waiting.recordVitals("P105", "N101", "120/80");

        waiting.finishVisit(
                "P105", "D104", "N101", "Consulta de ejemplo");

        // Queue initial waiting patients with mixed normal and emergency levels
        waiting.checkIn("P101", 0);
        waiting.checkIn("P102", 2);
        waiting.checkIn("P103", 1);
        waiting.checkIn("P104", 2);

        // Track the latest active assignment for automatic menu actions
        String activePatient = null;
        String activeDoctor = null;

        int option = -1;

        System.out.println("\nModo automatico iniciado.");

        while (option != 0) {

            showMenu();
            System.out.print("Seleccione una opcion: ");

            try {
                option = Integer.parseInt(sc.nextLine().trim());

                switch (option) {

                    case 1: {
                        // Automatically register a sample patient
                        Types.Patient patient = new Types.Patient(
                                "P108", "Paciente Automatico", "7873333333");

                        boolean added = registry.registerPatient(patient);

                        System.out.println("Paciente: " + patient.id);
                        System.out.println(added ?
                                "Paciente registrado" :
                                "Paciente ya registrado");
                        break;
                    }

                    case 2: {
                        // Automatically register a sample nurse
                        Types.Employee nurse = new Types.Employee(
                                "N103", "Enfermera Automatica",
                                Types.Role.NURSE);

                        boolean added = registry.registerEmployee(nurse);

                        System.out.println("Empleado: " + nurse.id);
                        System.out.println(added ?
                                "Empleado registrado" :
                                "Empleado ya registrado");
                        break;
                    }

                    case 3: {
                        // Display available doctors
                        System.out.println("Medicos disponibles:");

                        for (Types.Doctor d : registry.getAvailableDoctors()) {
                            System.out.println(
                                    d.id + " - " + d.name +
                                    " - " + d.specialty);
                        }
                        break;
                    }

                    case 4: {
                        // Search doctors in the NEURO specialty
                        System.out.println("Especialidad: NEURO");

                        for (Types.Doctor d :
                                registry.findDoctorsBySpecialty(
                                        Types.Specialty.NEURO)) {

                            System.out.println(
                                    d.id + " - " + d.name +
                                    " - " + d.status);
                        }
                        break;
                    }

                    case 5: {
                        // Automatically check in normal patient P106 (level 0)
                        boolean added = waiting.checkIn("P106", 0);

                        System.out.println("Paciente: P106");
                        System.out.println("Nivel: 0");
                        System.out.println(added ?
                                "Paciente registrado en espera" :
                                "Paciente ya esta en espera");
                        break;
                    }

                    case 6: {
                        // Automatically check in emergency patient P107 (level 1)
                        boolean added = waiting.checkIn("P107", 1);

                        System.out.println("Paciente: P107");
                        System.out.println("Emergencia: nivel 1");
                        System.out.println(added ?
                                "Emergencia registrada" :
                                "Paciente ya esta en espera");
                        break;
                    }

                    case 7: {
                        // Automatically schedule a sample appointment for today
                        String time = today + "T11:00";

                        Types.Appointment appt =
                                appointments.scheduleAppointment(
                                        "P102", "D102", time);

                        appointmentId = appt.id;

                        System.out.println("Cita: " + appt.id);
                        System.out.println("Paciente: " + appt.patientId);
                        System.out.println("Medico: " + appt.doctorId);
                        System.out.println("Fecha: " + appt.startTime);
                        break;
                    }

                    case 8: {
                        // Call the next waiting patient using the first available doctor
                        if (registry.getAvailableDoctors().isEmpty()) {
                            System.out.println("No hay medicos disponibles");
                            break;
                        }

                        if (waiting.listWaiting().isEmpty()) {
                            System.out.println("No hay pacientes esperando");
                            break;
                        }

                        Types.Doctor doctor =
                                registry.getAvailableDoctors().get(0);

                        String patientId = waiting.callNext(doctor.id);

                        activePatient = patientId;
                        activeDoctor = doctor.id;

                        System.out.println("Medico: " + activeDoctor);
                        System.out.println("Paciente llamado: " + activePatient);
                        break;
                    }

                    case 9: {
                        // Start a visit if none is active, then record vitals and finish it
                        if (activePatient == null) {

                            if (waiting.listWaiting().isEmpty() ||
                                    registry.getAvailableDoctors().isEmpty()) {

                                System.out.println(
                                        "No hay pacientes o medicos disponibles");
                                break;
                            }

                            Types.Doctor doctor =
                                    registry.getAvailableDoctors().get(0);

                            activeDoctor = doctor.id;
                            activePatient = waiting.callNext(activeDoctor);
                        }

                        waiting.recordVitals(
                                activePatient, "N101", "120/80");

                        Types.Visit visit = waiting.finishVisit(
                                activePatient,
                                activeDoctor,
                                "N101",
                                "Consulta automatica completada");

                        System.out.println("Paciente: " + visit.patientId);
                        System.out.println("Medico: " + visit.doctorId);
                        System.out.println("Enfermera: " + visit.nurseId);
                        System.out.println("Vitales: " + visit.vitals);
                        System.out.println("Notas: " + visit.notes);
                        System.out.println("Visita completada");

                        activePatient = null;
                        activeDoctor = null;
                        break;
                    }

                    case 10: {
                        // Automatically cancel the most recently tracked appointment
                        boolean cancelled =
                                appointments.cancelAppointment(appointmentId);

                        System.out.println("Cita: " + appointmentId);
                        System.out.println(cancelled ?
                                "Cita cancelada" :
                                "La cita no se pudo cancelar");
                        break;
                    }

                    case 11: {
                        // Display today's scheduled appointments
                        System.out.println("Citas de hoy:");

                        for (Types.Appointment a :
                                appointments.getTodaysAppointments()) {

                            System.out.println(
                                    a.id + " | " + a.patientId +
                                    " | " + a.doctorId +
                                    " | " + a.startTime +
                                    " | " + a.status);
                        }
                        break;
                    }

                    case 12: {
                        // Display waiting room patients in priority order
                        System.out.println("Sala de espera:");
                        System.out.println(waiting.listWaiting());
                        break;
                    }

                    case 13: {
                        // Display sample visit history for patient P105
                        System.out.println("Historial del paciente P105:");

                        for (Types.Visit v : appointments.getHistory("P105")) {
                            System.out.println(
                                    v.timestamp +
                                    " | Medico: " + v.doctorId +
                                    " | Vitales: " + v.vitals +
                                    " | Notas: " + v.notes);
                        }
                        break;
                    }

                    case 14: {
                        // Display up to 5 recent visits
                        System.out.println("Visitas recientes:");

                        for (Types.Visit v :
                                appointments.getRecentCompleted(5)) {

                            System.out.println(
                                    v.patientId +
                                    " | " + v.doctorId +
                                    " | " + v.timestamp);
                        }
                        break;
                    }

                    case 15: {
                        // Display patients linked to doctor D104
                        System.out.println("Medico: D104");
                        System.out.println(
                                registry.getPatientsForDoctor("D104"));
                        break;
                    }

                    case 0: {
                        System.out.println(
                                "Regresando al menu principal...");
                        break;
                    }

                    default: {
                        System.out.println("Opcion invalida");
                        break;
                    }
                }

            } catch (IllegalArgumentException |
                     IllegalStateException e) {

                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Automatic Mode Runner
    // Calls: Scanner(), run()
    // Called by: JVM when launching AutomaticMode directly
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        run(sc);
        sc.close();
    }
}