package clinic.cli;

import java.util.Scanner;

public class Main {

    // Displays the mode selection menu
    // Calls: System.out.println()
    // Called by: main()
    private static void showModeMenu() {
        System.out.println("\n=== SELECCIONAR MODO DE EJECUCION ===");
        System.out.println("1. Modo manual (ingresar datos paso a paso)");
        System.out.println("2. Modo automatico (datos y acciones de prueba)");
        System.out.println("0. Salir");
    }

    // Lets the user choose between ManualMode and AutomaticMode
    // Calls: Scanner(), showModeMenu(), sc.nextLine(), trim(), Integer.parseInt(), ManualMode.run(), AutomaticMode.run(), sc.close()
    // Called by: JVM when starting the application
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int mode = -1;

        while (mode != 0) {
            showModeMenu();
            System.out.print("Seleccione un modo: ");
            String input = sc.nextLine().trim();

            try {
                option = Integer.parseInt(read(sc, "Opcion: "));

                switch (option) {

                    case 1: {
                        // Registrar paciente.
                        String id = read(sc, "ID del paciente (Ej: P001): ");
                        String name = read(sc, "Nombre (Ej: Jose Pérez): ");
                        String phone = read(sc, "Telefono (Ej: 1234567890): ");

                switch (mode) {
                    case 1:
                        ManualMode.run(sc);
                        break;
                    }

                    case 2: {
                        // Registrar enfermera o secretario.
                        String id = read(sc, "ID del empleado (Ej: E001): ");
                        String name = read(sc, "Nombre (Ej: Maria García): ");
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
                    case 0:
                        System.out.println("Hasta luego.");
                        break;
                    }

                    case 4: {
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

                    case 5:
                    case 6: {
                        // Registrar paciente en sala de espera.
                        String patientId = read(sc, "ID del paciente (Ej: P001): ");

                        int level = Integer.parseInt(read(sc,
                                "Emergencia (0 normal, 1-3 emergencia): "));

                        boolean added = waiting.checkIn(patientId, level);

                        System.out.println(added ?
                                "Paciente en espera" :
                                "Paciente ya estaba en espera");
                        break;
                    }

                    case 7: {
                        // Programar cita.
                        String patientId = read(sc, "ID del paciente (Ej: P001): ");
                        String doctorId = read(sc, "ID del medico (Ej: D001): ");

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

                    case 8: {
                        // Llamar al proximo paciente.
                        String doctorId = read(sc, "ID del medico (Ej: D001): ");

                        String patientId = waiting.callNext(doctorId);

                        System.out.println(patientId == null ?
                                "No hay pacientes esperando" :
                                "Paciente llamado: " + patientId);
                        break;
                    }

                    case 9: {
                        // Registrar signos vitales y finalizar visita.
                        String patientId = read(sc, "ID del paciente (Ej: P001): ");
                        String doctorId = read(sc, "ID del medico (Ej: D001): ");
                        String nurseId = read(sc, "ID de la enfermera (Ej: N001): ");
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

                    case 10: {
                        // Cancelar cita.
                        String id = read(sc, "ID de la cita (Ej: C001): ");

                        boolean cancelled =
                                appointments.cancelAppointment(id);

                        System.out.println(cancelled ?
                                "Cita cancelada" :
                                "No se pudo cancelar la cita");
                        break;
                    }

                    case 11: {
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

                    case 12: {
                        // Mostrar pacientes esperando.
                        System.out.println(
                                "En espera: " + waiting.listWaiting());
                        break;
                    }

                    case 13: {
                        // Mostrar historial de paciente.
                        String id = read(sc, "ID del paciente (Ej: P001): ");

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

                    case 14: {
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

                    case 15: {
                        // Mostrar pacientes asociados a un medico.
                        String id = read(sc, "ID del medico (Ej: D001): ");

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
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero.");
            }
        }

        sc.close();
    }
}