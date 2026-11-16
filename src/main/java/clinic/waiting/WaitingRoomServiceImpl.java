package clinic.waiting;

import clinic.common.Types;
import clinic.common.Interfaces;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;

public class WaitingRoomServiceImpl implements Interfaces.IWaitingRoomModule {

    private Interfaces.IRegistryModule registry;
    private Interfaces.IAppointmentsModule appointments;

    private Queue<String> normalQueue = new LinkedList<>();
    private HashSet<String> waitingIds = new HashSet<>();

    public WaitingRoomServiceImpl(Interfaces.IRegistryModule registry, Interfaces.IAppointmentsModule appointments) {

        this.registry = registry;
        this.appointments = appointments;
    }

    // Registra pacientes en la sala de espera.
    @Override
    public boolean checkIn(String patientId, int emergencyLevel) {
        
        //Verificar que el nivel sea valido
        if(emergencyLevel < 0 || emergencyLevel > 3) {
            throw new IllegalArgumentException("Nivel de emrgencia invalido");
        }

        //Verificar que el paciente exista
        if(registry.getPatient(patientId) == null) {
            throw new IllegalArgumentException("Paciente no registrado");
        }

        //Evitar pacientes duplicados
        if(waitingIds.contains(patientId)) {
            return false;
        }

        //Añadir pacientes normales a la fila
        if(emergencyLevel == 0) {
            normalQueue.add(patientId);
            waitingIds.add(patientId);
            return true;
        }

        //Manejo de emergencias pendiente
        throw new UnsupportedOperationException("Emergencias pendientes");
    }

    // Muestra los pacientes en orden de llegada sin eliminarlos.
    @Override
    public List<String> listWaiting() {
        return new ArrayList<>(normalQueue);
    }

    
    @Override
    public String callNext(String doctorId) {
        return null;
    }

    
    @Override
    public void recordVitals(String patientId, String nurseId, String vitals) {
    }

    
    @Override
    public Types.Visit finishVisit(String patientId, String doctorId, String nurseId, String notes) {
        return null;
    }
}
