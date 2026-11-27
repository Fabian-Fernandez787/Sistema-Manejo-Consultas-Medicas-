package clinic.waiting;

import clinic.common.Types;
import clinic.common.Interfaces;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

public class WaitingRoomServiceImpl implements Interfaces.IWaitingRoomModule {
    // Guarda los datos de un paciente de emergencia.
    private static class Emergency {
        String patientId;
        int level;
        long arrivalOrder;

        Emergency(String patientId, int level, long arrivalOrder) {
            this.patientId = patientId;
            this.level = level;
            this.arrivalOrder = arrivalOrder;
        }
    }

    private Interfaces.IRegistryModule registry;
    private Interfaces.IAppointmentsModule appointments;

    private Queue<String> normalQueue = new LinkedList<>();

    private long arrivalCounter = 0;

    // Ordena emergencias por nivel y orden de llegada.
    private PriorityQueue<Emergency> emergencyQueue =
        new PriorityQueue<>(
            Comparator.comparingInt((Emergency e) -> e.level)
                      .thenComparingLong(e -> e.arrivalOrder)
        );

    private HashSet<String> waitingIds = new HashSet<>();
    private HashSet<String> activeVisitIds = new HashSet<>();
    private HashMap<String, String> patientVitals = new HashMap<>();
    private HashMap<String, String> patientNurses = new HashMap<>();
    private HashMap<String, String> patientDoctors = new HashMap<>();

    public WaitingRoomServiceImpl(Interfaces.IRegistryModule registry, Interfaces.IAppointmentsModule appointments) {

    // Verificar que los modulos existan
    if (registry == null || appointments == null) {
        throw new IllegalArgumentException("Modulos invalidos");
    }

    this.registry = registry;
    this.appointments = appointments;
}

    // Registra pacientes en la sala de espera.
    @Override
    public boolean checkIn(String patientId, int emergencyLevel) {

        // Verificar que el ID sea valido
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("ID de paciente invalido");
        }

        //Verificar que el nivel sea valido
        if(emergencyLevel < 0 || emergencyLevel > 3) {
            throw new IllegalArgumentException("Nivel de emrgencia invalido");
        }

        //Verificar que el paciente exista
        if(registry.getPatient(patientId) == null) {
            throw new IllegalArgumentException("Paciente no registrado");
        }
        
        // Evitar pacientes duplicados o en consulta
        if(waitingIds.contains(patientId) || activeVisitIds.contains(patientId)) {
            return false;
        }

        //Añadir pacientes normales a la fila
        if(emergencyLevel == 0) {
            normalQueue.add(patientId);
            waitingIds.add(patientId);
            return true;
        }

        // Añadir pacientes de emergencia por prioridad
        emergencyQueue.add(new Emergency(patientId, emergencyLevel, arrivalCounter++));
        waitingIds.add(patientId);

        return true;

    }
    
    // Muestra los pacientes en orden de prioridad sin eliminarlos.
    @Override
    public List<String> listWaiting() {

        List<String> result = new ArrayList<>();

        // Copiar las emergencias para no modificar la fila original
        PriorityQueue<Emergency> copy = new PriorityQueue<>(emergencyQueue);

        // Agregar emergencias en orden de prioridad
        while(!copy.isEmpty()) {
            result.add(copy.poll().patientId);
        }

        // Agregar pacientes normales al final
        result.addAll(normalQueue);

        return result;
    }


    //Asigna el proximo paciente a un medico disponible
    @Override
    public String callNext(String doctorId) {

        //Verificar que el medico exista
        Types.Employee employee = registry.getEmployee(doctorId);

        if(!(employee instanceof Types.Doctor)) {
            throw new IllegalArgumentException("Medico no encontrado");
        }

        Types.Doctor doctor = (Types.Doctor) employee;

        //Verificar que el medico este disponible
        if(doctor.status != Types.DoctorStatus.AVAILABLE) {
            throw new IllegalArgumentException("Medico no disponible");
        }

        // Verificar que haya pacientes esperando
        if(emergencyQueue.isEmpty() && normalQueue.isEmpty()) {
            return null;
        }

        // Atender primero a los pacientes de emergencia
        String patientId;

        if(!emergencyQueue.isEmpty()) {
            patientId = emergencyQueue.poll().patientId;
        } else {
            patientId = normalQueue.poll();
        }

        //Eliminar al paciente de la lista de espera
        waitingIds.remove(patientId);
        activeVisitIds.add(patientId);
        patientDoctors.put(patientId, doctorId);
        registry.linkPatientToDoctor(doctorId, patientId);

        //Cambiar el estado del medico a ocupado
        registry.setDoctorStatus(doctorId, Types.DoctorStatus.BUSY);

        return patientId;
    }

    //Registra los signos vitales del paciente
    @Override
    public void recordVitals(String patientId, String nurseId, String vitals) {
    
        //Verificar que la enfermera exista
        Types.Employee nurse = registry.getEmployee(nurseId);

        if(nurse == null){
            throw new IllegalArgumentException("Enfermera no encontrada");
        }

        //Verificar que tenga el rol NURSE
        if(nurse.role != Types.Role.NURSE){
            throw new IllegalArgumentException("El empleado no es enfermera");
        }

        //Verificar que el paciente este en consulta
        if(!activeVisitIds.contains(patientId)){
            throw new IllegalArgumentException("Paciente no esta en consulta");
        }

        if(vitals == null || vitals.isBlank()){
            throw new IllegalArgumentException("Signos vitales invalidos");
        }

        //Guardar los signos vitales y la enfermera
        patientVitals.put(patientId, vitals);
        patientNurses.put(patientId, nurseId);
    }

    //Finaliza la consulta, guarda la visita y libera al medico
    @Override
    public Types.Visit finishVisit(String patientId, String doctorId, String nurseId, String notes) {
        
        // Verificar que los datos de la visita sean validos
        if (patientId == null || patientId.isBlank() || doctorId == null || doctorId.isBlank() || nurseId == null || nurseId.isBlank()) {
            throw new IllegalArgumentException("Datos de visita incompletos");
}
        //Verificar que el paciente este con ese medico
        if(!activeVisitIds.contains(patientId) || !doctorId.equals(patientDoctors.get(patientId))) {
            throw new IllegalArgumentException("Paciente no esta con este medico");
        }

        //Verificar que tenga signos vitales
        if(!patientVitals.containsKey(patientId)){
            throw new IllegalArgumentException("Signos vitales no registrados");
        }

        //Verificar que sea la enfermera registrada
        if(!nurseId.equals(patientNurses.get(patientId))) {
            throw new IllegalArgumentException("Enfermera incorrecta");
        }

        //Crear la visita medica
        Types.Visit visit = new Types.Visit();
        visit.patientId = patientId;
        visit.doctorId = doctorId;
        visit.nurseId = nurseId;
        visit.timestamp = java.time.LocalDateTime.now().toString();
        visit.vitals = patientVitals.get(patientId);
        visit.notes = notes;

        //Guardar la visita en el historial
        appointments.addVisit(visit);

        //Liberar al medico
        registry.setDoctorStatus(doctorId, Types.DoctorStatus.AVAILABLE);

        //Limpiar los datos de la consulta
        activeVisitIds.remove(patientId);
        patientDoctors.remove(patientId);
        patientVitals.remove(patientId);
        patientNurses.remove(patientId);

        return visit;
    }
}
