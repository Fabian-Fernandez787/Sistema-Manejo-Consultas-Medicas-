package clinic.cli;

import java.util.Scanner;

import clinic.appointments.AppointmentManager;
import clinic.common.Types;
import clinic.registry.RegistryServiceImpl;
import clinic.waiting.WaitingRoomServiceImpl;

public class ManualMode {

    // Lee un dato escrito en la consola.
    private static String read(Scanner sc, String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    // Muestra el menu principal del sistema.
    private static void showMenu() {
        System.out.println("\n=== SISTEMA DE CONSULTAS MEDICAS ===");
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
        System.out.println("0. Salir");
    }

    // Ejecuta las opciones de los tres modulos.
    public static void run(Scanner sc) {

        RegistryServiceImpl registry = new RegistryServiceImpl();
        AppointmentManager appointments = new AppointmentManager();

        WaitingRoomServiceImpl waiting =
                new WaitingRoomServiceImpl(registry, appointments);

        // Cargar datos iniciales.
        registry.loadSeedData();

        int option = -1;

        while (option != 0) {

            showMenu();

            try {
                option = Integer.parseInt(read(sc, "Opcion: "));

                switch (option) {

                    case 1: {
                        // Registrar paciente.
                        String id = read(sc, "ID del paciente: ");
                        String name = read(sc, "Nombre: ");
                        String phone = read(sc, "Telefono: ");

                        boolean added = registry.registerPatient(
                                new Types.Patient(id, name, phone));

                        System.out.println(added ?
                                "Paciente registrado" : "ID ya registrado");
                        break;
                    }

                    case 2: {
                        // Registrar enfermera o secretario.
                        String id = read(sc, "ID del empleado: ");
                        String name = read(sc, "Nombre: ");
                        String role = read(sc,
                                "Rol (NURSE/SECRETARY): ").toUpperCase();

                        if (!role.equals("NURSE") &&
                                !role.equals("SECRETARY")) {
                            System.out.println("Rol invalido");
                            break;
                        }

                        boolean added = registry.registerEmployee(
                                new Types.Employee(
                                        id, name, Types.Role.valueOf(role)));

                        System.out.println(added ?
                                "Empleado registrado" : "ID ya registrado");
                        break;
                    }

                    case 3: {
                        // Registrar medico.
                        String id = read(sc, "ID del medico: ");
                        String name = read(sc, "Nombre: ");
                        String spec = read(sc,
                                "Especialidad (GENERAL/PEDIATRIC/NEURO/RETINA): ")
                                .toUpperCase();

                        boolean added = registry.registerEmployee(
                                new Types.Doctor(
                                        id, name, Types.Specialty.valueOf(spec)));

                        System.out.println(added ?
                                "Medico registrado" : "ID ya registrado");
                        break;
                    }

                    case 4: {
                        // Eliminar medico.
                        String doctorId = read(sc, "ID del medico a eliminar: ");
                        boolean removed = registry.removeDoctor(doctorId);

                        System.out.println(removed ?
                                "Medico eliminado" : "Medico no encontrado");
                        break;
                    }

                    case 5: {
                        // Buscar empleado por ID.
                        String id = read(sc, "ID del empleado: ");
                        Types.Employee emp = registry.getEmployee(id);

                        if (emp == null) {
                            System.out.println("Empleado no encontrado");
                        } else if (emp instanceof Types.Doctor) {
                            Types.Doctor doc = (Types.Doctor) emp;
                            System.out.println(
                                    doc.id + " | " + doc.name +
                                    " | " + doc.role +
                                    " | " + doc.specialty +
                                    " | " + doc.status);
                        } else {
                            System.out.println(
                                    emp.id + " | " + emp.name +
                                    " | " + emp.role);
                        }
                        break;
                    }

                    case 6: {
                        // Mostrar medicos disponibles.
                        for (Types.Doctor d : registry.getAvailableDoctors()) {
                            System.out.println(
                                    d.id + " - " + d.name + " - " + d.specialty);
                        }
                        break;
                    }

                    case 7: {
                        // Buscar medicos por especialidad.
                        String spec = read(sc,
                                "Especialidad (GENERAL/PEDIATRIC/NEURO/RETINA): ")
                                .toUpperCase();

                        for (Types.Doctor d : registry.findDoctorsBySpecialty(
                                Types.Specialty.valueOf(spec))) {

                            System.out.println(
                                    d.id + " - " + d.name + " - " + d.status);
                        }
                        break;
                    }

                    case 8:
                    case 9: {
                        // Registrar paciente en sala de espera.
                        String patientId = read(sc, "ID del paciente: ");

                        int level = Integer.parseInt(read(sc,
                                "Emergencia (0 normal, 1-3 emergencia): "));

                        boolean added = waiting.checkIn(patientId, level);

                        System.out.println(added ?
                                "Paciente en espera" :
                                "Paciente ya estaba en espera");
                        break;
                    }

                    case 10: {
                        // Programar cita.
                        String patientId = read(sc, "ID del paciente: ");
                        String doctorId = read(sc, "ID del medico: ");

                        if (registry.getPatient(patientId) == null ||
                                !(registry.getEmployee(doctorId)
                                        instanceof Types.Doctor)) {

                            System.out.println(
                                    "Paciente o medico no registrado");
                            break;
                        }

                        String time = read(sc,
                                "Fecha y hora (AAAA-MM-DDTHH:MM): ");

                        Types.Appointment appt =
                                appointments.scheduleAppointment(
                                        patientId, doctorId, time);

                        System.out.println("Cita creada: " + appt.id);
                        break;
                    }

                    case 11: {
                        // Llamar al proximo paciente.
                        String doctorId = read(sc, "ID del medico: ");

                        String patientId = waiting.callNext(doctorId);

                        System.out.println(patientId == null ?
                                "No hay pacientes esperando" :
                                "Paciente llamado: " + patientId);
                        break;
                    }

                    case 12: {
                        // Registrar signos vitales y finalizar visita.
                        String patientId = read(sc, "ID del paciente: ");
                        String doctorId = read(sc, "ID del medico: ");
                        String nurseId = read(sc, "ID de la enfermera: ");
                        String vitals = read(sc, "Signos vitales: ");
                        String notes = read(sc, "Notas: ");

                        waiting.recordVitals(patientId, nurseId, vitals);

                        Types.Visit visit = waiting.finishVisit(
                                patientId, doctorId, nurseId, notes);

                        System.out.println(
                                "Visita guardada: " + visit.patientId +
                                " / " + visit.timestamp);
                        break;
                    }

                    case 13: {
                        // Cancelar cita.
                        String id = read(sc, "ID de la cita: ");

                        boolean cancelled =
                                appointments.cancelAppointment(id);

                        System.out.println(cancelled ?
                                "Cita cancelada" :
                                "No se pudo cancelar la cita");
                        break;
                    }

                    case 14: {
                        // Mostrar citas de hoy.
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

                    case 15: {
                        // Mostrar pacientes esperando.
                        System.out.println(
                                "En espera: " + waiting.listWaiting());
                        break;
                    }

                    case 16: {
                        // Mostrar historial de paciente.
                        String id = read(sc, "ID del paciente: ");

                        for (Types.Visit v : appointments.getHistory(id)) {

                            System.out.println(
                                    v.timestamp +
                                    " | Medico: " + v.doctorId +
                                    " | Enfermera: " + v.nurseId +
                                    " | Vitales: " + v.vitals +
                                    " | Notas: " + v.notes);
                        }
                        break;
                    }

                    case 17: {
                        // Mostrar visitas recientes.
                        int n = Integer.parseInt(
                                read(sc, "Cantidad de visitas: "));

                        for (Types.Visit v :
                                appointments.getRecentCompleted(n)) {

                            System.out.println(
                                    v.timestamp +
                                    " | Paciente: " + v.patientId +
                                    " | Medico: " + v.doctorId);
                        }
                        break;
                    }

                    case 18: {
                        // Mostrar pacientes asociados a un medico.
                        String id = read(sc, "ID del medico: ");

                        System.out.println(
                                registry.getPatientsForDoctor(id));
                        break;
                    }

                    case 0: {
                        System.out.println("Hasta luego");
                        break;
                    }

                    default: {
                        System.out.println("Opcion no valida");
                        break;
                    }
                }

            } catch (IllegalArgumentException |
                     IllegalStateException |
                     UnsupportedOperationException e) {

                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Permite ejecutar este modo por separado.
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        run(sc);
        sc.close();
    }
}